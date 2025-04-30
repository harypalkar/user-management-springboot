
package com.example.usermanagement.service;

import com.example.usermanagement.model.User;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }


    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }


    public Optional<User> findById(Long id) {
        return userRepo.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    public User createUser(User user) {
        return userRepo.save(user);
    }

    public User updateUser(Long userId, User updatedUser) {
        Optional<User> optionalUser = userRepo.findById(userId);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            if (updatedUser.getName() != null && !updatedUser.getName().isEmpty()) {
                existingUser.setName(updatedUser.getName());
            }
            List<String> updatedLanguages = updatedUser.getLanguagesKnow();

            if (updatedLanguages != null) {
                if (existingUser.getLanguagesKnow() == null || existingUser.getLanguagesKnow().isEmpty()) {
                    existingUser.setLanguagesKnow(updatedLanguages);
                } else {
                    for (String language : updatedLanguages) {
                        if (!existingUser.getLanguagesKnow().contains(language)) {
                            existingUser.getLanguagesKnow().add(language);
                        }
                    }
                }
            }
            return userRepo.save(existingUser);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Optional<User> logoutUser(Long userId) {
        return userRepo.findById(userId);
    }

    public void saveUser(User user) {
        userRepo.save(user);
    }

    public boolean deleteUser(Long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean authenticateUser(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        return userOptional.isPresent() && userOptional.get().getPassword().equals(password);
    }
}
