package org.opmile.securitytodo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,

        String title,

        String description,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
        LocalDateTime updatedAt,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dueDate,

        String status
) {
}
