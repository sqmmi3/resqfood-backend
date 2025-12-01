package be.ucll;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
	"jwt.secret=Y8r3mP9wQ2tF6sV1xB7eH4kN0uJ5cR8Z",
	"jwt.expiration=3600000"
})
class ResqfoodApplicationTests {

	@Test
	void contextLoads() {
	}

}
