package com.cqrs.command.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cqrs.command.model.entity.GadgetEntity;
import com.cqrs.command.model.entity.StoreEntity;
import com.cqrs.command.repository.GadgetRepository;
import com.cqrs.command.repository.StoreRepository;
import com.cqrs.dto.event.store.GadgetState;
import com.cqrs.dto.event.store.StoreCreatedEvent;
import com.cqrs.dto.event.store.StoreNameUpdatedEvent;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreEventHandlerTest {

  private static final String STORE_ID = "store-1";

  @Mock private StoreRepository storeRepository;

  @Mock private GadgetRepository gadgetRepository;

  @InjectMocks private StoreEventHandler storeEventHandler;

  @Test
  void givenStoreCreatedEvent_whenHandled_thenSavesStoreAndGadgets() throws Exception {
    // given
    final StoreCreatedEvent event =
        StoreCreatedEvent.builder()
            .id(STORE_ID)
            .name("Shop")
            .year(2020)
            .country("PL")
            .gadgets(
                List.of(
                    GadgetState.builder()
                        .id("gadget-1")
                        .name("phone")
                        .type("mobile")
                        .color("black")
                        .build()))
            .build();

    // when
    this.storeEventHandler.on(event);

    // then
    verify(this.storeRepository)
        .save(StoreEntity.builder().id(STORE_ID).name("Shop").year(2020).country("PL").build());
    verify(this.gadgetRepository)
        .save(
            GadgetEntity.builder()
                .id("gadget-1")
                .name("phone")
                .type("mobile")
                .color("black")
                .storeId(STORE_ID)
                .build());
  }

  @Test
  void givenStoreNameUpdatedEventAndStoreExists_whenHandled_thenSavesStoreWithNewName()
      throws Exception {
    // given
    final StoreEntity existing =
        StoreEntity.builder().id(STORE_ID).name("Shop").year(2020).country("PL").build();
    when(this.storeRepository.findById(STORE_ID)).thenReturn(Optional.of(existing));

    // when
    this.storeEventHandler.on(
        StoreNameUpdatedEvent.builder().id(STORE_ID).name("Renamed Shop").build());

    // then
    verify(this.storeRepository)
        .save(
            StoreEntity.builder()
                .id(STORE_ID)
                .name("Renamed Shop")
                .year(2020)
                .country("PL")
                .build());
  }

  @Test
  void givenStoreNameUpdatedEventAndStoreMissing_whenHandled_thenDoesNotSave() throws Exception {
    // given
    when(this.storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

    // when
    this.storeEventHandler.on(
        StoreNameUpdatedEvent.builder().id(STORE_ID).name("Renamed Shop").build());

    // then
    verify(this.storeRepository, never()).save(any());
  }
}
