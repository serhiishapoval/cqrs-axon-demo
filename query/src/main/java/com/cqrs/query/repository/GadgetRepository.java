package com.cqrs.query.repository;

import com.cqrs.query.model.entity.GadgetEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GadgetRepository extends JpaRepository<GadgetEntity, String> {
  List<GadgetEntity> findAllByStoreId(String storeId);
}
