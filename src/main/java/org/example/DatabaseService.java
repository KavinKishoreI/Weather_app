package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DatabaseService {
    private static DatabaseService instance;
    private final SessionFactory sessionFactory;
    
    private DatabaseService() {
        Configuration config = new Configuration().configure();
        config.addAnnotatedClass(User.class);
        config.addAnnotatedClass(UserPreference.class);
        config.addAnnotatedClass(SavedLocation.class);
        this.sessionFactory = config.buildSessionFactory();
    }
    
    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }
    
    public User authenticateUser(String email, String password) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery(
                "FROM User WHERE email = :email AND password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password); // In production, use hashed passwords
            
            User user = query.uniqueResult();
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                session.beginTransaction();
                session.merge(user);
                session.getTransaction().commit();
            }
            return user;
        }
    }
    
    public User registerUser(String email, String password, String displayName) {
        try (Session session = sessionFactory.openSession()) {
            // Check if user already exists
            Query<User> existingUserQuery = session.createQuery(
                "FROM User WHERE email = :email", User.class);
            existingUserQuery.setParameter("email", email);
            
            if (existingUserQuery.uniqueResult() != null) {
                throw new RuntimeException("User with email " + email + " already exists");
            }
            
            User newUser = new User(email, password, displayName);
            session.beginTransaction();
            session.persist(newUser);
            session.getTransaction().commit();
            return newUser;
        }
    }
    
    public void saveUserPreference(Long userId, String key, String value) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            
            // Check if preference already exists
            Query<UserPreference> existingQuery = session.createQuery(
                "FROM UserPreference WHERE user.id = :userId AND key = :key", UserPreference.class);
            existingQuery.setParameter("userId", userId);
            existingQuery.setParameter("key", key);
            
            UserPreference existing = existingQuery.uniqueResult();
            
            session.beginTransaction();
            if (existing != null) {
                existing.setValue(value);
                session.merge(existing);
            } else {
                UserPreference preference = new UserPreference(key, value, user);
                session.persist(preference);
            }
            session.getTransaction().commit();
        }
    }
    
    public String getUserPreference(Long userId, String key) {
        try (Session session = sessionFactory.openSession()) {
            Query<UserPreference> query = session.createQuery(
                "FROM UserPreference WHERE user.id = :userId AND key = :key", UserPreference.class);
            query.setParameter("userId", userId);
            query.setParameter("key", key);
            
            UserPreference preference = query.uniqueResult();
            return preference != null ? preference.getValue() : null;
        }
    }
    
    public void saveLocation(Long userId, String locationName, double latitude, double longitude) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.get(User.class, userId);
            if (user == null) {
                throw new RuntimeException("User not found");
            }
            
            SavedLocation location = new SavedLocation(locationName, latitude, longitude, user);
            session.beginTransaction();
            session.persist(location);
            session.getTransaction().commit();
        }
    }
    
    public List<SavedLocation> getUserLocations(Long userId) {
        try (Session session = sessionFactory.openSession()) {
            Query<SavedLocation> query = session.createQuery(
                "FROM SavedLocation WHERE user.id = :userId ORDER BY createdAt DESC", SavedLocation.class);
            query.setParameter("userId", userId);
            return query.list();
        }
    }
    
    public void deleteLocation(Long locationId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            SavedLocation location = session.get(SavedLocation.class, locationId);
            if (location != null) {
                session.remove(location);
            }
            session.getTransaction().commit();
        }
    }
    
    public void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
