package com.ncba.countryintegration.integration;

import com.ncba.countryintegration.entity.CountryInfo;
import com.ncba.countryintegration.repository.CountryInfoRepository;
import com.ncba.countryintegration.soap.generated.ArrayOftLanguage;
import com.ncba.countryintegration.soap.generated.CountryInfoServiceSoapType;
import com.ncba.countryintegration.soap.generated.TCountryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CountryIntegrationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryInfoRepository countryInfoRepository;

    @MockitoBean
    private CountryInfoServiceSoapType soapPort;

    @BeforeEach
    void setUp() {
        countryInfoRepository.deleteAll();
    }

    @Test
    void createCountryPersistsItAndReturnsItFromGetEndpoint() throws Exception {
        TCountryInfo soapCountry = new TCountryInfo();
        soapCountry.setSISOCode("TZ");
        soapCountry.setSName("Tanzania");
        soapCountry.setSCapitalCity("Dodoma");
        soapCountry.setSPhoneCode("255");
        soapCountry.setSContinentCode("AF");
        soapCountry.setSCurrencyISOCode("TZS");
        soapCountry.setSCountryFlag("https://example.com/tanzania.jpg");

        when(soapPort.countryISOCode(anyString()))
                .thenReturn("TZ");

        when(soapPort.fullCountryInfo("TZ"))
                .thenReturn(soapCountry);

        // Create a country through the REST API.
        String response = mockMvc.perform(
                        post("/api/v1/countries")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "name": "tanzania"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tanzania"))
                .andExpect(jsonPath("$.isoCode").value("TZ"))
                .andExpect(jsonPath("$.capital").value("Dodoma"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Confirm the country was persisted in the H2 database.
        CountryInfo savedCountry = countryInfoRepository
                .findByIsoCodeIgnoreCase("TZ")
                .orElseThrow();

        org.junit.jupiter.api.Assertions.assertEquals(
                "Tanzania",
                savedCountry.getName()
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                "Dodoma",
                savedCountry.getCapital()
        );

        // Retrieve the persisted country through the REST API.
        mockMvc.perform(get("/api/v1/countries/" + savedCountry.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tanzania"))
                .andExpect(jsonPath("$.isoCode").value("TZ"))
                .andExpect(jsonPath("$.capital").value("Dodoma"));
    }

    @Test
    void getUnknownCountryReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/countries/999999"))
                .andExpect(status().isNotFound());
    }
}