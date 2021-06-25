package rs.ac.uns.ftn.devops.tim5.nistagramuser.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/init")
public class InitController {


    @GetMapping
    public ResponseEntity<String> getAllSales() {
        return new ResponseEntity<>("Hello, Nistagram User", HttpStatus.OK);
    }

    @GetMapping(value = "/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> admin() {
        return new ResponseEntity<>("Hello, Nistagram User", HttpStatus.OK);
    }

    @GetMapping(value = "/regular")
    @PreAuthorize("hasRole('ROLE_REGULAR')")
    public ResponseEntity<String> regular() {
        return new ResponseEntity<>("Hello, Nistagram User", HttpStatus.OK);
    }

}