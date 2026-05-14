package com.quejapp.quejapi.controller;

import com.quejapp.quejapi.dto.PQRSStatisticsDTO;
import com.quejapp.quejapi.model.Complaint;
import com.quejapp.quejapi.repository.ComplaintRepository;
import com.quejapp.quejapi.service.PQRSStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/stats")
@RequiredArgsConstructor
public class PQRSStatisticsController {

    private final PQRSStatisticsService statisticsService;

    @GetMapping("/overview")
    public ResponseEntity<PQRSStatisticsDTO> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        PQRSStatisticsDTO statistics = statisticsService.getStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @RestController
    @RequestMapping("/api/reportes")
    @CrossOrigin(origins = "*")
    public class ReporteController {

        private final ComplaintRepository complaintRepository;

        public ReporteController(ComplaintRepository complaintRepository) {
            this.complaintRepository = complaintRepository;
        }

        @GetMapping
        public List<Complaint> obtenerTodas() {
            return complaintRepository.findAll();
        }
    }

}


