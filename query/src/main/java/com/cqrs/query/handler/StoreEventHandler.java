package com.cqrs.query.handler;

import com.cqrs.dto.event.store.StoreCreatedEvent;
import com.cqrs.query.model.entity.GadgetEntity;
import com.cqrs.query.model.entity.GadgetToCountryEntity;
import com.cqrs.query.model.entity.StoreEntity;
import com.cqrs.query.model.entity.StoreIdToGadgetEntity;
import com.cqrs.query.repository.GadgetRepository;
import com.cqrs.query.repository.GadgetToCountryRepository;
import com.cqrs.query.repository.StoreIdToGadgetRepository;
import com.cqrs.query.repository.StoreRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("query-store-group")
@RequiredArgsConstructor
@Slf4j
public class StoreEventHandler {

  private final StoreRepository storeRepository;
  private final GadgetRepository gadgetRepository;
  private final StoreIdToGadgetRepository storeIdToGadgetRepository;
  private final GadgetToCountryRepository gadgetToCountryRepository;

  @EventHandler
  public void on(final StoreCreatedEvent storeCreatedEvent) throws Exception {
    this.insertStore(storeCreatedEvent);
    this.insertGadgets(storeCreatedEvent);
    this.insertStoreIdToGadgets(storeCreatedEvent);
    this.insertGadgetToCountry(storeCreatedEvent);
  }

  private void insertStore(final StoreCreatedEvent storeCreatedEvent) {
    this.storeRepository.save(
        StoreEntity.builder()
            .id(storeCreatedEvent.getId())
            .name(storeCreatedEvent.getName())
            .year(storeCreatedEvent.getYear())
            .country(storeCreatedEvent.getCountry())
            .build());
  }

  private void insertGadgets(final StoreCreatedEvent storeCreatedEvent) {
    this.gadgetRepository.saveAll(
        storeCreatedEvent.getGadgets().stream()
            .map(
                gadgetState ->
                    GadgetEntity.builder()
                        .id(gadgetState.getId())
                        .name(gadgetState.getName())
                        .type(gadgetState.getType())
                        .color(gadgetState.getColor())
                        .storeId(storeCreatedEvent.getId())
                        .build())
            .toList());
  }

  private void insertStoreIdToGadgets(final StoreCreatedEvent storeCreatedEvent) {
    this.storeIdToGadgetRepository.saveAll(
        storeCreatedEvent.getGadgets().stream()
            .map(
                gadgetState ->
                    StoreIdToGadgetEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .storeId(storeCreatedEvent.getId())
                        .gadgetId(gadgetState.getId())
                        .name(gadgetState.getName())
                        .type(gadgetState.getType())
                        .color(gadgetState.getColor())
                        .build())
            .toList());
  }

  private void insertGadgetToCountry(final StoreCreatedEvent storeCreatedEvent) {
    this.gadgetToCountryRepository.saveAll(
        storeCreatedEvent.getGadgets().stream()
            .map(
                gadgetState ->
                    GadgetToCountryEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .country(storeCreatedEvent.getCountry())
                        .gadgetId(gadgetState.getId())
                        .name(gadgetState.getName())
                        .type(gadgetState.getType())
                        .color(gadgetState.getColor())
                        .build())
            .toList());
  }
}
