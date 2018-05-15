package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "key_gen_GPXTrack", sequenceName = "key_gen_GPXTrack",  initialValue = 1)
@ApiModel(value = "GPXTrack", description="Model of gpx track")
public class GPXTrack {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_GPXTrack")
    private Long id;
    private double latitude;
    private double longitude;
    private double height;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    @ApiModelProperty(hidden=true)
    private Stage stage;

    public Long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @ApiModelProperty(hidden=true)
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
