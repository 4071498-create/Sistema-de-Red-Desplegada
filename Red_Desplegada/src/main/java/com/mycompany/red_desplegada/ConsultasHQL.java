package com.mycompany.red_desplegada;

import org.hibernate.Session;
import org.hibernate.query.Query;
import com.mycompany.red_desplegada.Nave;
import com.mycompany.red_desplegada.HibernateUtil;
import java.util.List;

public class ConsultasHQL {

    public static void main(String[] args) {
        System.out.println("======= SISTEMA DE INTELIGENCIA TÁCTICA HQL =======");

        // ====================================================================
        // EJEMPLO 1: LISTADO COMPLETO (Equivalente orientado a objetos de SELECT *)
        // ====================================================================
        System.out.println("\n🛰 1. Recuperando el censo completo de la flota galáctica...");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // ¡OJO ALUMNOS!: Apuntamos a la clase 'Nave' (con la N mayúscula), NO a la tabla 'naves'
            String hqlTodos = "FROM Nave"; 
            
            Query<Nave> queryTodos = session.createQuery(hqlTodos, Nave.class);
            List<Nave> flotaCompleta = queryTodos.getResultList();
            
            for (Nave nave : flotaCompleta) {
                System.out.println(nave);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el escaneo general HQL: " + e.getMessage());
        }

        // ====================================================================
        // EJEMPLO 2: CONSULTA FILTRADA CON PARÁMETROS (:estado)
        // ====================================================================
        String estadoBuscado = "Critico";
        System.out.println("\n🚨 2. Alerta Táctica: Filtrando naves en estado [" + estadoBuscado + "]...");
        
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // Usamos marcadores de posición con nombre (:estadoRef) para evitar inyecciones SQL
            String hqlFiltro = "FROM Nave n WHERE n.estadoSistema = :estadoRef";
            
            Query<Nave> queryFiltro = session.createQuery(hqlFiltro, Nave.class);
            // Asignamos el parámetro de forma fuertemente tipada
            queryFiltro.setParameter("estadoRef", estadoBuscado);
            
            List<Nave> navesPeligrosas = queryFiltro.getResultList();
            
            System.out.println("--- Reporte de Amenazas / Sistemas Críticos ---");
            if (!navesPeligrosas.isEmpty()) {
                for (Nave naveCritica : navesPeligrosas) {
                    System.out.println("⚠ OBJETIVO -> ID: " + naveCritica.getId() 
                                     + " | Modelo: " + naveCritica.getModelo() 
                                     + " | Nombre: " + naveCritica.getNombre());
                }
            } else {
                System.out.println("ℹ Todos los sistemas operan con normalidad en este sector.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en el filtrado perimetral HQL: " + e.getMessage());
        } finally {
            // Cerramos de forma definitiva los flujos de la factoría
            HibernateUtil.shutdown();
            System.out.println("======================================================");
        }
    }
}