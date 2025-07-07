package com.cqrs.command.repository;

import com.cqrs.command.model.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<StoreEntity, String> {

}
