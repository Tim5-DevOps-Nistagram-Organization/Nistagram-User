package rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.saga;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.Constants;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka.FollowMessage;

@Service
public class FollowOrchestrator {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;

    @Autowired
    public FollowOrchestrator(KafkaTemplate<String, String> kafkaTemplate, Gson gson) {
        this.kafkaTemplate = kafkaTemplate;
        this.gson = gson;
    }

    @Async
    public void startSaga(String username, String followingUsername, String followAction) {
        FollowMessage message = new FollowMessage(Constants.SEARCH_TOPIC, Constants.USER_FOLLOW_ORCHESTRATOR_TOPIC,
                Constants.START_ACTION, username, followingUsername, followAction);
        this.kafkaTemplate.send(message.getTopic(), gson.toJson(message));
    }

    @KafkaListener(topics = Constants.USER_FOLLOW_ORCHESTRATOR_TOPIC, groupId = Constants.GROUP)
    public void getMessageOrchestrator(String msg) {
        FollowMessage message = gson.fromJson(msg, FollowMessage.class);
        if (message.getAction().equals(Constants.ERROR_ACTION)) {
            message.setDetails(Constants.USER_TOPIC, Constants.USER_FOLLOW_ORCHESTRATOR_TOPIC,
                    Constants.ROLLBACK_ACTION);
            kafkaTemplate.send(message.getTopic(), gson.toJson(message));
        }
    }
}
