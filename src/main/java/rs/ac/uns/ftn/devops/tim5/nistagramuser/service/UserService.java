package rs.ac.uns.ftn.devops.tim5.nistagramuser.service;

import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.FollowRequestException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;

import javax.mail.MessagingException;
import java.util.Collection;

public interface UserService {
    User findByUsername(String username) throws ResourceNotFoundException;

    void create(String username, String email, String websiteUrl);

    void delete(String username) throws ResourceNotFoundException;

    void update(User user, String username) throws ResourceNotFoundException;

    boolean settings(boolean isPrivate, String username) throws ResourceNotFoundException;

    void settingsRollback(boolean isPrivate, String username) throws ResourceNotFoundException, MessagingException;

    boolean isFriend(User user, String username) throws ResourceNotFoundException;

    boolean isMuted(User user, String username) throws ResourceNotFoundException;

    boolean follow(String username, String followUserUsername) throws ResourceNotFoundException, FollowRequestException;

    void followRequest(String username, String followUserUsername, boolean accept) throws ResourceNotFoundException;

    void unfollow(String username, String unfollowUserUsername) throws ResourceNotFoundException, FollowRequestException;

    Collection<User> getFollowRequests(String username) throws ResourceNotFoundException;

    void mute(String username, String followingUsername) throws ResourceNotFoundException, FollowRequestException;

    void unmute(String username, String followingUsername) throws ResourceNotFoundException, FollowRequestException;

    void followRollback(String userUsername, String followingUsername, String followAction) throws ResourceNotFoundException, FollowRequestException, MessagingException;
}
