package com.ncba.countryintegration.dto;

import jakarta.validation.constraints.NotBlank;

public record CountryRequest(
        @NotBlank(message = "Country name is required")
        String name
) {
}