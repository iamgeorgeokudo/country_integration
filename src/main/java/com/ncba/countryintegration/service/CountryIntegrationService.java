package com.ncba.countryintegration.service;

import com.ncba.countryintegration.dto.CountryRequest;
import com.ncba.countryintegration.dto.CountryResponse;
import com.ncba.countryintegration.dto.LanguageResponse;
import com.ncba.countryintegration.entity.CountryInfo;
import com.ncba.countryintegration.entity.Language;
import com.ncba.countryintegration.exception.ResourceNotFoundException;
import com.ncba.countryintegration.repository.CountryInfoRepository;
import com.ncba.countryintegration.service.soap.CountrySoapClient;
import com.ncba.countryintegration.soap.generated.TCountryInfo;
import com.ncba.countryintegration.soap.generated.TLanguage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CountryIntegrationService {

    private final CountryInfoRepository countryInfoRepository;
    private final CountrySoapClient countrySoapClient;

    public CountryIntegrationService(
            CountryInfoRepository countryInfoRepository,
            CountrySoapClient countrySoapClient
    ) {
        this.countryInfoRepository = countryInfoRepository;
        this.countrySoapClient = countrySoapClient;
    }

    @Transactional
    public CountryResponse createCountry(CountryRequest request) {

        String countryName = toSentenceCase(request.name());

        String isoCode = countrySoapClient.getCountryIsoCode(countryName);

        TCountryInfo soapCountryInfo =
                countrySoapClient.getFullCountryInfo(isoCode);

        CountryInfo countryInfo = mapToEntity(soapCountryInfo);

        countryInfo.setName(countryName);

        CountryInfo savedCountry =
                countryInfoRepository.save(countryInfo);

        return mapToResponse(savedCountry);
    }

    @Transactional(readOnly = true)
    public List<CountryResponse> getAllCountries() {
        return countryInfoRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CountryResponse getCountryById(Long id) {
        CountryInfo countryInfo = countryInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country not found with id: " + id
                ));

        return mapToResponse(countryInfo);
    }

    @Transactional
    public CountryResponse updateCountry(Long id, CountryRequest request) {
        CountryInfo existingCountry = countryInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country not found with id: " + id
                ));

        String countryName = toSentenceCase(request.name());

        String isoCode = countrySoapClient.getCountryIsoCode(countryName);

        TCountryInfo soapCountryInfo = countrySoapClient.getFullCountryInfo(isoCode);

        CountryInfo updatedCountry = mapToEntity(soapCountryInfo);
        updatedCountry.setId(existingCountry.getId());
        updatedCountry.setName(countryName);

        CountryInfo savedCountry = countryInfoRepository.save(updatedCountry);

        return mapToResponse(savedCountry);
    }

    @Transactional
    public void deleteCountry(Long id) {
        CountryInfo countryInfo = countryInfoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Country not found with id: " + id
                ));

        countryInfoRepository.delete(countryInfo);
    }
    private CountryInfo mapToEntity(TCountryInfo soapCountryInfo) {

        CountryInfo countryInfo = new CountryInfo();

        countryInfo.setIsoCode(soapCountryInfo.getSISOCode());
        countryInfo.setCapital(soapCountryInfo.getSCapitalCity());
        countryInfo.setPhoneCode(soapCountryInfo.getSPhoneCode());
        countryInfo.setContinent(soapCountryInfo.getSContinentCode());
        countryInfo.setCurrency(soapCountryInfo.getSCurrencyISOCode());
        countryInfo.setCountryFlag(soapCountryInfo.getSCountryFlag());

        if (soapCountryInfo.getLanguages() != null) {

            List<Language> languages =
                    soapCountryInfo.getLanguages()
                            .getTLanguage()
                            .stream()
                            .map(language -> mapLanguage(language, countryInfo))
                            .toList();

            countryInfo.setLanguages(languages);
        }

        return countryInfo;
    }

    private Language mapLanguage(
            TLanguage soapLanguage,
            CountryInfo countryInfo
    ) {
        Language language = new Language();

        language.setName(soapLanguage.getSName());
        language.setIsoCode(soapLanguage.getSISOCode());
        language.setCountry(countryInfo);

        return language;
    }

    private CountryResponse mapToResponse(CountryInfo countryInfo) {

        List<LanguageResponse> languages =
                countryInfo.getLanguages()
                        .stream()
                        .map(language ->
                                new LanguageResponse(
                                        language.getName(),
                                        language.getIsoCode()
                                )
                        )
                        .toList();

        return new CountryResponse(
                countryInfo.getId(),
                countryInfo.getName(),
                countryInfo.getIsoCode(),
                countryInfo.getCapital(),
                countryInfo.getPhoneCode(),
                countryInfo.getContinent(),
                countryInfo.getCurrency(),
                countryInfo.getCountryFlag(),
                languages
        );
    }

    private String toSentenceCase(String value) {

        String trimmed = value.trim();

        if (trimmed.isEmpty()) {
            return trimmed;
        }

        return Character.toUpperCase(trimmed.charAt(0))
                + trimmed.substring(1).toLowerCase();
    }
}