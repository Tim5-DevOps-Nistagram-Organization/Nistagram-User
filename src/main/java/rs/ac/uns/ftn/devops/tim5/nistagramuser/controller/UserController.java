package rs.ac.uns.ftn.devops.tim5.nistagramuser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.RequestUserDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.ResponseUserDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.UserDetailsDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.FollowRequestException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.Constants;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.saga.FollowOrchestrator;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.kafka.saga.SettingsOrchestrator;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.mapper.UserMapper;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.UserService;

import java.security.Principal;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final SettingsOrchestrator settingsOrchestrator;
    private final FollowOrchestrator followOrchestrator;

    @Autowired
    public UserController(UserService userService, SettingsOrchestrator settingsOrchestrator,
                          FollowOrchestrator followOrchestrator) {
        this.userService = userService;
        this.settingsOrchestrator = settingsOrchestrator;
        this.followOrchestrator = followOrchestrator;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<ResponseUserDTO> me(Principal principal) throws ResourceNotFoundException {
        return new ResponseEntity<>(UserMapper.toDTO(userService.findByUsername(principal.getName())), HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<String> updateRegular(@RequestBody RequestUserDTO userDTO, Principal principal) throws ResourceNotFoundException {
        userService.update(UserMapper.toEntity(userDTO), principal.getName());
        return new ResponseEntity<>("Successfully update.", HttpStatus.OK);
    }

    @PutMapping(value = "/settings/{isPrivate}")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<String> settings(@PathVariable boolean isPrivate, Principal principal) throws ResourceNotFoundException {
        boolean changed = userService.settings(isPrivate, principal.getName());
        if (changed)
            settingsOrchestrator.startSaga(principal.getName(), isPrivate);

        return new ResponseEntity<>("Successfully update.", HttpStatus.OK);
    }

    @GetMapping(value = "/details")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<UserDetailsDTO> meDetails(Principal principal) throws ResourceNotFoundException {
        return new ResponseEntity<>(UserMapper.toDTODetails(userService.findByUsername(principal.getName())), HttpStatus.OK);
    }

    @GetMapping(value = "/view/{username}")
    public ResponseEntity<UserDetailsDTO> getByUsername(@PathVariable String username, Principal principal) throws ResourceNotFoundException {
        User user = userService.findByUsername(username);
        boolean canAccess = principal == null ? !user.getIsPrivate() : (!user.getIsPrivate() || userService.canAccess(user, principal.getName()));
        return new ResponseEntity<>(UserMapper.toDTODetails(user, canAccess), HttpStatus.OK);
    }

    @PutMapping(value = "/follow/{username}")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<String> follow(@PathVariable String username, Principal principal) throws ResourceNotFoundException, FollowRequestException {
        boolean following = userService.follow(principal.getName(), username);
        if (following) {
            followOrchestrator.startSaga(principal.getName(), username, Constants.FOLLOW_ACTION);
            return new ResponseEntity<>("Successfully follow.", HttpStatus.OK);
        }
        return new ResponseEntity<>("Successfully send follow request.", HttpStatus.OK);
    }

    @GetMapping(value = "/follow/requests")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<Collection<String>> followRequests(Principal principal) throws ResourceNotFoundException {
        Collection<User> followRequests = userService.getFollowRequests(principal.getName());
        return new ResponseEntity<>(followRequests.stream().map(User::getUsername).collect(Collectors.toList()), HttpStatus.OK);
    }

    @PutMapping(value = "/follow/{username}/{accept}")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<String> followRequest(@PathVariable String username,
                                                @PathVariable boolean accept,
                                                Principal principal) throws ResourceNotFoundException {
        userService.followRequest(principal.getName(), username, accept);
        if (accept)
            followOrchestrator.startSaga(username, principal.getName(), Constants.FOLLOW_ACTION);
        return new ResponseEntity<>("Successfully accept decision.", HttpStatus.OK);
    }

    @PutMapping(value = "/unfollow/{username}")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<String> unfollow(@PathVariable String username, Principal principal) throws ResourceNotFoundException, FollowRequestException {
        userService.unfollow(principal.getName(), username);
        followOrchestrator.startSaga(principal.getName(), username, Constants.UNFOLLOW_ACTION);
        return new ResponseEntity<>("Successfully unfollow.", HttpStatus.OK);
    }

    @PutMapping(value = "/mute/{username}")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<String> mute(@PathVariable String username, Principal principal) throws ResourceNotFoundException, FollowRequestException {
        userService.mute(principal.getName(), username);
        followOrchestrator.startSaga(principal.getName(), username, Constants.MUTE_ACTION);
        return new ResponseEntity<>("Successfully muted.", HttpStatus.OK);
    }

    @PutMapping(value = "/unmute/{username}")
    @PreAuthorize("hasRole('ROLE_REGULAR') || hasRole('ROLE_AGENT')")
    public ResponseEntity<String> unmute(@PathVariable String username, Principal principal) throws ResourceNotFoundException, FollowRequestException {
        userService.unmute(principal.getName(), username);
        followOrchestrator.startSaga(principal.getName(), username, Constants.UNMUTE_ACTION);
        return new ResponseEntity<>("Successfully unmuted.", HttpStatus.OK);
    }
}