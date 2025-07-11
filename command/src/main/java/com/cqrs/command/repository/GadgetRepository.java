package com.cqrs.command.repository;

import com.cqrs.command.model.entity.GadgetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GadgetRepository extends JpaRepository<GadgetEntity, String> {}
