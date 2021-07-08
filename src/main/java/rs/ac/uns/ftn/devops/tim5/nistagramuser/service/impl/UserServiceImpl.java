package rs.ac.uns.ftn.devops.tim5.nistagramuser.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.FollowRequestException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.Constants;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.repository.UserRepository;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.MailService;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.UserService;

import javax.mail.MessagingException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MailService mailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public User findByUsername(String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User"));
    }

    @Override
    public void create(String username, String email, String websiteUrl) {
        User user = new User(username, email);
        user.setWebSite(websiteUrl);
        userRepository.save(user);
    }

    @Override
    public void delete(String username) throws ResourceNotFoundException {
        User user = findByUsername(username);
        userRepository.delete(user);
    }

    @Override
    public void update(User user, String username) throws ResourceNotFoundException {
        User dbUser = findByUsername(username);
        dbUser.update(user);
        userRepository.save(dbUser);
    }

    @Override
    public boolean settings(boolean isPrivate, String username) throws ResourceNotFoundException {
        User dbUser = findByUsername(username);
        if (dbUser.getIsPrivate() != isPrivate) {
            dbUser.setIsPrivate(isPrivate);
            userRepository.save(dbUser);
            return true;
        }
        return false;
    }

    @Override
    public void settingsRollback(boolean isPrivate, String username) throws ResourceNotFoundException, MessagingException {
        User dbUser = findByUsername(username);
        dbUser.setIsPrivate(isPrivate);
        userRepository.save(dbUser);
        String subject = "Something went wrong!";
        String message = "<html><head><meta charset=\"UTF-8\"></head>"
                + "<body><h3>Nistagram app - Something went wrong!</h3><br>"
                + "<div><p>Something went wrong with changing your privacy settings, please try again!"
                + "</p></div></body></html>";
        mailService.sendMail(dbUser.getEmail(), subject, message);
    }

    @Override
    public boolean isFriend(User user, String username) throws ResourceNotFoundException {
        User me = findByUsername(username);
        return me.getFollowing().contains(user);
    }

    @Override
    public boolean isMuted(User user, String username) throws ResourceNotFoundException {
        User me = findByUsername(username);
        return me.getMuted().contains(user);
    }

    @Override
    public boolean follow(String username, String followUserUsername) throws ResourceNotFoundException, FollowRequestException {
        if (username.equals(followUserUsername))
            throw new FollowRequestException("You can not follow yourself.");

        User followUser = findByUsername(followUserUsername);
        if (followUser.getFollowers().stream().anyMatch(u -> u.getUsername().equals(username)))
            throw new FollowRequestException("Already following");

        User follower = findByUsername(username);
        if (followUser.getIsPrivate()) {
            followUser.getFollowRequest().add(follower);
            userRepository.save(followUser);
            return false;
        }
        followUsers(followUser, follower);
        return true;
    }

    @Override
    public void followRequest(String username, String followerUsername, boolean accept) throws ResourceNotFoundException {
        User user = findByUsername(username);
        user.setFollowRequest(user.getFollowRequest().stream()
                .filter((u) -> !u.getUsername().equals(followerUsername))
                .collect(Collectors.toSet()));
        if (accept) {
            User follower = findByUsername(followerUsername);
            followUsers(user, follower);
        } else {
            userRepository.save(user);
        }
    }

    @Override
    public void unfollow(String username, String unfollowUserUsername) throws ResourceNotFoundException, FollowRequestException {
        User unfollowUser = findByUsername(unfollowUserUsername);

        if (unfollowUser.getFollowers().stream().noneMatch(u -> u.getUsername().equals(username)))
            throw new FollowRequestException("Already unfollowing");
        User follower = findByUsername(username);

        unfollowUser.setFollowers(unfollowUser.getFollowers().stream()
                .filter((u) -> !u.getUsername().equals(username))
                .collect(Collectors.toSet()));

        follower.setFollowing(follower.getFollowing().stream()
                .filter((u) -> !u.getUsername().equals(unfollowUserUsername))
                .collect(Collectors.toSet()));

        follower.setMuted(follower.getFollowing().stream()
                .filter((u) -> !u.getUsername().equals(unfollowUserUsername))
                .collect(Collectors.toSet()));

        userRepository.save(unfollowUser);
        userRepository.save(follower);
    }

    @Override
    public Collection<User> getFollowRequests(String username) throws ResourceNotFoundException {
        return findByUsername(username).getFollowRequest();
    }

    @Override
    public void mute(String username, String followingUsername) throws ResourceNotFoundException, FollowRequestException {
        User user = findByUsername(username);
        if (user.getFollowing().stream().noneMatch(u -> u.getUsername().equals(followingUsername)))
            throw new FollowRequestException("You do not following that user.");
        User following = findByUsername(followingUsername);
        user.getMuted().add(following);
        userRepository.save(user);
    }

    @Override
    public void unmute(String username, String followingUsername) throws ResourceNotFoundException, FollowRequestException {
        User user = findByUsername(username);
        if (user.getMuted().stream().noneMatch(u -> u.getUsername().equals(followingUsername)))
            throw new FollowRequestException("That user user is not muted.");
        user.setMuted(user.getMuted().stream()
                .filter(u -> !u.getUsername().equals(followingUsername))
                .collect(Collectors.toSet()));
        userRepository.save(user);
    }

    @Override
    public void followRollback(String userUsername, String followingUsername, String followAction) throws ResourceNotFoundException, FollowRequestException, MessagingException {
        User user = findByUsername(userUsername);
        switch (followAction) {
            case Constants.FOLLOW_ACTION:
                unfollow(userUsername, followingUsername);
                break;
            case Constants.UNFOLLOW_ACTION:
                User followingUser = findByUsername(followingUsername);
                followUsers(followingUser, user);
                break;
            case Constants.MUTE_ACTION:
                unmute(userUsername, followingUsername);
                break;
            case Constants.UNMUTE_ACTION:
                mute(userUsername, followingUsername);
                break;
        }
        String subject = "Something went wrong!";
        String message = "<html><head><meta charset=\"UTF-8\"></head>"
                + "<body><h3>Nistagram app - Something went wrong!</h3><br>"
                + "<div><p>Something went wrong with following other profile, please try again!"
                + "</p></div></body></html>";
        mailService.sendMail(user.getEmail(), subject, message);
    }

    public void followUsers(User followUser, User follower) {
        followUser.getFollowers().add(follower);
        follower.getFollowing().add(followUser);
        userRepository.save(followUser);
        userRepository.save(follower);
    }
}
