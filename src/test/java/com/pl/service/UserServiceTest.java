package com.pl.service;

import com.pl.exception.NotFoudException;
import com.pl.model.User;
import com.pl.model.dto.UserDTO;
import com.pl.repository.UserRepository;
import com.pl.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private Map<String, Object> update;
    private Map<String, Object> updateWithNull;
    private UserDTO userDTO1;
    private List<User> userList;

    @BeforeEach
    void testData() {
        user1 = new User("firstName_user1",
                "lastName_user1", "email_user1", "123456789qwerty_user1", Role.USER);

        userList = List.of(
                user2 = new User("firstName_user2",
                        "lastName_user2", "email_user2", "123456789qwerty_user2", Role.USER),
                user3 = new User("firstName_user3",
                        "lastName_user3", "email_user3", "123456789qwerty_user3", Role.USER),
                user4 = new User("firstName_user4",
                        "lastName_user4", "email_user4", "123456789qwerty_user4", Role.USER)
        );

        userDTO1 = new UserDTO("firstName_dto",
                "lastName_dto", "email", "123456789qwerty", Role.USER);
        update = new HashMap<>();
        update.put("firstName", "firstName_dto");
        update.put("lastName", "lastName_dto");
        updateWithNull = new HashMap<>();
        updateWithNull.put("firstName", null);
        updateWithNull.put("lastName", null);
    }

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldFindUserByGivenId() {
        //Given --> testData
        User saveUser = userRepository.save(user1);

        //When
        UserDTO userById = userService.getUserById(saveUser.getId());

        //Then
        assertEquals("firstName_user1", userById.firstName());
        assertEquals("lastName_user1", userById.lastName());
    }

    @Test
    void shouldHandleExceptionUserNotFoundTryingRetriveUserById() {
        //Given --> testData
        long nonExistingUserId = 1;

        //When
        NotFoudException notFoudException = assertThrows(NotFoudException.class,
                () -> userService.getUserById(nonExistingUserId));

        //Then
        String expectedMessage = "User not found with given id " + nonExistingUserId;
        String actualMessage = notFoudException.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void shoudlFindAllUsersFromDb() {
        //Given
        userRepository.saveAll(userList);

        //When
        List<UserDTO> listOfAllUsers = userService.getListOfAllUsers();

        //Then
        int expectedSizeOfList = 3;
        assertEquals(expectedSizeOfList, listOfAllUsers.size());
    }

    @Test
    void shouldReturnEmptyListIfNoUsersInDb() {
        //Given
        int expectedSize = 0;

        //When
        List<UserDTO> listOfAllUsers = userService.getListOfAllUsers();

        //Then
        assertEquals(expectedSize, listOfAllUsers.size());
    }

    @Test
    void shouldDeleteUserFromDb() {
        //Given
        List<User> userSavingList = userRepository.saveAll(userList);
        Long idOfUserForDelete = userSavingList.get(1).getId();

        //When
        int expectedSizeBeforeDelete = 3;
        int expectedSizeAfterDelete = 2;
        int sizeBeforeDeletingUser = userRepository.findAll().size();
        userService.deleteUserFromDb(idOfUserForDelete);
        int sizeAfterDeletingUser = userRepository.findAll().size();

        //Then
        assertEquals(expectedSizeBeforeDelete, sizeBeforeDeletingUser);
        assertEquals(expectedSizeAfterDelete, sizeAfterDeletingUser);
    }

    @Test
    void shoudlHandleNotFoundExceptionWhileTryingToDeleteByWrongId() {
        //Given
        long nonexistingUserId = 100;

        //Whne
        NotFoudException userNotFound = assertThrows(NotFoudException.class,
                () -> userService.deleteUserFromDb(nonexistingUserId));
        String expectedMessage = "User not found with given id " + nonexistingUserId;
        String meesageFromException = userNotFound.getMessage();

        assertTrue(meesageFromException.contains(expectedMessage));
    }

    @Test
    void shouldModifyExistingUser() {
        // Given
        User savedUser = userRepository.save(user1);
        Long idOfUserInDb = savedUser.getId();

        // When
        UserDTO modifyUser = userService.editUser(idOfUserInDb, update);

        // Then
        assertEquals("firstName_dto", modifyUser.firstName());
        assertEquals("lastName_dto", modifyUser.lastName());

    }

    @Test
    void shouldHandleExceptionWhileTryingToFindUserForUpdate() {
        //Given
        long nonExistingId = 12;

        //When
        String expectedMessage = "User Not found";
        NotFoudException notFoudException = assertThrows(NotFoudException.class,
                () -> userService.editUser(nonExistingId, update));
        String notFoundExceptionMessage = notFoudException.getMessage();

        //Then
        assertTrue(notFoundExceptionMessage.contains(expectedMessage));
    }

    @Test
    void shouldAcceptOnlyNonNullValuesWhileUpdatingUser() {
        //Given
        User savedUser = userRepository.save(user1);
        Long savedUserId = savedUser.getId();

        //When
        UserDTO updateWithNulls = userService.editUser(savedUserId, updateWithNull);

        //The
        assertEquals("firstName_user1", updateWithNulls.firstName());
        assertEquals("lastName_user1", updateWithNulls.lastName());
    }


}