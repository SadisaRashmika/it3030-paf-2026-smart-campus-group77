import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * SmartCampus Development Utility: GetOTP
 * 
 * Usage: 
 * 1. Compile: javac GetOTP.java
 * 2. Run: java -cp ".;smartcampus/lib/*" GetOTP <user_email>
 *    (Note: PostgreSQL JDBC driver must be on the classpath)
 */
public class GetOTP {

    // Database configuration from application.properties
    private static final String URL = "jdbc:postgresql://localhost:5432/smartcampus";
    private static final String USER = "postgres";
    private static final String PASS = "12345678";

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java GetOTP <email>");
            System.exit(1);
        }

        String email = args[0];

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT otp, name, user_id, is_active FROM users WHERE email = ? OR user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, email.toUpperCase());
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String otp = rs.getString("otp");
                        String name = rs.getString("name");
                        String userId = rs.getString("user_id");
                        boolean active = rs.getBoolean("is_active");

                        System.out.println("\n--- SmartCampus OTP Retrieval ---");
                        System.out.println("User:      " + name + " (" + userId + ")");
                        System.out.println("Email:     " + email);
                        System.out.println("Status:    " + (active ? "ACTIVE" : "INACTIVE (Needs Activation)"));
                        System.out.println("---------------------------------");
                        System.out.println("CURRENT OTP: " + (otp != null ? otp : "NONE FOUND"));
                        System.out.println("---------------------------------\n");
                    } else {
                        System.out.println("Error: No user found with email or ID '" + email + "'");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Database Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
