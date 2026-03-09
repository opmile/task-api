package org.opmile.securitytodo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank
        String title,

        @NotBlank
        String description,

        @JsonFormat(pattern = "dd/MM/yyyy")
        @NotNull
        @FutureOrPresent
        LocalDate dueDate
) {}
