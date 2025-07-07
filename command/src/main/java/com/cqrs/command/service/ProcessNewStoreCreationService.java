package com.cqrs.command.service;

import com.cqrs.command.model.rest.StoreRequest;

public interface ProcessNewStoreCreationService {
  String processNewStore(StoreRequest storeRequest);
}
