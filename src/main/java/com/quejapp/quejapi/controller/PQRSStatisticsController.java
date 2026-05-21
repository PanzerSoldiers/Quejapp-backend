package com.quejapp.quejapi.controller;

import com.quejapp.quejapi.dto.PQRSStatisticsDTO;
import com.quejapp.quejapi.model.Complaint;
import com.quejapp.quejapi.model.User;
import com.quejapp.quejapi.repository.ComplaintRepository;
import com.quejapp.quejapi.repository.UserRepository;
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
        private final UserRepository userRepository;

        public ReporteController(ComplaintRepository complaintRepository, UserRepository userRepository) {
            this.complaintRepository = complaintRepository;
            this.userRepository = userRepository;
        }

        @GetMapping("/complaints")
        public List<Complaint> obtenerTodas() {
            return complaintRepository.findAll();
        }

        @GetMapping("/users")
        public List<User> obtenerUsuarios() {
            return userRepository.findAll();
        }
    }

}
/*
        @GetMapping
        public List<Complaint> obtenerTodas() {
            return complaintRepository.findAll();
        }*/

