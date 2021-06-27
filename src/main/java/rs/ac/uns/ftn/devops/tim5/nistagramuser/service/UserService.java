package rs.ac.uns.ftn.devops.tim5.nistagramuser.service;

import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;

public interface UserService {
    User findByUsername(String username) throws ResourceNotFoundException;

    void create(String username, String email);

    void delete(String username) throws ResourceNotFoundException;

    void update(User user, String username) throws ResourceNotFoundException;
}
