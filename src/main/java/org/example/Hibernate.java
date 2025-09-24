package org.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Hibernate{
    public static void main(String[] args) {
        SessionFactory factory = new Configuration().configure().buildSessionFactory();
        Session session = factory.openSession();

        session.beginTransaction();
        session.persist(new User("demo@example.com", "demo123", "Demo User"));
        session.getTransaction().commit();

        session.close();
        factory.close();

        System.out.println("✅ User inserted into Oracle DB!");
    }
}