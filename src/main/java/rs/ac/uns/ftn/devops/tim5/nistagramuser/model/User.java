package rs.ac.uns.ftn.devops.tim5.nistagramuser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "user_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String email;
    private String name;
    private String phone;
    private String gender;
    private Date dateOfBirth;
    private String webSite;
    private String biography;
    private Boolean isPrivate;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> followers;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> following;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> followRequest;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<User> muted;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.isPrivate = false;
        this.followers = new HashSet<>();
        this.following = new HashSet<>();
        this.followRequest = new HashSet<>();
        this.muted = new HashSet<>();
    }

    public User(String name, String phone, String gender, Date dateOfBirth, String webSite, String biography) {
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.webSite = webSite;
        this.biography = biography;
        this.isPrivate = false;
    }

    public void update(User user) {
        this.name = user.getName();
        this.phone = user.getPhone();
        this.gender = user.getGender();
        this.dateOfBirth = user.getDateOfBirth();
        this.webSite = user.getWebSite();
        this.biography = user.getBiography();
    }
}
