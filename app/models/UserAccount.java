package models;

import io.swagger.annotations.ApiModel;

import javax.persistence.*;

@Entity
@SequenceGenerator(name = "key_gen_User", sequenceName = "key_gen_User",  initialValue = 1)
@ApiModel(value = "UserAccount", description="Model of user account")
public class UserAccount {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO, generator = "key_gen_User")
    private Long id;

    private String username;

    private String password;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
