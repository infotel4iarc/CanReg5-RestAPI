package fr.iarc.canreg.restapi;

import canreg.server.database.CanRegDAO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("junit")
class CanRegApiApplicationTests {

    @MockBean
    private CanRegDAO canRegDAO;
    
    @Test
    void contextLoads() {
    }

}
