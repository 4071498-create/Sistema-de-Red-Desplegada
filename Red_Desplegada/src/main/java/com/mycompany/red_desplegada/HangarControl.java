/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.red_desplegada;

import org.hibernate.query.Query;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author aleja
 */
public class HangarControl {

    public static void main(String[] args) {

    }

    public static void registrarNuevaNave(Nave nave) {
        System.out.println("======= CONECTANDO TERMINAL REBELDE A HIBERNATE =======");
        
        // Declaramos la Session y la Transaction
        Transaction tx = null;

        // 2. Abrimos la sesión usando try-with-resources para garantizar el cierre seguro
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Iniciamos la transacción (Obligatorio en Hibernate para operaciones de escritura DML)
            tx = session.beginTransaction();

            System.out.println("⏳ Persistiendo la nave en la base de datos relacional...");

            // El método persist() guarda el objeto (antiguamente save())
            session.persist(nave);

            // Confirmamos los cambios en PostgreSQL
            tx.commit();

            System.out.println("✔ Transmisión guardada con éxito.");
            // Demostración del fin del desfase: ¡El objeto Java ya tiene el ID asignado por Postgres!
            System.out.println("🚀 ID generado automáticamente por PostgreSQL: " + nave.getId());
            System.out.println(nave);

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
    public static void actualizarEstadoNave(int idNave, String nuevoEstado){
         System.out.println("======= SISTEMA DE LOGÍSTICA DE LA FLOTA =======");

        // ====================================================================
        // OP 1: CARGA DE OBJETOS (session.get)
        // ====================================================================
        System.out.println("\n🔍 1. Buscando Nave con ID "+idNave+" en PostgreSQL...");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // El método get busca por Clase y Clave Primaria. Retorna null si no existe.
            Nave naveRecuperada = session.get(Nave.class, idNave);
            
            if (naveRecuperada != null) {
                // Al recuperarse de la sesión, el objeto entra automáticamente en estado PERSISTENTE
                System.out.println("✔ Nave localizada en el hangar: " + naveRecuperada);
                
                // ============================================================
                // OP 2: MODIFICACIÓN (Dirty Checking de Estado Persistente)
                // ============================================================
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    
                    System.out.println("\n⚙ 2. Modificando estado del sistema a '"+nuevoEstado+"'...");
                    // Cambiamos el atributo en Java puro
                    naveRecuperada.setEstadoSistema(nuevoEstado);
                    
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
    }
    public static List<Nave> obtenerCazasPorModelo(String modeloBuscado) {
    System.out.println("======= BUSCANDO CAZAS POR MODELO (HQL) =======");
    
    // Usamos try-with-resources para asegurar el cierre de la sesión
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        
        // 1. Escribimos la consulta HQL apuntando a la clase Nave y su atributo Java 'modelo'
        String hql = "FROM Nave WHERE modelo = :modeloParam";
        
        // 2. Creamos la query tipada indicando la clase de retorno (Nave.class)
        Query<Nave> query = session.createQuery(hql, Nave.class);
        
        // 3. Asignamos el valor al parámetro con seguridad (evita inyecciones SQL)
        query.setParameter("modeloParam", modeloBuscado);
        
        // 4. Ejecutamos y obtenemos la lista de resultados
        List<Nave> listaCazas = query.getResultList();
        
        System.out.println("✔ Búsqueda completada. Naves encontradas: " + listaCazas.size());
        return listaCazas;
        
    } catch (Exception e) {
        System.err.println("❌ Error al consultar las naves por modelo: " + e.getMessage());
        e.printStackTrace();
        return null; // O puedes retornar una lista vacía: new ArrayList<>()
    } finally {
        System.out.println("================================================");
    }
}
}
