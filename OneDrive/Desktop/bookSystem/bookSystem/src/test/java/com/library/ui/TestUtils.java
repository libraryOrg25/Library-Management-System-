package com.library.ui;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

public class TestUtils {

   
    public static Component getComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {

            if (name.equals(c.getName())) {
                return c;
            }

            if (c instanceof Container) {
                Component found = getComponentByName((Container) c, name);
                if (found != null) return found;
            }
        }
        return null;
    }

    
    public static JButton getButton(JFrame frame, String name) {
        return (JButton) getComponentByName(frame.getContentPane(), name);
    }

    
    public static Object getField(Object instance, String fieldName) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            return f.get(instance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
