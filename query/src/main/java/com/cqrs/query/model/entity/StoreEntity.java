package com.cqrs.query.model.entity;

import jakarta.persistence.Column;
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
@Entity(name = "STORE")
public class StoreEntity {
  @Id private String id;
  private String name;

  @Column(name = "\"year\"")
  private int year;

  private String country;
}
