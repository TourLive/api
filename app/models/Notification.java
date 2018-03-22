package models;

import models.enums.NotificationType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Access(AccessType.PROPERTY)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String message;
    public Timestamp timestamp;
    public NotificationType notificationType;

    @ManyToOne(cascade=CascadeType.PERSIST)
    public Stage stage;

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
