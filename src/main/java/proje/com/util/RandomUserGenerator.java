package proje.com.util;

import proje.com.model.User;
import java.util.Random;
import java.util.UUID;

public class RandomUserGenerator {
    private static final String[] titles = {"Mr", "Mrs"};
    private static final String[] countries = {"India", "Turkey", "USA", "Germany", "France"};
    private static final Random random = new Random();

    public static User generate() {
        User user = new User();
        user.title = titles[random.nextInt(titles.length)];
        user.name = "TestUser" + UUID.randomUUID().toString().substring(0, 6);
        user.email = "test" + UUID.randomUUID().toString().substring(0, 8) + "@mail.com";
        user.password = "Pass" + random.nextInt(10000) + "!";
        user.birthDay = String.valueOf(1 + random.nextInt(28));
        user.birthMonth = String.valueOf(1 + random.nextInt(12));
        user.birthYear = String.valueOf(1970 + random.nextInt(30));
        user.firstName = "First" + random.nextInt(1000);
        user.lastName = "Last" + random.nextInt(1000);
        user.company = "Company" + random.nextInt(1000);
        user.address1 = "Street " + random.nextInt(1000);
        user.address2 = "Apt " + random.nextInt(100);
        user.country = countries[random.nextInt(countries.length)];
        user.state = "State" + random.nextInt(100);
        user.city = "City" + random.nextInt(100);
        user.zipcode = String.valueOf(10000 + random.nextInt(90000));
        user.mobileNumber = "5" + (100000000 + random.nextInt(900000000));
        // Kart bilgileri
        user.cardName = user.firstName + " " + user.lastName;
        user.cardNumber = String.valueOf(4000000000000000L + (long)(random.nextDouble() * 100000000000000L));
        user.cvc = String.format("%03d", random.nextInt(1000));
        user.expMonth = String.format("%02d", 1 + random.nextInt(12));
        user.expYear = String.valueOf(2025 + random.nextInt(5));
        return user;
    }
} 