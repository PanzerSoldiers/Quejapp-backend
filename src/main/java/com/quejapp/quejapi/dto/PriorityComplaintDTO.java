package com.quejapp.quejapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriorityComplaintDTO {

    private String id;
    private String reference;
    private String subject;

    private Integer priorityScore;
    private String priorityLevel;
    private String reason;

    private Integer status;
    private Integer category;
    private Integer type;

    private Integer daysOpen;
}