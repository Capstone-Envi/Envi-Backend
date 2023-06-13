package com.capstone.project.models;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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

    @Column(name = "address")
    private String address;

    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

    @Column(name = "expireResetPasswordTime")
    private LocalDateTime expireResetPasswordTime;

    @Column(name = "resetPasscode")
    private String resetPasscode;

    @Transient
    private String token;

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = User.FK_USER_ROLE),
            name = "roleId", referencedColumnName = "id",
            nullable = false
    )
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "UserNodePermission",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "nodeId")
    )
    private List<Node> nodes = new LinkedList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Notification.class, mappedBy = "user")
    private List<Notification> notifications;
}