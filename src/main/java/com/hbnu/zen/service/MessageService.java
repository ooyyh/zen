package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.mapper.MessageMapper;
import com.hbnu.zen.mapper.MessageTemplateMapper;
import com.hbnu.zen.mybatis.entity.Message;
import com.hbnu.zen.mybatis.entity.MessageTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {
    private final MessageMapper messageMapper;
    private final MessageTemplateMapper templateMapper;

    public MessageService(MessageMapper messageMapper, MessageTemplateMapper templateMapper) {
        this.messageMapper = messageMapper;
        this.templateMapper = templateMapper;
    }

    public void sendTemplate(Long userId, String templateCode, Map<String, String> params) {
        MessageTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "消息模板不存在");
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
            throw new BusinessException(ErrorCode.NOT_FOUND, "消息不存在");
        }
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限操作该消息");
        }
        messageMapper.markRead(messageId, LocalDateTime.now());
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
}
