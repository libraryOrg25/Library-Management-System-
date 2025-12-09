package com.library.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IEmailServiceTest {

    @Test
    void testInterfaceHasSendMethod() throws Exception {
        // نتأكد أن الميثود موجودة
        assertNotNull(
            IEmailService.class.getMethod("send",
                    String.class, String.class, String.class)
        );
    }
}
