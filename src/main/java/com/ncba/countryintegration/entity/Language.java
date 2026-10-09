package com.ncba.countryintegration.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "language")
public class Language {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "iso_code", length = 10)
    private String isoCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryInfo country;

    public Language() {
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

    public CountryInfo getCountry() {
        return country;
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

    public void setCountry(CountryInfo country) {
        this.country = country;
    }
}