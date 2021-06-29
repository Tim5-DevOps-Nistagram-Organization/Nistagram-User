package rs.ac.uns.ftn.devops.tim5.nistagramuser.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.repository.UserRepository;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.MailService;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.UserService;

import javax.mail.MessagingException;

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
    public boolean canAccess(User user, String username) throws ResourceNotFoundException {
        User me = findByUsername(username);
        return user.getFollowers().contains(me);
    }
}
