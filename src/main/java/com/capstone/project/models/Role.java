package com.capstone.project.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "Role", schema = "public")
public class Role extends BaseModel {
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = User.class, mappedBy = "role")
    private List<User> users;
}
