package com.quejapp.quejapi.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatActionDTO {

    private String action;

    private String entity;

    private String fileName;

    private List<String> headers;

    // ACTION
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    // ENTITY
    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    // FILE NAME
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // HEADERS
    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
}
