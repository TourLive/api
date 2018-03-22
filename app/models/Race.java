package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    public String name;
    @OneToMany(mappedBy="race", cascade= CascadeType.ALL)
    public List<Stage> stages = new ArrayList<Stage>();
}
