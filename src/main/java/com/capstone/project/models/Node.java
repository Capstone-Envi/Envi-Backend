package com.capstone.project.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "node")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Node extends BaseModel{
    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "nodes")
    private List<User> users = new LinkedList<>();

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Notification.class, mappedBy = "node")
    private List<Notification> notifications;

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = Sensor.class, mappedBy = "node")
    private List<Sensor> sensors;
}
