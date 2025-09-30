package com.ecommerce.service;

import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.entity.User;
import org.springframework.security.core.userdetails.*;
import org.springframework.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.List;

@Transactional // prvent the half executed operations
@Service

public class UserService implements UserDetailsService {
    @Autowired //it can automatically overwrite the all methods in UserRepository interface method.
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User savUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }  

    public User registerUser(User user) throws Exception {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new Exception("Username is already taken");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("Email is already in use");
        }
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) throws Exception {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (!existingUserOpt.isPresent()) {
            throw new Exception("User not found");
        }
        User existingUser = existingUserOpt.get();
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        // Update other fields as necessary
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) throws Exception {
        if (!userRepository.existsById(id)) {
            throw new Exception("User not found");
        }
        userRepository.deleteById(id);
    }

    public User createUser(User user) {
        if(userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if(userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public updateUser(Long id, User updateUser) {
        User existingUser = userRepository.findById(id).orElseThrow(
            () -> new RuntimeException("User not found")
        );
        
        if(existingUser.getUsername() != updateUser.getUsername() && userRepository.existsByUsername(updateUser.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if(existingUser.getEmail() != updateUser.getEmail() && userRepository.existsByEmail(updateUser.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        existingUser.setUsername(updateUser.getUsername());
        existingUser.setEmail(updateUser.getEmail());

        if(updateUser.getPassword() != null && !updateUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateUser.getPassword()));
        }
        return userRepository.save(existingUser);
    }
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), List.of());
    }

}
