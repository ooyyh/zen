package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.common.Role;
import com.hbnu.zen.mapper.MessageMapper;
import com.hbnu.zen.mapper.MessageTemplateMapper;
import com.hbnu.zen.mapper.UserMapper;
import com.hbnu.zen.mybatis.entity.Message;
import com.hbnu.zen.mybatis.entity.MessageTemplate;
import com.hbnu.zen.mybatis.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    private final MessageMapper messageMapper;
    private final MessageTemplateMapper templateMapper;
    private final UserMapper userMapper;

    public MessageService(MessageMapper messageMapper,
                          MessageTemplateMapper templateMapper,
                          UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.templateMapper = templateMapper;
        this.userMapper = userMapper;
    }

    public void sendTemplate(Long userId, String templateCode, Map<String, String> params) {
        MessageTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "???????");
        }
        String title = fillTemplate(template.getTitle(), params);
        String content = fillTemplate(template.getContent(), params);
        Message message = new Message();
        message.setUserId(userId);
        message.setTemplateCode(templateCode);
        message.setTitle(title);
        message.setContent(content);
        message.setStatus("UNREAD");
        messageMapper.insert(message);
    }

    public List<Message> listByUser(Long userId) {
        return messageMapper.selectByUser(userId);
    }

    public void markRead(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "?????");
        }
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "????????");
        }
        messageMapper.markRead(messageId, LocalDateTime.now());
    }

    public int broadcast(String title, String content, String targetRole) {
        String role = normalizeRole(targetRole);
        List<User> users = userMapper.selectAll();
        int sent = 0;
        for (User user : users) {
            if (user.getStatus() == null || user.getStatus() == 0) {
                continue;
            }
            if (role != null && !role.equals(user.getRole())) {
                continue;
            }
            Message message = new Message();
            message.setUserId(user.getId());
            message.setTemplateCode("BROADCAST");
            message.setTitle(title);
            message.setContent(content);
            message.setStatus("UNREAD");
            messageMapper.insert(message);
            sent++;
        }
        return sent;
    }

    private String fillTemplate(String text, Map<String, String> params) {
        if (text == null || params == null) {
            return text;
        }
        String result = text;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = "{" + entry.getKey() + "}";
            result = result.replace(key, entry.getValue());
        }
        return result;
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty() || "ALL".equalsIgnoreCase(role)) {
            return null;
        }
        String normalized = role.trim().toUpperCase();
        if (!Role.ADMIN.equals(normalized) && !Role.TEACHER.equals(normalized) && !Role.STUDENT.equals(normalized)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "???????");
        }
        return normalized;
    }
}
