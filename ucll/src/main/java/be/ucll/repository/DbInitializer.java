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
import be.ucll.model.Household;
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
    HouseholdRepository householdRepository,
    PasswordEncoder passwordEncoder
  ) {
    return args -> {
      if (householdRepository.count() == 0) {
        logger.info("Seeding households...");

        Household household = new Household("88BB90");

        householdRepository.save(household);
      }

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

        Household household = householdRepository.findByInviteCode("88BB90").orElseThrow(() -> new DomainException("Household not found."));

        sqmmi3.setHousehold(household);

        userRepository.saveAll(List.of(admin, testUser, sqmmi3));

        logger.info("Users seeded!");
      }

      if (itemRepository.count() == 0) {
        logger.info("Seeding items...");

        Item steak = new Item("Steak", Type.PROTEIN);
        Item mozzarella = new Item("Mozzarella Everyday", Type.DAIRY);
        Item nutella = new Item("Nutella", Type.PANTRY);
        Item tuna = new Item("Tuna Everyday", Type.CANNED);

        Item milk = new Item("Milk Everyday", Type.DAIRY);
        Item bread = new Item("Bread Moregrains", Type.GRAIN);
        Item eggs = new Item("Eggs Boni 6x", Type.PROTEIN);
        Item cheese = new Item("Cheese Jong Everyday", Type.DAIRY);

        itemRepository.saveAll(List.of(milk, bread, eggs, cheese, steak, mozzarella, nutella, tuna));

        logger.info("Items seeded!");
      }

      if (userItemRepository.count() == 0) {
        logger.info("Seeding user_items");

        User sqmmi3 = userRepository.findByUsername("sqmmi3")
          .orElseThrow(() -> new RuntimeException("User sqmmi3 not found."));

        User testUser = userRepository.findByUsername("john_doe")
          .orElseThrow(() -> new RuntimeException("User john_doe not found."));
        
        String itemNotFoundMessage = "Item not found.";

        Item milk = itemRepository.findByNameContainingIgnoreCase("Milk Everyday").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        Item bread = itemRepository.findByNameContainingIgnoreCase("Bread Moregrains").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        Item eggs = itemRepository.findByNameContainingIgnoreCase("Eggs Boni 6x").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        Item cheese = itemRepository.findByNameContainingIgnoreCase("Cheese Jong Everyday").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        Item steak = itemRepository.findByNameContainingIgnoreCase("Steak").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        Item mozzarella = itemRepository.findByNameContainingIgnoreCase("Mozzarella Everyday").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        Item nutella = itemRepository.findByNameContainingIgnoreCase("Nutella").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        Item tuna = itemRepository.findByNameContainingIgnoreCase("Tuna Everyday").orElseThrow(() -> new DomainException(itemNotFoundMessage));

        UserItem sqmmi3milk1 = new UserItem(sqmmi3, milk, LocalDate.now().plusDays(24), LocalDate.now().minusDays(4), 5);
        UserItem sqmmi3milk2 = new UserItem(sqmmi3, milk, LocalDate.now().plusDays(24));
        UserItem sqmmi3milk3 = new UserItem(sqmmi3, milk, LocalDate.now().plusDays(28));
        UserItem sqmmi3bread1 = new UserItem(sqmmi3, bread, LocalDate.now().plusDays(5), LocalDate.now().minusDays(2), 5);
        UserItem sqmmi3eggs1 = new UserItem(sqmmi3, eggs, LocalDate.now().plusDays(7), LocalDate.now().minusDays(5), 5);
        UserItem sqmmi3cheese1 = new UserItem(sqmmi3, cheese, LocalDate.now().plusDays(48));
        UserItem sqmmi3cheese2 = new UserItem(sqmmi3, cheese, LocalDate.now().plusDays(64));
        UserItem sqmmi3cheese3 = new UserItem(sqmmi3, cheese, LocalDate.now().plusDays(32), LocalDate.now(), 7);
        UserItem johnsteak1 = new UserItem(testUser, steak, LocalDate.now().plusDays(6), 1);
        UserItem johnsteak2 = new UserItem(testUser, steak, LocalDate.now().plusDays(7), 1);
        UserItem johnmozzarella1 = new UserItem(testUser, mozzarella, LocalDate.now().plusDays(24));
        UserItem johnmozzarella2 = new UserItem(testUser, mozzarella, LocalDate.now().plusDays(24));
        UserItem johnnutella1 = new UserItem(testUser, nutella, LocalDate.now().plusDays(467), 93);
        UserItem johntuna1 = new UserItem(testUser, tuna, LocalDate.now().plusDays(467));

        userItemRepository.saveAll(List.of(sqmmi3milk1, sqmmi3milk2, sqmmi3milk3, sqmmi3bread1, sqmmi3eggs1, sqmmi3cheese1, sqmmi3cheese2, sqmmi3cheese3, johnsteak1, johnsteak2, johnmozzarella1, johnmozzarella2, johnnutella1, johntuna1));

        logger.info("User_Item s seeded!");
      }
    };
  }
}
