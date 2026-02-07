package com.junit.wiremock.service;

import com.junit.wiremock.entity.User;
import com.junit.wiremock.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("João");
        user1.setEmail("joao@email.com");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Maria");
        user2.setEmail("maria@email.com");
        
        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@email.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("João", result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        User user = new User();
        user.setName("João");
        user.setEmail("joao@email.com");
        
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("João");
        savedUser.setEmail("joao@email.com");
        
        when(userRepository.save(user)).thenReturn(savedUser);

        User result = userService.createUser(user);

        assertNotNull(result.getId());
        assertEquals("João", result.getName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_WhenUserExists_ShouldUpdateAndReturnUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("João");
        existingUser.setEmail("joao@email.com");
        
        User updateData = new User();
        updateData.setName("João Atualizado");
        updateData.setEmail("joao.novo@email.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        User result = userService.updateUser(1L, updateData);

        assertEquals("João Atualizado", result.getName());
        assertEquals("joao.novo@email.com", result.getEmail());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteUser() {
        User user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@email.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).deleteById(1L);
    }
}
