package com.capstone.project.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sensor")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Sensor extends BaseModel{
    public static final String FK_SENSOR_NODE = "fk_Sensor_Node";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "sensorCode", unique = true)
    private String sensorCode;

    @Column(name = "minThreshold")
    private float minThreshold;

    @Column(name = "maxThreshold")
    private float maxThreshold;

    @Enumerated(EnumType.STRING)
    private SensorType type;

    @Column(name = "power")
    private String power;

    @Column(name = "size")
    private String size;

    @Column(name = "productLine")
    private String productLine;

    @Column(name = "interval")
    private float interval;

    @Column(name = "location")
    private String location;

    @Column(name = "updatedDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @Column(name = "createdDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = Sensor.FK_SENSOR_NODE),
            name = "nodeId", referencedColumnName = "id",
            nullable = false
    )
    private Node node;

    @Setter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, targetEntity = SensorPeriodicData.class, mappedBy = "sensor")
    private List<SensorPeriodicData> sensorPeriodicData;

    @Setter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, targetEntity = SensorIntervalData.class, mappedBy = "sensor")
    private List<SensorIntervalData> sensorIntervalData;

    @Setter(AccessLevel.NONE)
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, targetEntity = Alert.class, mappedBy = "sensor")
    private List<Alert> alerts;
}
