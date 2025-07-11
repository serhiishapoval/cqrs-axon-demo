package com.cqrs.command.model.entity;

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
@Entity(name = "GADGET")
public class GadgetEntity {

  @Id private String id;
  private String name;
  private String type;
  private String color;
  private String storeId;
}
