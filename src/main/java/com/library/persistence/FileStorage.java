package com.library.persistence;

import com.library.domain.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileStorage {

    private static final String USER_FILE = "users.txt";

    // ✅ تحميل المستخدمين من الملف
    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // ✅ حفظ المستخدمين في الملف
    public static void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ✅ إضافة مستخدم جديد
    public static void addUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users);
    }

    // ✅ التحقق من وجود مستخدم (بناء على الإيميل)
    public static User findUserByEmail(String email) {
        return loadUsers().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
}
