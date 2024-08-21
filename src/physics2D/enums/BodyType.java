/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package physics2D.enums;

/**
 * Tipos de cuerpos fisicos de Box2D.
 * Static: no posee ni recibe fuerzas fisicas de otros cuerpos.
 * Dynamic: posee fuerzas fisicas y es afectado por otros cuerpos de la simulacion.
 * Kinematic: posee fuerzas fisicas pero no es afectado por otros cuerpos, se puede añadir fuerzas por codigo.
 * @author txaber gardeazabal
 */
public enum BodyType {
    Static,
    Dynamic,
    Kinematic
}
