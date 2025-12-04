package be.ucll.repository;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import be.ucll.model.Item;
import be.ucll.model.User;

@Configuration
@Profile("dev")
public class DbInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DbInitializer.class);
  
  @Bean
  public CommandLineRunner initDatabase(
    UserRepository userRepository,
    ItemRepository itemRepository,
    PasswordEncoder passwordEncoder
  ) {
    return args -> {
      if (userRepository.count() == 0) {
        logger.info("Seeding users...");

        User admin = new User(
          "admin",
          "admin@resqfood.com",
          passwordEncoder.encode("Admin123%")
        );

        User testUser = new User(
          "john_doe",
          "john.doe@example.com",
          passwordEncoder.encode("johnDoe123%")
        );

        User sqmmi3 = new User(
          "sqmmi3",
          "sqmmi3@resqfood.com",
          passwordEncoder.encode("Sqmmi3123%")
        );

        userRepository.saveAll(List.of(admin, testUser, sqmmi3));

        logger.info("Users seeded!");
      }

      if (itemRepository.count() == 0) {
        logger.info("Seeding items...");

        Item milk = new Item("Milk Everyday", Item.Category.DAIRY, 2, LocalDate.now().plusDays(7), "Full fat milk");
        Item bread = new Item("Bread Moregrains", Item.Category.GRAIN, 1, LocalDate.now().plusDays(5), "Whole grain bread");
        Item eggs = new Item("Eggs Boni 6x", Item.Category.PROTEIN, 6, LocalDate.now().plusDays(21), "Brown eggs");
        Item cheese = new Item("Cheese Jong Everyday", Item.Category.DAIRY, 500, LocalDate.now().plusDays(30), "Aged cheese");

        itemRepository.saveAll(List.of(milk, bread, eggs, cheese));

        logger.info("Items seeded!");
      }
    };
  }
}
