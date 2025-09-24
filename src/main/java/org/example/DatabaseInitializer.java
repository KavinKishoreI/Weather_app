package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Database initialization utility to set up the schema and test data
 */
public class DatabaseInitializer {
    
    public static void main(String[] args) {
        System.out.println(" Initializing Weather App Database...");
        
        try {
            // Initialize Hibernate configuration
            Configuration config = new Configuration().configure();
            config.addAnnotatedClass(User.class);
            config.addAnnotatedClass(UserPreference.class);
            config.addAnnotatedClass(SavedLocation.class);
            
            SessionFactory sessionFactory = config.buildSessionFactory();
            Session session = sessionFactory.openSession();
            
            try {
                session.beginTransaction();
                
                // Create a test user
                User testUser = new User("demo@example.com", "demo123", "Demo User");
                session.persist(testUser);
                
                // Add some test preferences
                UserPreference unitsPref = new UserPreference("units", "metric", testUser);
                session.persist(unitsPref);
                
                // Add some test locations
                SavedLocation london = new SavedLocation("London, UK", 51.5072, -0.1276, testUser);
                SavedLocation newYork = new SavedLocation("New York, US", 40.7128, -74.0060, testUser);
                session.persist(london);
                session.persist(newYork);
                
                session.getTransaction().commit();
                
                System.out.println("✅ Database initialized successfully!");
                System.out.println("📊 Created test user: demo@example.com / demo123");
                System.out.println("📍 Added sample locations: London, New York");
                
            } catch (Exception e) {
                if (session.getTransaction() != null) {
                    session.getTransaction().rollback();
                }
                System.err.println("❌ Error initializing database: " + e.getMessage());
                e.printStackTrace();
            } finally {
                session.close();
                sessionFactory.close();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to database: " + e.getMessage());
            System.err.println("Please check your database connection settings in hibernate.cfg.xml");
            e.printStackTrace();
        }
    }
}