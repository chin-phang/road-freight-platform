package com.minelog.road.auth.component;

import com.minelog.shared.common.domain.CustomAudit;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@Configurable
@RequiredArgsConstructor
public class CustomAuditListener {

  private final ObjectFactory<AuditorAware<String>> auditorAwareFactory;

  @PrePersist
  public void setCreatedOn(Object entity) {
    if (entity instanceof CustomAudit customAudit) {
      Instant now = Instant.now();
      customAudit.setCreatedAt(now);
      customAudit.setLastModifiedAt(now);

      String currentUser = getCurrentUser();
      customAudit.setCreatedBy(currentUser);
      customAudit.setLastModifiedBy(currentUser);
    }
  }

  @PreUpdate
  public void setLastModifiedOn(Object entity) {
    if (entity instanceof CustomAudit customAudit) {
      customAudit.setLastModifiedAt(Instant.now());
      customAudit.setLastModifiedBy(getCurrentUser());
    }
  }

  private String getCurrentUser() {
    String auditor = auditorAwareFactory.getObject()
        .getCurrentAuditor()
        .orElse("system");

    if ("anonymousUser".equalsIgnoreCase(auditor)) {
      return "system";
    }

    return auditor;
  }
}
