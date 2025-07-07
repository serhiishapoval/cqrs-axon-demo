package com.cqrs.command.handler;

import com.cqrs.command.model.entity.GadgetEntity;
import com.cqrs.command.model.entity.StoreEntity;
import com.cqrs.command.repository.GadgetRepository;
import com.cqrs.command.repository.StoreRepository;
import com.cqrs.dto.event.store.StoreCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
@ProcessingGroup("store-group")
@RequiredArgsConstructor
@Slf4j
public class StoreEventHandler {

  private final StoreRepository storeRepository;

  private final GadgetRepository gadgetRepository;

  @EventHandler
  public void on(final StoreCreatedEvent storeCreatedEvent) throws Exception {
    this.insertStore(storeCreatedEvent);
    this.insertGadget(storeCreatedEvent);
  }

  private void insertStore(final StoreCreatedEvent storeCreatedEvent) {
    final StoreEntity storeEntity = StoreEntity.builder()
        .id(storeCreatedEvent.getId())
        .name(storeCreatedEvent.getName())
        .year(storeCreatedEvent.getYear())
        .country(storeCreatedEvent.getCountry())
        .build();
    this.storeRepository.save(storeEntity);
    log.info("insertStore. New store inserted. storeEntity: {}", storeEntity);
  }

  private void insertGadget(final StoreCreatedEvent storeCreatedEvent) {
    storeCreatedEvent.getGadgets().forEach(gadget -> {
      final GadgetEntity gadgetEntity = GadgetEntity.builder()
          .id(gadget.getId())
          .name(gadget.getName())
          .type(gadget.getType())
          .color(gadget.getColor())
          .storeId(storeCreatedEvent.getId())
          .build();
      this.gadgetRepository.save(gadgetEntity);
      log.info("insertGadget. New gadget inserted. gadgetEntity: {}", gadgetEntity);
    });
  }
}
