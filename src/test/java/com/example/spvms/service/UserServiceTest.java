package com.example.spvms.service;

import com.example.spvms.dto.UserDto;
import com.example.spvms.model.User;
import com.example.spvms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    void create_Success() {
        UserDto dto = new UserDto();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("password");
        dto.setIsActive(true);

        User saved = new User();
        saved.setId(1L);
        saved.setName("John Doe");

        when(repository.existsByEmail("john@example.com")).thenReturn(false);
        when(repository.save(any(User.class))).thenReturn(saved);

        User result = service.create(dto);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(repository).save(any(User.class));
    }

    @Test
    void create_DuplicateEmail_ThrowsException() {
        UserDto dto = new UserDto();
        dto.setEmail("duplicate@example.com");

        when(repository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.create(dto));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void getAll_Success() {
        List<User> users = Arrays.asList(new User(), new User());

        when(repository.findAll()).thenReturn(users);

        List<User> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAll();
    }

    @Test
    void getById_Found() {
        Long id = 1L;
        User user = new User();
        user.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(user));

        User result = service.getById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        Long id = 999L;

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getById(id));
    }

    @Test
    void update_Success() {
        Long id = 1L;
        UserDto dto = new UserDto();
        dto.setName("Updated Name");
        dto.setEmail("updated@example.com");
        dto.setPassword("newpassword");
        dto.setIsActive(false);

        User existing = new User();
        existing.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(User.class))).thenReturn(existing);

        User result = service.update(id, dto);

        assertNotNull(result);
        verify(repository).save(existing);
    }

    @Test
    void updateStatus_Success() {
        Long id = 1L;
        Boolean newStatus = false;

        User user = new User();
        user.setId(id);
        user.setIsActive(true);

        when(repository.findById(id)).thenReturn(Optional.of(user));
        when(repository.save(any(User.class))).thenReturn(user);

        User result = service.updateStatus(id, newStatus);

        assertNotNull(result);
        assertEquals(newStatus, result.getIsActive());
        verify(repository).save(user);
    }

    @Test
    void delete_Success() {
        Long id = 1L;

        when(repository.existsById(id)).thenReturn(true);
        doNothing().when(repository).deleteById(id);

        service.delete(id);

        verify(repository).deleteById(id);
    }

    @Test
    void delete_NotFound_ThrowsException() {
        Long id = 999L;

        when(repository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(id));
        verify(repository, never()).deleteById(id);
    }
}
