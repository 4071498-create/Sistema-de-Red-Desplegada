/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.red_desplegada;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Clase de utilidad para gestionar el ciclo de vida de la SessionFactory de Hibernate.
 * Aplica el patrón Singleton para garantizar una única factoría en la aplicación.
 */
public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Inicializa la configuración buscando automáticamente el archivo hibernate.cfg.xml
            sessionFactory = new Configuration().configure().buildSessionFactory();
            System.out.println("✔ [HibernateUtil] SessionFactory inicializada correctamente en el sector.");
        } catch (Throwable ex) {
            System.err.println("❌ Fallo crítico al inicializar la SessionFactory de Hibernate: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        // Cierra los flujos y cachés de forma segura al finalizar la aplicación
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("ℹ [HibernateUtil] SessionFactory cerrada de forma segura.");
        }
    }
}