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
@Table(name = "notification")
@Accessors(fluent = true, chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Notification extends BaseModel{
    public static final String FK_NOTIFICATION_USER = "fk_Notification_User";
    public static final String FK_NOTIFICATION_NODE = "fk_Notification_Node";

    @Column(name = "isRead")
    private boolean isRead;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = Notification.FK_NOTIFICATION_USER),
            name = "userId", referencedColumnName = "id",
            nullable = false
    )
    private User user;

    @ManyToOne
    @JoinColumn(
            foreignKey = @ForeignKey(name = Notification.FK_NOTIFICATION_NODE),
            name = "nodeId", referencedColumnName = "id",
            nullable = false
    )
    private Node node;
}
