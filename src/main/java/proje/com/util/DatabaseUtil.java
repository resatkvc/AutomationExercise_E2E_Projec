package proje.com.util;

import proje.com.model.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/testdb";
    private static final String USER = "testuser";
    private static final String PASSWORD = "testpass";

    public static void insertUser(User user) {
        String sql = "INSERT INTO users (title, name, email, password, birth_day, birth_month, birth_year, first_name, last_name, company, address1, address2, country, state, city, zipcode, mobile_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.title);
            pstmt.setString(2, user.name);
            pstmt.setString(3, user.email);
            pstmt.setString(4, user.password);
            pstmt.setString(5, user.birthDay);
            pstmt.setString(6, user.birthMonth);
            pstmt.setString(7, user.birthYear);
            pstmt.setString(8, user.firstName);
            pstmt.setString(9, user.lastName);
            pstmt.setString(10, user.company);
            pstmt.setString(11, user.address1);
            pstmt.setString(12, user.address2);
            pstmt.setString(13, user.country);
            pstmt.setString(14, user.state);
            pstmt.setString(15, user.city);
            pstmt.setString(16, user.zipcode);
            pstmt.setString(17, user.mobileNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertCard(User user) {
        String sql = "INSERT INTO cards (user_email, card_name, card_number, cvc, exp_month, exp_year) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.email);
            pstmt.setString(2, user.cardName);
            pstmt.setString(3, user.cardNumber);
            pstmt.setString(4, user.cvc);
            pstmt.setString(5, user.expMonth);
            pstmt.setString(6, user.expYear);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 