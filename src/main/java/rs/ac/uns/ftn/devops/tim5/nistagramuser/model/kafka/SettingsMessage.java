package rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SettingsMessage extends Message {
    private String username;
    private boolean isPrivate;

    public SettingsMessage(String topic, String replayTopic, String action, String username, boolean isPrivate) {
        super(topic, replayTopic, action);
        this.username = username;
        this.isPrivate = isPrivate;
    }

}
