package com.mycompany.red_desplegada;

import org.hibernate.Session;
import org.hibernate.Transaction;
import com.mycompany.red_desplegada.Nave;
import com.mycompany.red_desplegada.HibernateUtil;

public class GestionCRUD {

    public static void main(String[] args) {
        System.out.println("======= SISTEMA DE LOGÍSTICA DE LA FLOTA =======");

        // ====================================================================
        // OP 1: CARGA DE OBJETOS (session.get)
        // ====================================================================
        System.out.println("\n🔍 1. Buscando Nave con ID 1 en PostgreSQL...");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // El método get busca por Clase y Clave Primaria. Retorna null si no existe.
            Nave naveRecuperada = session.get(Nave.class, 1);
            
            if (naveRecuperada != null) {
                // Al recuperarse de la sesión, el objeto entra automáticamente en estado PERSISTENTE
                System.out.println("✔ Nave localizada en el hangar: " + naveRecuperada);
                
                // ============================================================
                // OP 2: MODIFICACIÓN (Dirty Checking de Estado Persistente)
                // ============================================================
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    
                    System.out.println("\n⚙ 2. Modificando estado del sistema a 'Mantenimiento'...");
                    // Cambiamos el atributo en Java puro
                    naveRecuperada.setEstadoSistema("Mantenimiento");
                    
                    // ¡OJO ALUMNOS!: No llamamos a session.update() ni a session.persist(). 
                    // Al estar en estado PERSISTENTE, Hibernate detecta el cambio ("dirty checking")
                    // y generará el UPDATE de SQL de forma automática al hacer commit.
                    tx.commit();
                    System.out.println("✔ Base de datos actualizada dinámicamente.");
                    
                } catch (Exception e) {
                    if (tx != null) tx.rollback();
                    System.err.println("Error en la modificación: " + e.getMessage());
                }
                
            } else {
                System.out.println("❌ No se encontró ninguna nave con el ID especificado.");
            }
        } // Aquí se cierra la sesión automáticamente. El objeto naveRecuperada pasa a estar DETACHED.

        // ====================================================================
        // OP 3: ELIMINACIÓN DE OBJETOS (session.remove)
        // ====================================================================
        System.out.println("\n🚨 3. Iniciando protocolo de desguace (Borrado) para un caza TIE...");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            
            // Requisito para borrar: El objeto debe estar gestionado por la sesión actual.
            // Primero lo buscamos (Suponemos que el ID 5 corresponde a un Caza TIE de las pruebas)
            Nave naveParaBorrar = session.get(Nave.class, 5);
            
            if (naveParaBorrar != null) {
                // remove() marca el objeto para su eliminación física en la BD (antiguamente delete())
                session.remove(naveParaBorrar);
                tx.commit();
                System.out.println("✔ Registro eliminado correctamente de PostgreSQL.");
            } else {
                System.out.println("ℹ No se ejecutó el borrado: El ID 5 no existe en la base de datos.");
                tx.rollback();
            }
        } catch (Exception e) {
            System.err.println("❌ Error en la operación de borrado: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
            System.out.println("=================================================");
        }
    }
}