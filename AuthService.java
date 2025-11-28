package com.library.service;


import java.util.*;
import com.library.domain.User;
import com.library.persistence.FileStorage;

/**
 * Provides registration and login functionality for users.
 */
public class AuthService {

    private List<User> users;

    public AuthService() {
        // تحميل المستخدمين من الملف عند بدء التشغيل
        users = FileStorage.loadUsers();
    }

    /**
     * تسجيل مستخدم جديد
     * @return true إذا تم التسجيل بنجاح، false إذا الإيميل مستخدم مسبقاً
     */
    public boolean register(String username, String email, String password, String role) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return false; // الإيميل موجود مسبقاً
            }
        }

        users.add(new User(username, email, password, role));
        FileStorage.saveUsers(users);
        return true;
    }

    /**
     * تسجيل الدخول باستخدام الإيميل وكلمة المرور
     * @return كائن User إذا تم تسجيل الدخول بنجاح، null إذا فشل
     */
    public User login(String email, String password) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }
}