package com.minelog.shared.common.domain;

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
public abstract class CustomAudit {

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "created_by", nullable = false, updatable = false)
  private String createdBy;

  @Column(name = "last_modified_at", insertable = false)
  private Instant lastModifiedAt;

  @Column(name = "last_modified_by", insertable = false)
  private String lastModifiedBy;
}
