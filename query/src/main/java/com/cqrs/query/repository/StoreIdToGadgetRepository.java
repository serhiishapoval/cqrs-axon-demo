package com.cqrs.query.repository;

import com.cqrs.query.model.entity.StoreIdToGadgetEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreIdToGadgetRepository extends JpaRepository<StoreIdToGadgetEntity, String> {

  List<StoreIdToGadgetEntity> findAllByStoreId(String storeId);
}
