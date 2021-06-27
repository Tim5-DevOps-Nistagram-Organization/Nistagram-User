package rs.ac.uns.ftn.devops.tim5.nistagramuser.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.repository.UserRepository;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User findByUsername(String username) throws ResourceNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User"));
    }

    @Override
    public void create(String username, String email) {
        User user = new User(username, email);
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
}
