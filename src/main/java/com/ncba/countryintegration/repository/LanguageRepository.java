package com.ncba.countryintegration.repository;

import com.ncba.countryintegration.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, Long> {
}