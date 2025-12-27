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

import be.ucll.exception.DomainException;
import be.ucll.model.Item;
import be.ucll.model.User;
import be.ucll.model.UserItem;
import be.ucll.model.Item.Type;

@Configuration
@Profile("dev")
public class DbInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DbInitializer.class);

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            ItemRepository itemRepository,
            UserItemRepository userItemRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                logger.info("Seeding users...");

                User admin = new User(
                        "admin",
                        "admin@resqfood.com",
                        passwordEncoder.encode("Admin123%"));

                User testUser = new User(
                        "john_doe",
                        "john.doe@example.com",
                        passwordEncoder.encode("johnDoe123%"));

                User sqmmi3 = new User(
                        "sqmmi3",
                        "sqmmi3@resqfood.com",
                        passwordEncoder.encode("Sqmmi3123%"));

                userRepository.saveAll(List.of(admin, testUser, sqmmi3));

                logger.info("Users seeded!");
            }

            if (itemRepository.count() == 0) {
                logger.info("Seeding items...");

                Item milk = new Item("Milk Everyday", Type.DAIRY);
                Item bread = new Item("Bread Moregrains", Type.GRAIN);
                Item eggs = new Item("Eggs Boni 6x", Type.PROTEIN);
                Item cheese = new Item("Cheese Jong Everyday", Type.DAIRY);

                itemRepository.saveAll(List.of(milk, bread, eggs, cheese));

                logger.info("Items seeded!");
            }

            if (userItemRepository.count() == 0) {
                logger.info("Seeding user_items");

                User sqmmi3 = userRepository.findByUsername("sqmmi3")
                        .orElseThrow(() -> new RuntimeException("User sqmmi3 not found."));

                String itemNotFoundMessage = "Item not found.";

                Item milk = itemRepository.findByNameContainingIgnoreCase("Milk Everyday")
                        .orElseThrow(() -> new DomainException(itemNotFoundMessage));

                Item bread = itemRepository.findByNameContainingIgnoreCase("Bread Moregrains")
                        .orElseThrow(() -> new DomainException(itemNotFoundMessage));

                Item eggs = itemRepository.findByNameContainingIgnoreCase("Eggs Boni 6x")
                        .orElseThrow(() -> new DomainException(itemNotFoundMessage));

                Item cheese = itemRepository.findByNameContainingIgnoreCase("Cheese Jong Everyday")
                        .orElseThrow(() -> new DomainException(itemNotFoundMessage));

                UserItem sqmmi3milk1 = new UserItem(sqmmi3, milk, LocalDate.now().plusDays(24), LocalDate.now(), 6);
                UserItem sqmmi3milk2 = new UserItem(sqmmi3, milk, LocalDate.now().plusDays(24));
                UserItem sqmmi3milk3 = new UserItem(sqmmi3, milk, LocalDate.now().plusDays(28));
                UserItem sqmmi3bread1 = new UserItem(sqmmi3, bread, LocalDate.now().plusDays(5), LocalDate.now(), 4);
                UserItem sqmmi3eggs1 = new UserItem(sqmmi3, eggs, LocalDate.now().plusDays(16));
                UserItem sqmmi3cheese1 = new UserItem(sqmmi3, cheese, LocalDate.now().plusDays(48));
                UserItem sqmmi3cheese2 = new UserItem(sqmmi3, cheese, LocalDate.now().plusDays(64));
                UserItem sqmmi3cheese3 = new UserItem(sqmmi3, cheese, LocalDate.now().plusDays(32), LocalDate.now(), 8);

                userItemRepository.saveAll(List.of(sqmmi3milk1, sqmmi3milk2, sqmmi3milk3, sqmmi3bread1, sqmmi3eggs1,
                        sqmmi3cheese1, sqmmi3cheese2, sqmmi3cheese3));

                logger.info("User_Item s seeded!");
            }
        };
    }
}
