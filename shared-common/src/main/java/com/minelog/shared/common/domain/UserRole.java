package com.minelog.shared.common.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Entity
@Table(name = "user_role")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRole extends CustomAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_id_seq")
  @SequenceGenerator(
      name = "user_role_id_seq",
      sequenceName = "user_role_id_seq",
      allocationSize = 1
  )
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  @ToString.Exclude
  private User user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "role_id")
  @ToString.Exclude
  private Role role;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRole userRole = (UserRole) o;
    return Objects.equals(getId(), userRole.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
