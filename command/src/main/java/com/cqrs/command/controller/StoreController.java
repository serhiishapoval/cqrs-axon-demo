package com.cqrs.command.controller;

import com.cqrs.command.model.rest.StoreRequest;
import com.cqrs.command.model.rest.UpdateStoreNameRequest;
import com.cqrs.command.service.StoreProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Slf4j
public class StoreController {

  private final StoreProcessingService storeProcessingService;

  @PostMapping
  public ResponseEntity<String> createNewStore(@RequestBody final StoreRequest storeRequest) {
    final String storeId = this.storeProcessingService.createStore(storeRequest);
    log.info("createNewStore. Store {} successfully created", storeId);
    return ResponseEntity.ok(storeId);
  }

  @PatchMapping
  public ResponseEntity<Void> updateStoreName(
      @RequestBody final UpdateStoreNameRequest updateStoreNameRequest) {
    this.storeProcessingService.updateStoreName(updateStoreNameRequest);
    log.info(
        "updateStoreName. StoreId: {}, new StoreName: {}",
        updateStoreNameRequest.getStoreId(),
        updateStoreNameRequest.getName());
    return ResponseEntity.ok().build();
  }
}
