package com.library.service;

import java.util.*;
import com.library.domain.User;
import com.library.persistence.FileStorage;

public class AuthService {

    public AuthService() {
        // intentionally empty – we no longer cache users here
    }

    /**
     * Always reload fresh users from storage
     */
    private List<User> loadUsersFresh() {
        return FileStorage.loadUsers();
    }

    /**
     * Register new user
     */
    public boolean register(String username, String email, String password, String role) {

        List<User> users = loadUsersFresh();

        // check if email already exists
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }

        // add new user
        users.add(new User(username, email, password, role));

        // save updated list
        FileStorage.saveUsers(users);

        return true;
    }

    /**
     * Login user
     */
    public User login(String email, String password) {

        List<User> users = loadUsersFresh();

        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) &&
                u.getPassword().equals(password)) {
                return u;
            }
        }

        return null;
    }
}
