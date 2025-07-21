package com.cqrs.query.handler;

import com.cqrs.query.model.query.GetStoreByIdQuery;
import com.cqrs.query.model.rest.StoreResponse;
import com.cqrs.query.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreQueryHandler {

  private final StoreService storeService;

  @QueryHandler
  public StoreResponse getStore(final GetStoreByIdQuery getStoreByIdQuery) {
    log.info("getStore. GetStoreByIdQuery: {}", getStoreByIdQuery);
    return this.storeService.getStore(getStoreByIdQuery.getStoreId());
  }
}
