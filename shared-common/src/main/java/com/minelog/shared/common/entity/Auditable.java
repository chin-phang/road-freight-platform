package com.minelog.shared.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class Auditable {

  @Column(name = "created_by", nullable = false, updatable = false)
  private String createdBy;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "last_modified_by", insertable = false)
  private String lastModifiedBy;

  @Column(name = "last_modified_at", insertable = false)
  private Instant lastModifiedAt;
}
