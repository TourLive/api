package models;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(name = "key_gen_Maillot", sequenceName = "key_gen_Maillot",  initialValue = 1)
@ApiModel(value = "Maillot", description="Model of maillot")
public class Maillot {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator = "key_gen_Maillot")
    private Long id;
    private String type;
    private String name;
    private String color;
    private String partner;
    private long riderId;
    @ManyToMany(mappedBy="riderMaillots", cascade= CascadeType.ALL, fetch = FetchType.EAGER)
    private List<RiderStageConnection> riderStageConnections = new ArrayList<>();

    @ManyToOne
    private Stage stage;

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public Stage getStage() { return stage; }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public long getRiderId() {
        return riderId;
    }

    public void setRiderId(long riderId) {
        this.riderId = riderId;
    }

    public List<RiderStageConnection> getRiderStageConnections() {
        return riderStageConnections;
    }

    public void setRiderStageConnections(List<RiderStageConnection> riderStageConnections) {
        this.riderStageConnections = riderStageConnections;
    }
}
