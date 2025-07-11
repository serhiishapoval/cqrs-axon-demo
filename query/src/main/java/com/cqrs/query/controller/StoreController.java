package com.cqrs.query.controller;

import com.cqrs.query.model.query.GetStoreByIdQuery;
import com.cqrs.query.model.rest.StoreResponse;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
public class StoreController {

  private final QueryGateway queryGateway;

  @GetMapping("/{storeId}")
  public ResponseEntity<StoreResponse> getStoreById(@PathVariable final String storeId) {
    final GetStoreByIdQuery getStoreByIdQuery = new GetStoreByIdQuery(storeId);

    return ResponseEntity.ok(
        this.queryGateway.query(getStoreByIdQuery, StoreResponse.class).join());
  }
}
