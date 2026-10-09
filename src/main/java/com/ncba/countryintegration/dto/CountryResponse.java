package com.ncba.countryintegration.dto;

import java.util.List;

public record CountryResponse(
        Long id,
        String name,
        String isoCode,
        String capital,
        String phoneCode,
        String continent,
        String currency,
        String countryFlag,
        List<LanguageResponse> languages
) {
}