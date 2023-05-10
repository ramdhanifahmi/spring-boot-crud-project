package com.dev.fahmi.service;

import com.dev.fahmi.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDetailsService {
    Optional<User> findByUsername(String username);
    public User saveUser(User user);
    public List<User> getAllUsers();
    public User getUserById(Long id);
    public void deleteUserById(Long id);
}
