package com.ncba.countryintegration.config;

import com.ncba.countryintegration.service.soap.CountrySoapClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("soap-test")
public class SoapTestRunner implements CommandLineRunner {

    private final CountrySoapClient countrySoapClient;

    public SoapTestRunner(CountrySoapClient countrySoapClient) {
        this.countrySoapClient = countrySoapClient;
    }

    @Override
    public void run(String... args) {
        String countryName = "Tanzania";

        String isoCode =
                countrySoapClient.getCountryIsoCode(countryName);

        System.out.println("==========================================");
        System.out.println("Country: " + countryName);
        System.out.println("ISO Code: " + isoCode);

        var countryInfo =
                countrySoapClient.getFullCountryInfo(isoCode);

        System.out.println("Name: " + countryInfo.getSName());
        System.out.println("ISO Code: " + countryInfo.getSISOCode());
        System.out.println("Capital: " + countryInfo.getSCapitalCity());
        System.out.println("Phone Code: " + countryInfo.getSPhoneCode());
        System.out.println("Continent: " + countryInfo.getSContinentCode());
        System.out.println("Currency: " + countryInfo.getSCurrencyISOCode());

        if (countryInfo.getLanguages() != null) {
            System.out.println(
                    "Languages: "
                            + countryInfo.getLanguages().getTLanguage().size()
            );
        }

        System.out.println("==========================================");
    }
}