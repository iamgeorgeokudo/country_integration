package com.ncba.countryintegration.controller;

import com.ncba.countryintegration.dto.CountryRequest;
import com.ncba.countryintegration.dto.CountryResponse;
import com.ncba.countryintegration.service.CountryIntegrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
public class CountryController {

    private final CountryIntegrationService countryIntegrationService;

    public CountryController(
            CountryIntegrationService countryIntegrationService
    ) {
        this.countryIntegrationService = countryIntegrationService;
    }

    /**
     * Creates country information by retrieving data from the SOAP service
     * and persisting it in the database.
     */
    @PostMapping
    public ResponseEntity<CountryResponse> createCountry(
            @Valid @RequestBody CountryRequest request
    ) {
        CountryResponse response =
                countryIntegrationService.createCountry(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Retrieves all countries stored in the database.
     */
    @GetMapping
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        return ResponseEntity.ok(
                countryIntegrationService.getAllCountries()
        );
    }

    /**
     * Retrieves a country by its database ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> getCountryById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                countryIntegrationService.getCountryById(id)
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<CountryResponse> updateCountry(
            @PathVariable Long id,
            @Valid @RequestBody CountryRequest request) {

        return ResponseEntity.ok(
                countryIntegrationService.updateCountry(id, request)
        );
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCountry(@PathVariable Long id) {

        countryIntegrationService.deleteCountry(id);

        return ResponseEntity.noContent().build();
    }
}