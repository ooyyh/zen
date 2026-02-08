package com.hbnu.zen.service;

import com.hbnu.zen.common.BusinessException;
import com.hbnu.zen.common.ErrorCode;
import com.hbnu.zen.mapper.MessageTemplateMapper;
import com.hbnu.zen.mybatis.entity.MessageTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageTemplateService {
    private final MessageTemplateMapper templateMapper;

    public MessageTemplateService(MessageTemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    public List<MessageTemplate> list() {
        return templateMapper.selectAll();
    }

    public MessageTemplate create(MessageTemplate template) {
        MessageTemplate existing = templateMapper.selectByCode(template.getTemplateCode());
        if (existing != null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "模板编码已存在");
        }
        templateMapper.insert(template);
        return templateMapper.selectById(template.getId());
    }

    public MessageTemplate update(Long id, MessageTemplate template) {
        MessageTemplate existing = templateMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "模板不存在");
        }
        template.setId(id);
        template.setTemplateCode(existing.getTemplateCode());
        templateMapper.update(template);
        return templateMapper.selectById(id);
    }
}
