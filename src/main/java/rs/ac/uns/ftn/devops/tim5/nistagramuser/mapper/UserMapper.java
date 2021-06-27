package rs.ac.uns.ftn.devops.tim5.nistagramuser.mapper;

import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.RequestUserDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.ResponseUserDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;

public class UserMapper {

    public static User toEntity(RequestUserDTO userDTO) {
        return new User(userDTO.getName(), userDTO.getPhone(), userDTO.getGender(), userDTO.getDateOfBirth(),
                userDTO.getWebSite(), userDTO.getBiography());
    }

    public static ResponseUserDTO toDTO(User user) {
        return new ResponseUserDTO(user.getUsername(), user.getEmail(), user.getName(), user.getPhone(),
                user.getGender(), user.getDateOfBirth(), user.getWebSite(), user.getBiography());
    }
}