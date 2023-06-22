package com.capstone.project.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sensorintervaldata")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class SensorIntervalData extends SensorDataBaseModel{
    public static final String FK_INTERVAL_DATA_SENSOR = "fk_Interval_data_Sensor";

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = SensorIntervalData.FK_INTERVAL_DATA_SENSOR),
            name = "sensorId", referencedColumnName = "id",
            nullable = false
    )
    private Sensor sensor;

    @Column(name = "data")
    private double data;
}
