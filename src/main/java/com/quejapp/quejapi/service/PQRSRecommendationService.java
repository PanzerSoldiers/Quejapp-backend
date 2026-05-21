package com.quejapp.quejapi.service;

import com.quejapp.quejapi.dto.PriorityComplaintDTO;
import com.quejapp.quejapi.model.Complaint;
import com.quejapp.quejapi.repository.ComplaintRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PQRSRecommendationService {

    private final ComplaintRepository complaintRepository;

    public List<PriorityComplaintDTO> getPrioritizedComplaints() {

        List<Complaint> complaints = complaintRepository.findAll();

        return complaints.stream()

                // Solo abiertas
                .filter(c -> c.getStatus() != null && c.getStatus() != 2 && c.getStatus() != 3)

                .map(c -> {

                    int score = calculatePriority(c);

                    return PriorityComplaintDTO.builder()
                            .id(c.getId())
                            .reference(c.getReference())
                            .subject(c.getSubject())
                            .priorityScore(score)
                            .priorityLevel(getPriorityLevel(score))
                            .reason(generateReason(c, score))
                            .status(c.getStatus())
                            .category(c.getCategory())
                            .type(c.getType())
                            .daysOpen(calculateDaysOpen(c))
                            .build();
                })

                .sorted(Comparator
                        .comparing(PriorityComplaintDTO::getPriorityScore)
                        .reversed())

                .collect(Collectors.toList());
    }

    private int calculatePriority(Complaint c) {

        int score = 0;

        int days = calculateDaysOpen(c);

        // Muchos días abierta
        score += days * 2;

        // Categorías críticas
        if (c.getCategory() != null) {

            switch (c.getCategory()) {

                case 7: // Seguridad
                    score += 40;
                    break;

                case 6: // Accesibilidad
                    score += 25;
                    break;

                case 5: // Atención cliente
                    score += 20;
                    break;
            }
        }

        // Reclamos pesan más
        if (c.getType() != null) {

            switch (c.getType()) {

                case 2: // Reclamo
                    score += 30;
                    break;

                case 1: // Queja
                    score += 20;
                    break;
            }
        }

        // Próxima a vencer
        if (c.getDaysfromExpiration() != null) {

            if (c.getDaysfromExpiration() <= 3) {
                score += 50;
            }
        }

        return score;
    }

    private int calculateDaysOpen(Complaint complaint) {

        if (complaint.getRecievedDate() == null) {
            return 0;
        }

        LocalDate received = complaint.getRecievedDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        return (int) ChronoUnit.DAYS.between(received, LocalDate.now());
    }

    private String getPriorityLevel(int score) {

        if (score >= 80) {
            return "ALTA";
        }

        if (score >= 40) {
            return "MEDIA";
        }

        return "BAJA";
    }

    private String generateReason(Complaint c, int score) {

        if (score >= 80) {
            return "Debe atenderse inmediatamente";
        }

        if (score >= 40) {
            return "Requiere seguimiento prioritario";
        }

        return "Puede atenderse posteriormente";
    }
}