package rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage extends Message {
    private String username;
    private String email;
    private String websiteUrl;
}
