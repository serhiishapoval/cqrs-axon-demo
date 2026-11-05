package com.cqrs.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.cqrs.query.model.entity.GadgetEntity;
import com.cqrs.query.model.entity.StoreEntity;
import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.StoreResponse;
import com.cqrs.query.repository.GadgetRepository;
import com.cqrs.query.repository.StoreRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {

  private static final String STORE_ID = "store-1";

  @Mock private StoreRepository storeRepository;

  @Mock private GadgetRepository gadgetRepository;

  @InjectMocks private StoreServiceImpl storeService;

  @Test
  void givenExistingStore_whenGetStore_thenReturnsStoreWithItsGadgets() {
    // given
    when(this.storeRepository.findById(STORE_ID))
        .thenReturn(
            Optional.of(
                StoreEntity.builder().id(STORE_ID).name("Shop").year(2020).country("PL").build()));
    when(this.gadgetRepository.findAllByStoreId(STORE_ID))
        .thenReturn(
            List.of(
                GadgetEntity.builder()
                    .id("gadget-1")
                    .name("phone")
                    .type("mobile")
                    .color("black")
                    .storeId(STORE_ID)
                    .build()));

    // when
    final StoreResponse result = this.storeService.getStore(STORE_ID);

    // then
    final StoreResponse expected =
        StoreResponse.builder()
            .id(STORE_ID)
            .name("Shop")
            .year(2020)
            .country("PL")
            .gadgets(
                List.of(
                    GadgetResponse.builder()
                        .id("gadget-1")
                        .name("phone")
                        .type("mobile")
                        .color("black")
                        .build()))
            .build();
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void givenMissingStore_whenGetStore_thenReturnsEmptyResponse() {
    // given
    when(this.storeRepository.findById(STORE_ID)).thenReturn(Optional.empty());

    // when
    final StoreResponse result = this.storeService.getStore(STORE_ID);

    // then
    assertThat(result).isEqualTo(StoreResponse.builder().build());
  }
}
