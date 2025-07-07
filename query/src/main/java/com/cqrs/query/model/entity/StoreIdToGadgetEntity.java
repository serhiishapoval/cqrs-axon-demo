package com.cqrs.query.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "STORE_ID_TO_GADGET")
public class StoreIdToGadgetEntity {

  @Id
  private String id;
  private String storeId;
  private String gadgetId;
  private String name;
  private String type;
  private String color;
}
