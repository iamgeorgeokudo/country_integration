package com.ncba.countryintegration.controller;

import com.ncba.countryintegration.dto.CountryResponse;
import com.ncba.countryintegration.dto.LanguageResponse;
import com.ncba.countryintegration.exception.GlobalExceptionHandler;
import com.ncba.countryintegration.exception.ResourceNotFoundException;
import com.ncba.countryintegration.service.CountryIntegrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
@Import(GlobalExceptionHandler.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CountryIntegrationService countryIntegrationService;

    private CountryResponse sampleCountry() {
        return new CountryResponse(
                1L,
                "Tanzania",
                "TZ",
                "Dodoma",
                "255",
                "AF",
                "TZS",
                "https://example.com/tanzania.jpg",
                List.of(new LanguageResponse("Swahili", "swa"))
        );
    }

    @Test
    void getCountryByIdReturnsCountryAnd200() throws Exception {
        when(countryIntegrationService.getCountryById(1L))
                .thenReturn(sampleCountry());

        mockMvc.perform(get("/api/v1/countries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Tanzania"))
                .andExpect(jsonPath("$.isoCode").value("TZ"));
    }

    @Test
    void getMissingCountryReturns404() throws Exception {
        when(countryIntegrationService.getCountryById(999L))
                .thenThrow(new ResourceNotFoundException(
                        "Country not found with id: 999"
                ));

        mockMvc.perform(get("/api/v1/countries/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Country not found with id: 999"));
    }

    @Test
    void createCountryWithEmptyNameReturns400() throws Exception {
        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": ""}
                                """))
                .andExpect(status().isBadRequest());

        verify(countryIntegrationService, never()).createCountry(any());
    }

    @Test
    void createCountryReturns201() throws Exception {
        when(countryIntegrationService.createCountry(any()))
                .thenReturn(sampleCountry());

        mockMvc.perform(post("/api/v1/countries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Tanzania"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tanzania"))
                .andExpect(jsonPath("$.isoCode").value("TZ"));
    }

    @Test
    void deleteCountryReturns204() throws Exception {
        doNothing().when(countryIntegrationService).deleteCountry(1L);

        mockMvc.perform(delete("/api/v1/countries/1"))
                .andExpect(status().isNoContent());

        verify(countryIntegrationService).deleteCountry(1L);
    }

    @Test
    void updateCountryReturns200() throws Exception {
        when(countryIntegrationService.updateCountry(eq(1L), any()))
                .thenReturn(new CountryResponse(
                        1L,
                        "Uganda",
                        "UG",
                        "Kampala",
                        "256",
                        "AF",
                        "UGX",
                        "https://example.com/uganda.jpg",
                        List.of(new LanguageResponse("English", "eng"))
                ));

        mockMvc.perform(put("/api/v1/countries/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Uganda"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Uganda"))
                .andExpect(jsonPath("$.isoCode").value("UG"));
    }
}