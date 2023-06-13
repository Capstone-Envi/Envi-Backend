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
@Table(name = "sensorperiodicdata")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class SensorPeriodicData extends BaseModel{
    public static final String FK_PERIODICDATA_SENSOR = "fk_Periodicdata_Sensor";

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = SensorPeriodicData.FK_PERIODICDATA_SENSOR),
            name = "sensorId", referencedColumnName = "id",
            nullable = false
    )
    private Sensor sensor;

    @Column(name = "data")
    private String data;
}
