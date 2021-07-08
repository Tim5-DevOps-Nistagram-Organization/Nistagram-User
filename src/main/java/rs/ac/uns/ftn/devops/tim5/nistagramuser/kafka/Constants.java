package rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka;

public class Constants {

    public static final String GROUP = "my_group_id";

    public static final String AUTH_TOPIC = "auth_topic";
    public static final String AUTH_USER_TOPIC = "auth_user_topic";

    public static final String USER_ORCHESTRATOR_TOPIC = "user_orchestrator_topic";
    public static final String USER_SETTINGS_ORCHESTRATOR_TOPIC = "user_settings_orchestrator_topic";
    public static final String USER_FOLLOW_ORCHESTRATOR_TOPIC = "user_follow_orchestrator_topic";
    public static final String USER_TOPIC = "user_topic";
    public static final String SEARCH_TOPIC = "search_topic";

    public static final String FOLLOW_ACTION = "follow_action";
    public static final String UNFOLLOW_ACTION = "unfollow_action";
    public static final String MUTE_ACTION = "mute_action";
    public static final String UNMUTE_ACTION = "unmute_action";

    public static final String START_ACTION = "start_action";
    public static final String DONE_ACTION = "done_action";
    public static final String ERROR_ACTION = "error_action";
    public static final String ROLLBACK_ACTION = "rollback_action";
    public static final String ROLLBACK_DONE_ACTION = "rollback_done_action";
}
