package com.cqrs.command.model.rest;

import java.util.UUID;
import lombok.Data;

@Data
public class UpdateStoreNameRequest {
  private UUID storeId;
  private String name;
}
