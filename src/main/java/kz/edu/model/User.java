package kz.edu.model;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Entity(name = "UserEntity")
@Table(name = "users")
public class User implements Serializable {

    private long user_id;

    @NotEmpty(message = "Email should not be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "Name should not be empty")
    private String name;

    @NotEmpty(message = "Password should not be empty")
    private String password;


    private Role role;
    private Boolean active = true;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public long getId()
    {
        return this.user_id;
    }
    public void setId(long user_id)
    {
        this.user_id = user_id;
    }

    @Column(name = "email")
    public String getEmail()
    {
        return this.email;
    }
    public void setEmail(String email)
    {this.email = email;}

    @Column(name = "password")
    public String getPassword()
    {
        return this.password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    @Column(name = "name")
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id")
    public Role getRole()
    {
        return role;
    }
    public void setRole(Role role)
    {
        this.role = role;
    }

    @Column(name = "active")
    public Boolean isActive()
    {
        return this.active;
    }
    public void setActive(Boolean active)
    {
        this.active = active;
    }
}