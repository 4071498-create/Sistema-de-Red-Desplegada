package com.mycompany.red_desplegada;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.mycompany.red_desplegada.Nave;
import com.mycompany.red_desplegada.HibernateUtil;

public class TestPersistencia {

    public static void main(String[] args) {
        System.out.println("======= CONECTANDO TERMINAL REBELDE A HIBERNATE =======");

        // 1. Instanciamos el objeto Java puro (Estado: Transitorio / Transient)
        Nave halcon = new Nave("Halcón Milenario", "YT-1300", 6, "Critico");

        // Declaramos la Session y la Transaction
        Transaction tx = null;

        // 2. Abrimos la sesión usando try-with-resources para garantizar el cierre seguro
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // Iniciamos la transacción (Obligatorio en Hibernate para operaciones de escritura DML)
            tx = session.beginTransaction();

            System.out.println("⏳ Persistiendo la nave en la base de datos relacional...");
            
            // El método persist() guarda el objeto (antiguamente save())
            session.persist(halcon);

            // Confirmamos los cambios en PostgreSQL
            tx.commit();
            
            System.out.println("✔ Transmisión guardada con éxito.");
            // Demostración del fin del desfase: ¡El objeto Java ya tiene el ID asignado por Postgres!
            System.out.println("🚀 ID generado automáticamente por PostgreSQL: " + halcon.getId());
            System.out.println(halcon);

        } catch (Exception e) {
            // Si algo falla (p.ej. error de contraseña), deshacemos los cambios para proteger la BD
            if (tx != null) {
                tx.rollback();
            }
            System.err.println("❌ Error en la persistencia del bloque: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerramos la factoría al terminar el hilo principal
            HibernateUtil.shutdown();
            System.out.println("======================================================");
        }
    }
}