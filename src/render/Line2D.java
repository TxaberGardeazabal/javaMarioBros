/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package render;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Crea una linea para poder mostrarla en DebugDraw.
 * Esta clase es el equivalente al LineRenderer de unity, igual necesita cambiar el nombre
 * @author txaber gardeazabal
 */
public class Line2D {
    private Vector2f start;
    private Vector2f end;
    private Vector3f color;
    private int lifetime;

    public Line2D(Vector2f start, Vector2f end, Vector3f color, int lifetime) {
        this.start = start;
        this.end = end;
        this.color = color;
        this.lifetime = lifetime;
    }

    public Vector2f getStart() {
        return start;
    }

    public Vector2f getEnd() {
        return end;
    }

    public Vector3f getColor() {
        return color;
    }
    
    public int beginFrame() {
        lifetime--;
        return lifetime;
    }
}
