package com.cqrs.query.service.impl;

import com.cqrs.query.model.entity.GadgetEntity;
import com.cqrs.query.model.entity.StoreIdToGadgetEntity;
import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.GadgetToCountryResponse;
import com.cqrs.query.model.rest.StoreIdToGadgetResponse;
import com.cqrs.query.repository.GadgetRepository;
import com.cqrs.query.repository.GadgetToCountryRepository;
import com.cqrs.query.repository.StoreIdToGadgetRepository;
import com.cqrs.query.service.GadgetService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class GadgetServiceImpl implements GadgetService {

  private final GadgetRepository gadgetRepository;

  private final GadgetToCountryRepository gadgetToCountryRepository;

  private final StoreIdToGadgetRepository storeIdToGadgetRepository;

  @Override
  public GadgetResponse getGadget(String gadgetId) {
    final Optional<GadgetEntity> gadgetEntityOptional = this.gadgetRepository.findById(gadgetId);
    if (gadgetEntityOptional.isPresent()) {
      final GadgetEntity gadgetEntity = gadgetEntityOptional.get();
      return GadgetResponse.builder()
          .id(gadgetEntity.getId())
          .name(gadgetEntity.getName())
          .type(gadgetEntity.getType())
          .color(gadgetEntity.getColor())
          .build();
    }
    return GadgetResponse.builder().build();
  }

  @Override
  public List<GadgetToCountryResponse> getGadgetsFromCountry(final String country) {
    return new ArrayList<>(
        this.gadgetToCountryRepository.findAllByCountry(country).stream()
            .map(
                gadgetToCountryEntity ->
                    GadgetToCountryResponse.builder()
                        .country(gadgetToCountryEntity.getCountry())
                        .gadgetId(gadgetToCountryEntity.getGadgetId())
                        .name(gadgetToCountryEntity.getName())
                        .type(gadgetToCountryEntity.getType())
                        .color(gadgetToCountryEntity.getColor())
                        .build())
            .toList());
  }

  @Override
  public List<StoreIdToGadgetResponse> getStoreIdToGadgets(String storeId) {
    final List<StoreIdToGadgetEntity> storeIdToGadgetEntities =
        this.storeIdToGadgetRepository.findAllByStoreId(storeId);
    return CollectionUtils.isEmpty(storeIdToGadgetEntities)
        ? new ArrayList<>(0)
        : new ArrayList<>(
            storeIdToGadgetEntities.stream()
                .map(
                    storeIdToGadgetEntity ->
                        StoreIdToGadgetResponse.builder()
                            .storeId(storeIdToGadgetEntity.getStoreId())
                            .gadgetId(storeIdToGadgetEntity.getGadgetId())
                            .name(storeIdToGadgetEntity.getName())
                            .type(storeIdToGadgetEntity.getType())
                            .color(storeIdToGadgetEntity.getColor())
                            .build())
                .toList());
  }
}
