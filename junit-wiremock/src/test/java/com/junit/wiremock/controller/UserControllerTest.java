package com.junit.wiremock.controller;

import com.junit.wiremock.entity.User;
import com.junit.wiremock.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_ShouldReturnUserList() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("João");
        user1.setEmail("joao@email.com");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Maria");
        user2.setEmail("maria@email.com");
        
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@email.com");
        
        when(userService.getUserById(1L)).thenReturn(user);

        ResponseEntity<User> response = userController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("João", response.getBody().getName());
        verify(userService).getUserById(1L);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        User user = new User();
        user.setName("João");
        user.setEmail("joao@email.com");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("João");
        savedUser.setEmail("joao@email.com");
        
        when(userService.createUser(user)).thenReturn(savedUser);

        ResponseEntity<User> response = userController.createUser(user);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(userService).createUser(user);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        User user = new User();
        user.setName("João Atualizado");
        user.setEmail("joao.novo@email.com");
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("João Atualizado");
        updatedUser.setEmail("joao.novo@email.com");
        
        when(userService.updateUser(1L, user)).thenReturn(updatedUser);

        ResponseEntity<User> response = userController.updateUser(1L, user);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("João Atualizado", response.getBody().getName());
        verify(userService).updateUser(1L, user);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        doNothing().when(userService).deleteUser(1L);

        ResponseEntity<Void> response = userController.deleteUser(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(1L);
    }
}
