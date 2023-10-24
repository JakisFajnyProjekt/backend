package com.pl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pl.model.User;
import com.pl.model.dto.UserDTO;
import com.pl.model.dto.UserUpdateDTO;
import com.pl.security.JwtService;
import com.pl.security.Role;
import com.pl.service.UserAuthenticationService;
import com.pl.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @MockBean
    private UserAuthenticationService userAuthenticationService;


    private User user1;
    private User user2;
    private User user3;
    private UserDTO userDTO1;
    private UserDTO userDTO2;
    private UserDTO userDTO3;
    private List<UserDTO> listOfUsers;
    

    @BeforeEach
    public void testData() {
        user1 = new User("firstname1", "lastname", "password", "email@email.com", Role.USER);
        user2 = new User("firstname2", "lastname", "password", "email@email.com", Role.USER);
        user3 = new User("firstname3", "lastname", "password", "email@email.com", Role.USER);
        userDTO1 = new UserDTO("firstname1", "lastname", "password", "email@email.com", Role.USER);
        userDTO2 = new UserDTO("firstname2", "lastname", "password", "email@email.com", Role.USER);
        userDTO3 = new UserDTO("firstname3", "lastname", "password", "email@email.com", Role.USER);
        listOfUsers = List.of(userDTO1, userDTO2, userDTO3);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldFindUserById() throws Exception {
        //Given
        long userId = 1L;

        //When
        when(userService.getUserById(userId)).thenReturn(userDTO1);

        //Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserDTO.class)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(userDTO1.firstName()))
                .andExpect(jsonPath("$.lastName").value(userDTO1.lastName()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldFindListOFUsersFromDb() throws Exception {
        //Given

        //When
        when(userService.list()).thenReturn(listOfUsers);

        //Then

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UserDTO.class)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(3))
                .andExpect(jsonPath("$.[0].firstName").value(userDTO1.firstName()));
    }


    @Test
    @WithMockUser(roles = "USER")
    void shouldDeleteUserWithJwtToken() throws Exception {
        // Given
        long userId = 1L;

        // When
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{userId}", userId)
//                        .with(SecurityMockMvcRequestPostProcessors.user(user1)) <- option 2
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
        // Then
        verify(userService, times(1)).remove(userId);
    }


    @Test
    @WithMockUser(roles = "USER")
    public void shouldModifyUser() throws Exception {
        // Given
        long userId = 1L;
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO("differentFirstName","newLastName","email");

        // When
        mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{userId}", userId)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isAccepted());

        verify(userService).edit(userId, userUpdateDTO);
    }


}