package models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModel;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@ApiModel(value = "Race", description="Model of race")
public class Race {
    @Id
    private Long id;
    private String name;

    @OneToMany(mappedBy = "race", orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Stage> stages = new ArrayList<>();

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
