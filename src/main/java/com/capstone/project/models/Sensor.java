package com.capstone.project.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private SensorType type;

    @Column(name = "location")
    private String location;

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = Sensor.FK_SENSOR_NODE),
            name = "nodeId", referencedColumnName = "id",
            nullable = false
    )
    private Node node;

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = SensorPeriodicData.class, mappedBy = "sensor")
    private List<SensorPeriodicData> sensorPeriodicData;

    @Setter(AccessLevel.NONE)
    @OneToMany(targetEntity = SensorIntervalData.class, mappedBy = "sensor")
    private List<SensorIntervalData> sensorIntervalData;
}
