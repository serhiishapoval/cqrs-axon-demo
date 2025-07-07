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
@Entity(name = "GADGET_TO_COUNTRY")
public class GadgetToCountryEntity {
  @Id
  private String id;
  private String country;
  private String gadgetId;
  private String name;
  private String type;
  private String color;
}
