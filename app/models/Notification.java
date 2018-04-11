package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import models.enums.NotificationType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@SequenceGenerator(name = "key_gen_Notification", sequenceName = "key_gen_Notification",  initialValue = 1)
public class Notification {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_Notification")
    private Long id;
    private String message;
    private Timestamp timestamp;
    private NotificationType notificationType;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Stage stage;

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
