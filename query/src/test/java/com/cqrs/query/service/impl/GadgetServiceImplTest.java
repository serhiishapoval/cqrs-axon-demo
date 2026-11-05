package com.cqrs.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.cqrs.query.model.entity.GadgetEntity;
import com.cqrs.query.model.entity.GadgetToCountryEntity;
import com.cqrs.query.model.entity.StoreIdToGadgetEntity;
import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.GadgetToCountryResponse;
import com.cqrs.query.model.rest.StoreIdToGadgetResponse;
import com.cqrs.query.repository.GadgetRepository;
import com.cqrs.query.repository.GadgetToCountryRepository;
import com.cqrs.query.repository.StoreIdToGadgetRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GadgetServiceImplTest {

  @Mock private GadgetRepository gadgetRepository;

  @Mock private GadgetToCountryRepository gadgetToCountryRepository;

  @Mock private StoreIdToGadgetRepository storeIdToGadgetRepository;

  @InjectMocks private GadgetServiceImpl gadgetService;

  @Test
  void givenExistingGadget_whenGetGadget_thenReturnsGadgetResponse() {
    // given
    when(this.gadgetRepository.findById("gadget-1"))
        .thenReturn(
            Optional.of(
                GadgetEntity.builder()
                    .id("gadget-1")
                    .name("phone")
                    .type("mobile")
                    .color("black")
                    .build()));

    // when
    final GadgetResponse result = this.gadgetService.getGadget("gadget-1");

    // then
    assertThat(result)
        .isEqualTo(
            GadgetResponse.builder()
                .id("gadget-1")
                .name("phone")
                .type("mobile")
                .color("black")
                .build());
  }

  @Test
  void givenMissingGadget_whenGetGadget_thenReturnsEmptyResponse() {
    // given
    when(this.gadgetRepository.findById("gadget-1")).thenReturn(Optional.empty());

    // when
    final GadgetResponse result = this.gadgetService.getGadget("gadget-1");

    // then
    assertThat(result).isEqualTo(GadgetResponse.builder().build());
  }

  @Test
  void givenCountry_whenGetGadgetsFromCountry_thenReturnsMappedGadgets() {
    // given
    when(this.gadgetToCountryRepository.findAllByCountry("PL"))
        .thenReturn(
            List.of(
                GadgetToCountryEntity.builder()
                    .id("row-1")
                    .country("PL")
                    .gadgetId("gadget-1")
                    .name("phone")
                    .type("mobile")
                    .color("black")
                    .build()));

    // when
    final List<GadgetToCountryResponse> result = this.gadgetService.getGadgetsFromCountry("PL");

    // then
    assertThat(result)
        .containsExactly(
            GadgetToCountryResponse.builder()
                .country("PL")
                .gadgetId("gadget-1")
                .name("phone")
                .type("mobile")
                .color("black")
                .build());
  }

  @Test
  void givenStoreWithGadgets_whenGetStoreIdToGadgets_thenReturnsMappedGadgets() {
    // given
    when(this.storeIdToGadgetRepository.findAllByStoreId("store-1"))
        .thenReturn(
            List.of(
                StoreIdToGadgetEntity.builder()
                    .id("row-1")
                    .storeId("store-1")
                    .gadgetId("gadget-1")
                    .name("phone")
                    .type("mobile")
                    .color("black")
                    .build()));

    // when
    final List<StoreIdToGadgetResponse> result = this.gadgetService.getStoreIdToGadgets("store-1");

    // then
    assertThat(result)
        .containsExactly(
            StoreIdToGadgetResponse.builder()
                .storeId("store-1")
                .gadgetId("gadget-1")
                .name("phone")
                .type("mobile")
                .color("black")
                .build());
  }

  @Test
  void givenStoreWithoutGadgets_whenGetStoreIdToGadgets_thenReturnsEmptyList() {
    // given
    when(this.storeIdToGadgetRepository.findAllByStoreId("store-1")).thenReturn(List.of());

    // when
    final List<StoreIdToGadgetResponse> result = this.gadgetService.getStoreIdToGadgets("store-1");

    // then
    assertThat(result).isEmpty();
  }
}
