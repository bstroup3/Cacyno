package com._2_ug_1.cacyno;

import com._2_ug_1.cacyno.config.WebSocketConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@WebAppConfiguration
@ContextConfiguration(classes = WebSocketConfig.class)

class CacynoApplicationTests {

    @Test
    void contextLoads() {
    }

}
