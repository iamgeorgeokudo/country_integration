package com.ncba.countryintegration.repository;

import com.ncba.countryintegration.entity.CountryInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryInfoRepository extends JpaRepository<CountryInfo, Long> {

    Optional<CountryInfo> findByNameIgnoreCase(String name);

    Optional<CountryInfo> findByIsoCodeIgnoreCase(String isoCode);
}