package com.ncba.countryintegration.service.soap;

import com.ncba.countryintegration.exception.SoapIntegrationException;
import com.ncba.countryintegration.soap.generated.CountryInfoServiceSoapType;
import com.ncba.countryintegration.soap.generated.TCountryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CountrySoapClient {

    private static final Logger log =
            LoggerFactory.getLogger(CountrySoapClient.class);

    private final CountryInfoServiceSoapType soapPort;

    public CountrySoapClient(CountryInfoServiceSoapType soapPort) {
        this.soapPort = soapPort;
    }

    public String getCountryIsoCode(String countryName) {
        try {
            log.info("Calling SOAP CountryISOCode for country={}", countryName);

            String isoCode = soapPort.countryISOCode(countryName);

            if (isoCode == null || isoCode.isBlank()) {
                throw new SoapIntegrationException(
                        "SOAP service returned no ISO code for country: " + countryName
                );
            }

            log.info(
                    "SOAP CountryISOCode successful: country={}, isoCode={}",
                    countryName,
                    isoCode
            );

            return isoCode.trim();

        } catch (SoapIntegrationException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error(
                    "SOAP CountryISOCode call failed for country={}",
                    countryName,
                    ex
            );

            throw new SoapIntegrationException(
                    "Failed to resolve country ISO code",
                    ex
            );
        }
    }

    public TCountryInfo getFullCountryInfo(String isoCode) {
        try {
            log.info(
                    "Calling SOAP FullCountryInfo for isoCode={}",
                    isoCode
            );

            TCountryInfo countryInfo = soapPort.fullCountryInfo(isoCode);

            if (countryInfo == null) {
                throw new SoapIntegrationException(
                        "SOAP service returned no country information for ISO code: "
                                + isoCode
                );
            }

            log.info(
                    "SOAP FullCountryInfo successful: isoCode={}",
                    isoCode
            );

            return countryInfo;

        } catch (SoapIntegrationException ex) {
            throw ex;

        } catch (Exception ex) {
            log.error(
                    "SOAP FullCountryInfo call failed for isoCode={}",
                    isoCode,
                    ex
            );

            throw new SoapIntegrationException(
                    "Failed to retrieve full country information",
                    ex
            );
        }
    }
}