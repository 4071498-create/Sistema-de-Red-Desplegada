/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.red_desplegada;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "planetas")
public class Planeta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_id")
    private int id;

    @Column(name = "nombre_planeta", nullable = false, length = 100)
    private String nombrePlaneta;

    @Column(name = "sector_galactico", nullable = false, length = 50)
    private String sectorGalactico;
    
     // mappedBy: Indica "vete a la clase Nave y busca el atributo privado llamado planetaOrigen"
    // cascade: Si borramos un planeta, podemos arrastrar o desvincular sus naves asociadas
    @OneToMany(mappedBy = "planetaOrigen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<Nave> listaNaves = new java.util.ArrayList<>();

    // Constructor obligatorio JPA
    public Planeta() {}

    public Planeta(String nombrePlaneta, String sectorGalactico) {
        this.nombrePlaneta = nombrePlaneta;
        this.sectorGalactico = sectorGalactico;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombrePlaneta() { return nombrePlaneta; }
    public void setNombrePlaneta(String nombrePlaneta) { this.nombrePlaneta = nombrePlaneta; }
    public String getSectorGalactico() { return sectorGalactico; }
    public void setSectorGalactico(String sectorGalactico) { this.sectorGalactico = sectorGalactico; }
    
    // Getters y Setters de la colección
    public java.util.List<Nave> getListaNaves() { return listaNaves; }
    public void setListaNaves(java.util.List<Nave> listaNaves) { this.listaNaves = listaNaves; }
    
    // MÉTODO HELPER (Práctica recomendada para sincronizar ambos lados de la relación en memoria)
    public void agregarNave(Nave nave) {
        this.listaNaves.add(nave);
        nave.setPlanetaOrigen(this); // Sincroniza la clave ajena de forma automática en la Nave
    }

    @Override
    public String toString() {
        return "Planeta [ID=" + id + ", Nombre=" + nombrePlaneta + ", Sector=" + sectorGalactico + "]";
    }
}