package com.ncba.countryintegration.service;

import com.ncba.countryintegration.dto.CountryRequest;
import com.ncba.countryintegration.dto.CountryResponse;
import com.ncba.countryintegration.entity.CountryInfo;
import com.ncba.countryintegration.exception.ResourceNotFoundException;
import com.ncba.countryintegration.repository.CountryInfoRepository;
import com.ncba.countryintegration.service.soap.CountrySoapClient;
import com.ncba.countryintegration.soap.generated.TCountryInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryIntegrationServiceTest {

    @Mock
    private CountryInfoRepository countryInfoRepository;

    @Mock
    private CountrySoapClient countrySoapClient;

    @InjectMocks
    private CountryIntegrationService countryIntegrationService;

    private CountryInfo tanzania;

    @BeforeEach
    void setUp() {
        tanzania = new CountryInfo();
        tanzania.setId(1L);
        tanzania.setName("Tanzania");
        tanzania.setIsoCode("TZ");
        tanzania.setCapital("Dodoma");
        tanzania.setPhoneCode("255");
        tanzania.setContinent("AF");
        tanzania.setCurrency("TZS");
    }

    @Test
    void getCountryByIdReturnsCountryWhenFound() {
        when(countryInfoRepository.findById(1L))
                .thenReturn(Optional.of(tanzania));

        CountryResponse response =
                countryIntegrationService.getCountryById(1L);

        assertEquals(1L, response.id());
        assertEquals("Tanzania", response.name());
        assertEquals("TZ", response.isoCode());

        verify(countryInfoRepository).findById(1L);
    }

    @Test
    void getCountryByIdThrowsWhenCountryDoesNotExist() {
        when(countryInfoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> countryIntegrationService.getCountryById(999L)
        );
    }

    @Test
    void deleteCountryDeletesExistingCountry() {
        when(countryInfoRepository.findById(1L))
                .thenReturn(Optional.of(tanzania));

        countryIntegrationService.deleteCountry(1L);

        verify(countryInfoRepository).delete(tanzania);
    }

    @Test
    void deleteCountryThrowsWhenCountryDoesNotExist() {
        when(countryInfoRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> countryIntegrationService.deleteCountry(999L)
        );

        verify(countryInfoRepository, never()).delete(any(CountryInfo.class));
    }

    @Test
    void createCountryNormalizesCountryNameAndSavesCountry() {
        TCountryInfo soapResponse = new TCountryInfo();
        soapResponse.setSISOCode("TZ");
        soapResponse.setSCapitalCity("Dodoma");
        soapResponse.setSPhoneCode("255");
        soapResponse.setSContinentCode("AF");
        soapResponse.setSCurrencyISOCode("TZS");

        when(countrySoapClient.getCountryIsoCode("Tanzania"))
                .thenReturn("TZ");
        when(countrySoapClient.getFullCountryInfo("TZ"))
                .thenReturn(soapResponse);
        when(countryInfoRepository.save(any(CountryInfo.class)))
                .thenAnswer(invocation -> {
                    CountryInfo country = invocation.getArgument(0);
                    country.setId(1L);
                    return country;
                });

        CountryResponse response =
                countryIntegrationService.createCountry(
                        new CountryRequest("  TANZANIA  ")
                );

        assertEquals("Tanzania", response.name());
        assertEquals("TZ", response.isoCode());
        assertEquals(1L, response.id());

        verify(countrySoapClient).getCountryIsoCode("Tanzania");
        verify(countrySoapClient).getFullCountryInfo("TZ");
        verify(countryInfoRepository).save(any(CountryInfo.class));
    }
}