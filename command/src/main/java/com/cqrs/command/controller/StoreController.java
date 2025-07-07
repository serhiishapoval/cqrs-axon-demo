package com.cqrs.command.controller;

import com.cqrs.command.model.rest.StoreRequest;
import com.cqrs.command.service.ProcessNewStoreCreationService;
import com.cqrs.command.service.impl.ProcessNewStoreCreationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Slf4j
public class StoreController {

  private final ProcessNewStoreCreationService processNewStoreCreationService;

  @PostMapping
  public ResponseEntity<String> createNewStore(@RequestBody final StoreRequest storeRequest) {
    final String storeId = this.processNewStoreCreationService.processNewStore(storeRequest);
    log.info("createNewStore. Store {} successfully created", storeId);
    return ResponseEntity.ok(storeId);
  }
}
