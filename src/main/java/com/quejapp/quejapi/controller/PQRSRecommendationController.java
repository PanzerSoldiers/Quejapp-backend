package com.quejapp.quejapi.controller;

import com.quejapp.quejapi.dto.PriorityComplaintDTO;
import com.quejapp.quejapi.service.PQRSRecommendationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class PQRSRecommendationController {

    private final PQRSRecommendationService recommendationService;

    @GetMapping("/priorities")
    public List<PriorityComplaintDTO> getPriorities() {
        return recommendationService.getPrioritizedComplaints();
    }
}