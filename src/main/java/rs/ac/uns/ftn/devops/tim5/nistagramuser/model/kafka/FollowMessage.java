package rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FollowMessage extends Message {
    private String userUsername;
    private String followingUsername;
    private String followAction;

    public FollowMessage(String topic, String replayTopic, String action, String userUsername, String followingUsername,
                         String followAction) {
        super(topic, replayTopic, action);
        this.userUsername = userUsername;
        this.followingUsername = followingUsername;
        this.followAction = followAction;
    }

}
