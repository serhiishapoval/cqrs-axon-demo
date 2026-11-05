package com.cqrs.query.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cqrs.dto.event.store.GadgetState;
import com.cqrs.dto.event.store.StoreCreatedEvent;
import com.cqrs.dto.event.store.StoreNameUpdatedEvent;
import com.cqrs.query.model.entity.GadgetEntity;
import com.cqrs.query.model.entity.GadgetToCountryEntity;
import com.cqrs.query.model.entity.StoreEntity;
import com.cqrs.query.model.entity.StoreIdToGadgetEntity;
import com.cqrs.query.repository.GadgetRepository;
import com.cqrs.query.repository.GadgetToCountryRepository;
import com.cqrs.query.repository.StoreIdToGadgetRepository;
import com.cqrs.query.repository.StoreRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreEventHandlerTest {

  private static final String STORE_ID = "store-1";

  @Mock private StoreRepository storeRepository;

  @Mock private GadgetRepository gadgetRepository;

  @Mock private StoreIdToGadgetRepository storeIdToGadgetRepository;

  @Mock private GadgetToCountryRepository gadgetToCountryRepository;

  @InjectMocks private StoreEventHandler storeEventHandler;

  private static StoreCreatedEvent storeCreatedEvent() {
    return StoreCreatedEvent.builder()
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
  }

  @Test
  void givenStoreCreatedEvent_whenHandled_thenSavesStoreEntity() {
    // given
    final StoreCreatedEvent event = storeCreatedEvent();

    // when
    this.storeEventHandler.on(event);

    // then
    verify(this.storeRepository)
        .save(StoreEntity.builder().id(STORE_ID).name("Shop").year(2020).country("PL").build());
  }

  @Test
  void givenStoreCreatedEvent_whenHandled_thenSavesGadgetEntities() {
    // given
    final StoreCreatedEvent event = storeCreatedEvent();
    final ArgumentCaptor<List<GadgetEntity>> captor = listCaptor();

    // when
    this.storeEventHandler.on(event);

    // then
    verify(this.gadgetRepository).saveAll(captor.capture());
    assertThat(captor.getValue())
        .isEqualTo(
            List.of(
                GadgetEntity.builder()
                    .id("gadget-1")
                    .name("phone")
                    .type("mobile")
                    .color("black")
                    .storeId(STORE_ID)
                    .build()));
  }

  @Test
  void givenStoreCreatedEvent_whenHandled_thenSavesStoreIdToGadgetRows() {
    // given
    final StoreCreatedEvent event = storeCreatedEvent();
    final ArgumentCaptor<List<StoreIdToGadgetEntity>> captor = listCaptor();

    // when
    this.storeEventHandler.on(event);

    // then
    verify(this.storeIdToGadgetRepository).saveAll(captor.capture());
    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            List.of(
                StoreIdToGadgetEntity.builder()
                    .storeId(STORE_ID)
                    .gadgetId("gadget-1")
                    .name("phone")
                    .type("mobile")
                    .color("black")
                    .build()));
  }

  @Test
  void givenStoreCreatedEvent_whenHandled_thenSavesGadgetToCountryRows() {
    // given
    final StoreCreatedEvent event = storeCreatedEvent();
    final ArgumentCaptor<List<GadgetToCountryEntity>> captor = listCaptor();

    // when
    this.storeEventHandler.on(event);

    // then
    verify(this.gadgetToCountryRepository).saveAll(captor.capture());
    assertThat(captor.getValue())
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(
            List.of(
                GadgetToCountryEntity.builder()
                    .country("PL")
                    .gadgetId("gadget-1")
                    .name("phone")
                    .type("mobile")
                    .color("black")
                    .build()));
  }

  @Test
  void givenStoreNameUpdatedEventAndStoreExists_whenHandled_thenSavesStoreWithNewName() {
    // given
    when(this.storeRepository.findById(STORE_ID))
        .thenReturn(
            Optional.of(
                StoreEntity.builder().id(STORE_ID).name("Shop").year(2020).country("PL").build()));

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
  void givenStoreNameUpdatedEventAndStoreMissing_whenHandled_thenDoesNotSave() {
    // given
    when(this.storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

    // when
    this.storeEventHandler.on(
        StoreNameUpdatedEvent.builder().id(STORE_ID).name("Renamed Shop").build());

    // then
    verify(this.storeRepository, never()).save(any());
  }

  @SuppressWarnings("unchecked")
  private static <T> ArgumentCaptor<List<T>> listCaptor() {
    return (ArgumentCaptor<List<T>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);
  }
}
