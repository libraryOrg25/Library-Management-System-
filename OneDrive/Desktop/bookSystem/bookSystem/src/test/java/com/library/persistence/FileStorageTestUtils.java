package com.library.persistence;

import com.library.domain.User;
import org.mockito.Mockito;

import java.util.List;

public class FileStorageTestUtils {

    public static void mockLoadUsers(List<User> users) {
        Mockito.mockStatic(FileStorage.class).when(FileStorage::loadUsers).thenReturn(users);
    }
}
