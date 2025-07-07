package com.cqrs.query.handler;

import com.cqrs.query.model.query.GetStoreByIdQuery;
import com.cqrs.query.model.rest.StoreResponse;
import com.cqrs.query.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StoreQueryHandler {

  private final StoreService storeService;

  @QueryHandler
  public StoreResponse getStore(final GetStoreByIdQuery getStoreByIdQuery) {
    return this.storeService.getStore(getStoreByIdQuery.getStoreId());
  }
}
