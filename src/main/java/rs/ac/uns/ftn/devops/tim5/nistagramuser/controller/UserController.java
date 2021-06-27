package rs.ac.uns.ftn.devops.tim5.nistagramuser.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.RequestUserDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.ResponseUserDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.mapper.UserMapper;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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

}