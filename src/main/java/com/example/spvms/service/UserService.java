package com.example.spvms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.spvms.dto.UserDto;
import com.example.spvms.model.User;
import com.example.spvms.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /* CREATE */
    public User create(UserDto dto) {

        if (repository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setIsActive(dto.getIsActive());

        return repository.save(user);
    }

    /* GET ALL */
    public List<User> getAll() {
        return repository.findAll();
    }

    /* GET BY ID */
    public User getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /* UPDATE */
    public User update(Long id, UserDto dto) {
        User user = getById(id);

        user.setName(dto.getName());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setIsActive(dto.getIsActive());

        return repository.save(user);
    }

    /* UPDATE STATUS */
    public User updateStatus(Long id, Boolean status) {
        User user = getById(id);
        user.setIsActive(status);
        return repository.save(user);
    }

    /* DELETE (HARD DELETE) */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
