package com.mycompany.red_desplegada;

import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Paso 3: Orquestación del laboratorio (MainLogistica.java).
 * Crea el grafo Planeta -> Naves en memoria, lo persiste desde el padre (cascade)
 * y lo recupera en una sesión limpia navegando por la colección.
 */
public class MainLogistica {

    public static void main(String[] args) {
        System.out.println("======= SISTEMA DE RED DESPLEGADA Y MAPEO BIDIRECCIONAL =======");

        int idPlaneta = 0;
        Transaction tx = null;

        try {
            // ===== SESIÓN 1: Creación y persistencia =====
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                // 1. Instanciamos el Planeta (Estado Transitorio)
                Planeta corellia = new Planeta("Astillero de Corellia", "Mundos del Núcleo");

                // 2. Creamos las dos naves (Estado Transitorio)
                Nave tieBasico = new Nave("Caza TIE Básico", "TIE/LN", 1, "Estable");
                Nave tieBomber = new Nave("TIE Bomber", "TIE/sa", 1, "Estable");

                // 3. Vinculamos ambas naves con el método helper (sincroniza los dos lados del grafo)
                corellia.agregarNave(tieBasico);
                corellia.agregarNave(tieBomber);

                // 4. Persistimos SOLO el planeta: CascadeType.ALL arrastra también las naves
                session.persist(corellia);

                tx.commit();
                idPlaneta = corellia.getId();
                System.out.println("✔ Planeta y flota registrados de forma coordinada: " + corellia);
            }

            // ===== SESIÓN 2: Sesión limpia posterior (sin caché de la sesión anterior) =====
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                System.out.println("\n🔍 Recuperando datos del planeta astillero...");

                // 5. Recuperamos el planeta por su ID
                Planeta planetaDestino = session.get(Planeta.class, idPlaneta);

                if (planetaDestino != null) {
                    System.out.println("\n🌍 PLANETA LOCALIZADO: " + planetaDestino.getNombrePlaneta());
                    System.out.println("🚀 Sector Espacial: " + planetaDestino.getSectorGalactico());

                    System.out.println("\n⏳ Cargando flota amarrada en los muelles de este planeta...");

                    // Al llamar a size() o iterar la lista, Hibernate lanza el SELECT secundario (LAZY)
                    int totalNaves = planetaDestino.getListaNaves().size();
                    System.out.println("📈 Total de naves censadas en el hangar: " + totalNaves);
                    System.out.println("--- Manifiesto de la Flota Desplegada ---");

                    for (Nave n : planetaDestino.getListaNaves()) {
                        System.out.println(" » 🛸 Nave: " + n.getNombre() + " | Modelo: " + n.getModelo()
                                + " | Estado: " + n.getEstadoSistema());
                    }
                } else {
                    System.out.println("❌ No se encontró ningún planeta con el ID " + idPlaneta + " en la base de datos.");
                }
            }

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            System.err.println("❌ Fallo en la orquestación logística: " + e.getMessage());
        } finally {
            HibernateUtil.shutdown();
            System.out.println("==========================================================");
        }
    }
}
