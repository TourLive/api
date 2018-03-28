package models;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class Maillot {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private int maillotId;
    private String type;
    private String name;
    private String color;
    private String partner;

    @ManyToOne(cascade=CascadeType.PERSIST)
    @JsonBackReference
    private Stage stage;

    public Long getId() {
        return id;
    }

    public int getMaillotId() {
        return maillotId;
    }

    public void setMaillotId(int maillotId) {
        this.maillotId = maillotId;
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

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
