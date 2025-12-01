package com.library.ui;

import com.library.persistence.FileStorage;
import com.library.domain.Book;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;

import static org.mockito.Mockito.*;

public class AdminDashboardTest {

    @Test
    void testAddBookCallsFileStorage() {

        try (MockedStatic<FileStorage> mock = Mockito.mockStatic(FileStorage.class)) {

            AdminDashboard ui = new AdminDashboard();

            JTextField title = (JTextField) TestUtils.getField(ui, "titleField");
            JTextField author = (JTextField) TestUtils.getField(ui, "authorField");
            JTextField isbn = (JTextField) TestUtils.getField(ui, "isbnField");
            JTextField type = (JTextField) TestUtils.getField(ui, "typeField");
            JTextField copies = (JTextField) TestUtils.getField(ui, "copiesField");

            title.setText("Java");
            author.setText("Someone");
            isbn.setText("111");
            type.setText("Book");
            copies.setText("5");

            JButton addBtn = TestUtils.getButton(ui, "ADD");
            addBtn.doClick();

            mock.verify(() -> FileStorage.addBook(any(Book.class)), times(1));
        }
    }
}
