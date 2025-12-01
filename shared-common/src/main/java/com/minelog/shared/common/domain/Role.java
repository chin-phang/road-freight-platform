package com.minelog.shared.common.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "role")
@Getter
@Setter
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Role extends CustomAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
  @SequenceGenerator(
      name = "role_id_seq",
      sequenceName = "role_id_seq",
      allocationSize = 1
  )
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  // Inverse side of the relationship
  @OneToMany(mappedBy = "role")
  @Builder.Default
  @ToString.Exclude
  private List<UserRole> users = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Role role = (Role) o;
    return Objects.equals(this.name, role.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
