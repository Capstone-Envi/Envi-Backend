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
@Table(name = "alert")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Alert extends BaseModel{
    public static final String FK_ALERT_USER = "fk_Alert_User";
    public static final String FK_ALERT_SENSOR = "fk_Alert_Sensor";

    @Column(name = "isRead")
    private boolean isRead;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = Alert.FK_ALERT_SENSOR),
            name = "sensorId", referencedColumnName = "id",
            nullable = false
    )
    private Sensor sensor;
}
