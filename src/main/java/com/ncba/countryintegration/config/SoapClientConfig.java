package com.ncba.countryintegration.config;

import com.ncba.countryintegration.soap.generated.CountryInfoService;
import com.ncba.countryintegration.soap.generated.CountryInfoServiceSoapType;
import jakarta.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URL;
import java.util.Map;

@Configuration
public class SoapClientConfig {

    @Value("${soap.country-info.url}")
    private String soapEndpoint;

    @Value("${soap.country-info.connect-timeout:5000}")
    private String connectTimeout;

    @Value("${soap.country-info.request-timeout:10000}")
    private String requestTimeout;

    @Bean
    public CountryInfoServiceSoapType countryInfoSoapClient() {
        URL wsdlLocation = CountryInfoService.class
                .getResource("/wsdl/CountryInfoService.wsdl");

        if (wsdlLocation == null) {
            throw new IllegalStateException(
                    "WSDL file not found on the application classpath: "
                            + "/wsdl/CountryInfoService.wsdl"
            );
        }

        CountryInfoService service = new CountryInfoService(wsdlLocation);
        CountryInfoServiceSoapType port =
                service.getCountryInfoServiceSoap();

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext =
                bindingProvider.getRequestContext();

        requestContext.put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                soapEndpoint
        );

        requestContext.put(
                "com.sun.xml.ws.connect.timeout",
                Integer.parseInt(connectTimeout)
        );

        requestContext.put(
                "com.sun.xml.ws.request.timeout",
                Integer.parseInt(requestTimeout)
        );

        return port;
    }
}