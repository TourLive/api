package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Race {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "race", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Stage> stages = new ArrayList<Stage>();

    @OneToMany(mappedBy="race", cascade= CascadeType.ALL)
    @JsonIgnore
    private List<Maillot> mailllots = new ArrayList<Maillot>();

    public Long getId() {
        return id;
    }

    public void setId(long id){this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public List<Maillot> getMailllots() {
        return mailllots;
    }

    public void setMailllots(List<Maillot> mailllots) {
        this.mailllots = mailllots;
    }
}
