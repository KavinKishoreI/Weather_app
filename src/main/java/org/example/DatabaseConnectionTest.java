package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Simple database connection test
 */
public class DatabaseConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("🔍 Testing database connection...");
        
        try {
            Configuration config = new Configuration().configure();
            config.addAnnotatedClass(User.class);
            config.addAnnotatedClass(UserPreference.class);
            config.addAnnotatedClass(SavedLocation.class);
            
            SessionFactory sessionFactory = config.buildSessionFactory();
            Session session = sessionFactory.openSession();
            
            // Test basic connection
            session.beginTransaction();
            session.createNativeQuery("SELECT 1 FROM DUAL").getSingleResult();
            session.getTransaction().commit();
            
            System.out.println("✅ Database connection successful!");
            System.out.println("📋 Database URL: " + config.getProperty("hibernate.connection.url"));
            System.out.println("👤 Username: " + config.getProperty("hibernate.connection.username"));
            
            session.close();
            sessionFactory.close();
            
        } catch (Exception e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nPlease check:");
            System.err.println("1. Oracle database is running");
            System.err.println("2. Connection details in hibernate.cfg.xml are correct");
            System.err.println("3. Database user has proper permissions");
            e.printStackTrace();
        }
    }
}
