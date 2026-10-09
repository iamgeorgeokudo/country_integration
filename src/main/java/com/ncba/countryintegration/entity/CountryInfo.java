package com.ncba.countryintegration.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "country_info",
        indexes = {
                @Index(name = "idx_country_iso_code", columnList = "iso_code"),
                @Index(name = "idx_country_name", columnList = "name")
        }
)
public class CountryInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "iso_code", nullable = false, unique = true, length = 10)
    private String isoCode;

    @Column(length = 100)
    private String capital;

    @Column(name = "phone_code", length = 20)
    private String phoneCode;

    @Column(length = 100)
    private String continent;

    @Column(length = 10)
    private String currency;

    @Column(name = "country_flag", length = 500)
    private String countryFlag;

    @OneToMany(
            mappedBy = "country",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Language> languages = new ArrayList<>();

    public CountryInfo() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getCapital() {
        return capital;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public String getContinent() {
        return continent;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountryFlag() {
        return countryFlag;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCountryFlag(String countryFlag) {
        this.countryFlag = countryFlag;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }
}