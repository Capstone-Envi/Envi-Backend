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
public class SensorIntervalData extends BaseModel{
    public static final String FK_INTERVALDATA_SENSOR = "fk_Intervaldata_Sensor";

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = SensorIntervalData.FK_INTERVALDATA_SENSOR),
            name = "sensorId", referencedColumnName = "id",
            nullable = false
    )
    private Sensor sensor;

    @Column(name = "data")
    private String data;
}
