package com.cqrs.query.repository;

import com.cqrs.query.model.entity.GadgetToCountryEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GadgetToCountryRepository extends JpaRepository<GadgetToCountryEntity, String> {

  List<GadgetToCountryEntity> findAllByCountry(String country);
}
