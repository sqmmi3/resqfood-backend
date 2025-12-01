package be.ucll;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"jwt.secret=test_secret_1234567890_test_secret_key",
	"jwt.expiration=3600000"
})
class ResqfoodApplicationTests {

	@Test
	void contextLoads() {
	}

}
