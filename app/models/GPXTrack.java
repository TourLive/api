package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "key_gen_GPXTrack", sequenceName = "key_gen_GPXTrack",  initialValue = 1)
public class GPXTrack {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_GPXTrack")
    private Long id;
    private double latitude;
    private double longitude;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Stage stage;

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

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
