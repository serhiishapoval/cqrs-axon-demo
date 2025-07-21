package com.cqrs.command.service;

import com.cqrs.command.model.rest.StoreRequest;
import com.cqrs.command.model.rest.UpdateStoreNameRequest;

public interface StoreProcessingService {
  String createStore(StoreRequest storeRequest);

  void updateStoreName(UpdateStoreNameRequest updateStoreNameRequest);
}
