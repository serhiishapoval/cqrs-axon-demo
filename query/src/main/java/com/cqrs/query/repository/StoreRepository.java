package com.cqrs.query.repository;

import com.cqrs.query.model.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<StoreEntity, String> {}
