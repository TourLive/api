package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import models.enums.NotificationType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@SequenceGenerator(name = "key_gen_Log", sequenceName = "key_gen_Log",  initialValue = 1)
@ApiModel(value = "Log", description="Model of log")
public class Log {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_Log")
    private Long id;
    private String message;
    private Timestamp timestamp;
    private NotificationType notificationType;
    private Long riderId;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
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

    @ApiModelProperty(hidden=true)
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setRiderId(Long riderId) { this.riderId = riderId; }

    public Long getRiderId(){ return riderId;}
}
