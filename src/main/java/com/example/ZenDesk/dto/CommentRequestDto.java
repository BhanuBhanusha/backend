package com.example.ZenDesk.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequestDto {
    
    @NotBlank
    private String content;

    private boolean isInternal;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isIsInternal() {
        return isInternal;
    }

    public void setIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }
}
