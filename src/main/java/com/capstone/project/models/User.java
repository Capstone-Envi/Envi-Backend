package com.capstone.project.models;
import java.util.Date;

import jakarta.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseModel{
    public static final String FK_USER_ROLE = "fk_User_Role";

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

    @Transient
    private String token;

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = User.FK_USER_ROLE),
            name = "roleId", referencedColumnName = "id",
            nullable = false
    )
    private Role role;
}