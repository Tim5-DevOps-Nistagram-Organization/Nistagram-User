package rs.ac.uns.ftn.devops.tim5.nistagramuser.service;

import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;

import javax.mail.MessagingException;

public interface UserService {
    User findByUsername(String username) throws ResourceNotFoundException;

    void create(String username, String email, String websiteUrl);

    void delete(String username) throws ResourceNotFoundException;

    void update(User user, String username) throws ResourceNotFoundException;

    boolean settings(boolean isPrivate, String username) throws ResourceNotFoundException;

    void settingsRollback(boolean isPrivate, String username) throws ResourceNotFoundException, MessagingException;
}
