package rs.ac.uns.ftn.devops.tim5.nistagramuser.integration;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.constants.UserConstants;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.dto.RequestUserDTO;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.FollowRequestException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.exception.ResourceNotFoundException;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.mapper.UserMapper;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.model.User;
import rs.ac.uns.ftn.devops.tim5.nistagramuser.service.UserService;

import javax.transaction.Transactional;
import java.util.Collection;

import static org.junit.Assert.*;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {

    @Autowired
    private UserService userService;

    /*
     *  Method test User edit profile
     *  Method test creation of user,
     *              findByUsername()
     *              editProfile()
     *
     * */
    @Test
    public void testEditProfile_Success() throws ResourceNotFoundException {

        userService.create(UserConstants.USERNAME1, UserConstants.EMAIL, "");

        RequestUserDTO userDTO = new RequestUserDTO();
        userDTO.setBiography(UserConstants.BIOGRAPHY);
        userDTO.setDateOfBirth(UserConstants.DAY_OF_BIRTH);
        userDTO.setGender(UserConstants.GENDER);
        userDTO.setName(UserConstants.NAME);
        userDTO.setPhone(UserConstants.PHONE);
        userDTO.setWebSite(UserConstants.WEBSITE);
        userService.update(UserMapper.toEntity(userDTO), UserConstants.USERNAME1);

        assertEquals(UserConstants.BIOGRAPHY, userDTO.getBiography());
        assertEquals(UserConstants.DAY_OF_BIRTH, userDTO.getDateOfBirth());
        assertEquals(UserConstants.GENDER, userDTO.getGender());
        assertEquals(UserConstants.NAME, userDTO.getName());
        assertEquals(UserConstants.PHONE, userDTO.getPhone());

    }

    /*
     *   Method test User privacy settings.
     *
     * */
    @Test
    public void testUserPrivacySettings_Success() throws ResourceNotFoundException {
        userService.create(UserConstants.USERNAME2, UserConstants.EMAIL, "");
        boolean changed = userService.settings(UserConstants.IS_PRIVATE, UserConstants.USERNAME2);
        assertTrue(changed);
        User user = userService.findByUsername(UserConstants.USERNAME2);
        assertEquals(true, user.getIsPrivate());
    }

    /*
     *   Method test User privacy settings.
     *   User send invalid_username should throw ResourceNotFoundException
     *
     * */
    @Test(expected = ResourceNotFoundException.class)
    public void testUserPrivacySettings_UserNotFound() throws ResourceNotFoundException {
        userService.create(UserConstants.USERNAME_INVALID, UserConstants.EMAIL, "");
        userService.settings(UserConstants.IS_PRIVATE, UserConstants.USERNAME2);
    }

    /*
     *   Method test all following features
     *
     * */
    @Test
    public void testFollowFeature_Success() throws ResourceNotFoundException, FollowRequestException {

        userService.create(UserConstants.USERNAME3, UserConstants.EMAIL, "");
        userService.create(UserConstants.USERNAME4, UserConstants.EMAIL, "");
        userService.settings(UserConstants.IS_PRIVATE, UserConstants.USERNAME4);

        boolean following = userService.follow(UserConstants.USERNAME3, UserConstants.USERNAME4);
        // because profile is private
        assertFalse(following);

        // test follow request creation
        Collection<User> followRequests = userService.getFollowRequests(UserConstants.USERNAME4);
        assertEquals(1, followRequests.size());

        //accept follow request
        userService.followRequest(UserConstants.USERNAME4,
                UserConstants.USERNAME3,
                UserConstants.ACCEPT_FOLLOW_REQUEST);

        // test if followedUser has new follower
        User followedUser = userService.findByUsername(UserConstants.USERNAME4);
        assertEquals(1, followedUser.getFollowers().size());
        assertEquals(0, followedUser.getFollowRequest().size());

        // test if followerUser has new following
        User followerUser = userService.findByUsername(UserConstants.USERNAME3);
        assertEquals(1, followerUser.getFollowing().size());

    }


}
