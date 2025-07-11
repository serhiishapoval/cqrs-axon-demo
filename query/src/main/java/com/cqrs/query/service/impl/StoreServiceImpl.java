package com.cqrs.query.service.impl;

import com.cqrs.query.model.entity.GadgetEntity;
import com.cqrs.query.model.entity.StoreEntity;
import com.cqrs.query.model.rest.GadgetResponse;
import com.cqrs.query.model.rest.StoreResponse;
import com.cqrs.query.repository.GadgetRepository;
import com.cqrs.query.repository.StoreRepository;
import com.cqrs.query.service.StoreService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

  private final StoreRepository storeRepository;

  private final GadgetRepository gadgetRepository;

  @Override
  public StoreResponse getStore(final String storeId) {
    final Optional<StoreEntity> storeOptional = this.storeRepository.findById(storeId);

    if (storeOptional.isPresent()) {
      final StoreEntity storeEntity = storeOptional.get();
      final List<GadgetEntity> gadgetEntities = this.gadgetRepository.findAllByStoreId(storeId);

      return StoreResponse.builder()
          .id(storeEntity.getId())
          .name(storeEntity.getName())
          .year(storeEntity.getYear())
          .country(storeEntity.getCountry())
          .gadgets(
              new ArrayList<>(
                  gadgetEntities.stream()
                      .map(
                          gadgetEntity ->
                              GadgetResponse.builder()
                                  .id(gadgetEntity.getId())
                                  .name(gadgetEntity.getName())
                                  .type(gadgetEntity.getType())
                                  .color(gadgetEntity.getColor())
                                  .build())
                      .toList()))
          .build();
    }
    return StoreResponse.builder().build();
  }
}
