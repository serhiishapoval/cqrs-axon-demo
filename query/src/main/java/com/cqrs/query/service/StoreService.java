package com.cqrs.query.service;

import com.cqrs.query.model.rest.StoreResponse;

public interface StoreService {

  StoreResponse getStore(String storeId);
}
