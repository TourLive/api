package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Race {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "race", cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Stage> stages = new ArrayList<Stage>();

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
}
