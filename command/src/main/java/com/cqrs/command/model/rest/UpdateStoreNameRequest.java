package com.cqrs.command.model.rest;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateStoreNameRequest {
    private UUID storeId;
    private String name;
}
