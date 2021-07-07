package rs.ac.uns.ftn.devops.tim5.nistagramuser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO {

    private String username;
    private String name;
    private String phone;
    private String gender;
    private Date dateOfBirth;
    private String webSite;
    private String biography;
    private int followers;
    private int following;
    private int followRequests;
    private boolean isPrivate;
    private boolean isFriend;
    private boolean isMuted;
}
