package com.hbnu.zen.dto;

import javax.validation.constraints.NotBlank;

public class MessageTemplateUpdateRequest {
    @NotBlank(message = "模板标题不能为空")
    private String title;
    @NotBlank(message = "模板内容不能为空")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
