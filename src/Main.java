import Bootstrapers.AuditBootstrapper;
import Bootstrapers.DatabaseBootstrapper;
import Entities.AuctionEntity;
import Entities.BidderEntity;
import Entities.ProductEntity;
import Entities.SellerEntity;
import Entities.UserEntity;
import Services.AuctionService;
import Services.ProductService;
import Services.UserService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void showMenu(UserEntity userEntity) {
        if (userEntity == null) {
            return;
        }

        if (userEntity instanceof SellerEntity) {
            System.out.println("1. Create a product and place it in auction.");
            System.out.println("2. Read all products details in alphabetical order.");
            System.out.println("3. Delete a product and delete it from auction.");
            System.out.println("4. Update a product's details.");
            System.out.println("Please choose from [1-4] or type STOP to exit: ");
            return;
        }

        System.out.println("1. Check all auctions and your bid history.");
        System.out.println("2. Bid for a product in an active auction.");
        System.out.println("3. Check your current winning bids.");
        System.out.println("Please choose from [1-3] or type STOP to exit: ");
    }
    public static void main(String[] args) {
        // let's suppose that we pass by parameters by arguments the user and email check for some hashing
        DatabaseBootstrapper databaseInstance = DatabaseBootstrapper.getInstance(
                "jdbc:mysql://localhost:3306/auctions", "root", ""
        );
        AuditBootstrapper auditInstance = AuditBootstrapper.getInstance("data/system_audit.csv");

        // the four services
        UserService userService = UserService.getInstance(databaseInstance);
        ProductService productService = ProductService.getInstance(databaseInstance);
        AuctionService auctionService = AuctionService.getInstance(databaseInstance);

        // prima functionalitate autentificarea unui utilizator.
        UserEntity authenticatedUser = null;
        try {
            authenticatedUser = userService.loginUser(args[0], args[1]);
            auditInstance.addNewAuditLog(authenticatedUser.getUserName() + " s-a autentificat cu succes!");

            String accountType = authenticatedUser instanceof BidderEntity ? "bidder" : "seller";
            System.out.printf("[AUCTION]: Hello %s(TYPE: %s) you have logged in with success!\n\n", authenticatedUser.getUserName(), accountType);
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        // a doua functionalitate crearea unor noi licitatii daca in baza de date sunt expirate
        try {
            List<AuctionEntity> availableAuctions = auctionService.getAvailableAuctions(ZonedDateTime.now());

            if (availableAuctions.size() == 0) {
                ZonedDateTime start = ZonedDateTime.now(),
                              finish = start.plusDays(2);

                auctionService.create(Map.of(
                "startingTime", start,
                "stoppageTime", finish,
                "name", "English-style Auction from %s - %s".formatted(start.toLocalDateTime(), finish.toLocalDateTime()),
                "description", "Here you can find the english auction",
                "location", "online",
                "type", "english",
                "incremental", 1
                ));

                auctionService.create(Map.of(
                "startingTime", start,
                "stoppageTime", finish,
                "name", "Dutch-style Auction from %s - %s".formatted(start.toLocalDateTime(), finish.toLocalDateTime()),
                "description", "Here you can find the Dutch auction",
                "location", "online",
                "type", "dutch",
                "minimum", 1
                ));
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }

        // 4 functionalitati daca utilizatorul e de tip seller sa poata adauga detalii despre obiecte
        Scanner scanner = new Scanner(System.in);
        String currentScanner = null; long currentOption;

        while (currentScanner == null || !currentScanner.equalsIgnoreCase("stop")) {
            showMenu(authenticatedUser); currentScanner = scanner.nextLine();

            try {
                currentOption = Long.parseLong(currentScanner);

                switch ((int) currentOption) {
                case 1 -> {
                    ProductEntity productEntity = new ProductEntity();
                    productEntity.setProductSeller((SellerEntity) authenticatedUser);

                    System.out.println("Please insert the name of the product: ");
                    currentScanner = scanner.nextLine();
                    productEntity.setProductName(currentScanner);

                    System.out.println("Please insert the description of the product: ");
                    currentScanner = scanner.nextLine();
                    productEntity.setProductDescription(currentScanner);

                    System.out.println("Please insert the starting price of the product: ");
                    Double price = scanner.nextDouble();
                    productEntity.setProductStartingPrice(price);

                    productService.create(Map.of(
                        "name", productEntity.getProductName(),
                        "description", productEntity.getProductDescription(),
                        "startingPrice", productEntity.getProductStartingPrice(),
                        "userId", productEntity.getProductSeller().getUserId()
                    ));
                }
                case 2 -> {
                }
                case 3 -> {
                }
                case 4 -> {
                }
                }
            } catch (NumberFormatException exception) {
                if (currentScanner.equalsIgnoreCase("stop")) {
                    continue;
                }
                System.out.printf("[ERROR] Incorrect choice, exception: %s.\n", exception.getMessage());
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }


//        try {
//            auditInstance.addNewAuditLog("test action");
//        } catch (IOException exception) {
//            System.out.println(exception);
//        }
//
//        System.out.println("Hello world!");
    }
}