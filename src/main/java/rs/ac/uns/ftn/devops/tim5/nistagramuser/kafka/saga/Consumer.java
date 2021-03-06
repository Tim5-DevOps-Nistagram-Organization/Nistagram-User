package rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.saga;


import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.FollowRequestException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.Constants;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka.FollowMessage;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka.Message;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka.SettingsMessage;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.kafka.UserMessage;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.UserService;

import javax.mail.MessagingException;

@Service
public class Consumer {

    private final UserService userService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;

    @Autowired
    public Consumer(UserService userService, KafkaTemplate<String, String> kafkaTemplate, Gson gson) {
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
        this.gson = gson;
    }

    @KafkaListener(topics = Constants.USER_TOPIC, groupId = Constants.GROUP)
    public void getMessage(String msg) throws ResourceNotFoundException, MessagingException, FollowRequestException {
        Message message = gson.fromJson(msg, Message.class);
        if (message.getReplayTopic().equals(Constants.USER_ORCHESTRATOR_TOPIC)) {
            UserMessage userMessage = gson.fromJson(msg, UserMessage.class);
            if (userMessage.getAction().equals(Constants.START_ACTION)) {
                try {
                    userService.create(userMessage.getUsername(), userMessage.getEmail(), userMessage.getWebsiteUrl());
                    userMessage.setDetails(userMessage.getReplayTopic(), Constants.USER_TOPIC, Constants.DONE_ACTION);
                } catch (Exception e) {
                    userMessage.setDetails(userMessage.getReplayTopic(), Constants.USER_TOPIC, Constants.ERROR_ACTION);
                }
            } else if (userMessage.getAction().equals(Constants.ROLLBACK_ACTION)) {
                userService.delete(userMessage.getUsername());
                userMessage.setDetails(userMessage.getReplayTopic(), Constants.USER_TOPIC, Constants.ROLLBACK_DONE_ACTION);
            }
            kafkaTemplate.send(userMessage.getTopic(), gson.toJson(userMessage));
        } else if (message.getReplayTopic().equals(Constants.USER_SETTINGS_ORCHESTRATOR_TOPIC) &&
                message.getAction().equals(Constants.ROLLBACK_ACTION)) {
            SettingsMessage settingsMessage = gson.fromJson(msg, SettingsMessage.class);
            userService.settingsRollback(!settingsMessage.isPrivate(), settingsMessage.getUsername());
        } else if (message.getReplayTopic().equals(Constants.USER_FOLLOW_ORCHESTRATOR_TOPIC) &&
                message.getAction().equals(Constants.ROLLBACK_ACTION)) {
            FollowMessage followMessage = gson.fromJson(msg, FollowMessage.class);
            userService.followRollback(followMessage.getUserUsername(), followMessage.getFollowingUsername(),
                    followMessage.getFollowAction());
        }
    }
}