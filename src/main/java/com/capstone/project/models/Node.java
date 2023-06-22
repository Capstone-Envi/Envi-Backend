package com.capstone.project.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "node")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Node{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "nodeId", unique = true)
    private String nodeId;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "updatedDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Column(name = "createdDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToMany(mappedBy = "nodes")
    private List<User> users = new LinkedList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Notification.class, mappedBy = "node")
    private List<Notification> notifications;

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Sensor.class, mappedBy = "node")
    private List<Sensor> sensors;
}
