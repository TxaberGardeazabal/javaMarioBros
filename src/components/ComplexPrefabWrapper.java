/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.Transform;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;

/**
 * Clase de ayuda para poder añadir multiples objetos a la vez al nivel desde prefabs.
 * Se hace una lista con todos los objetos que forman parte de un grupo para poder interacutar
 * simultaneamente con todos
 * @author txaber gardeazabal
 */
public class ComplexPrefabWrapper extends Component{
    private List<GameObject> gameObjects = new ArrayList();
    private List<Transform> ofsets = new ArrayList();
    private Transform anchor;
    private boolean first = true;

    /**
     * Devuelve todas las referencias a GOs
     * @return la lista con los GOs
     */
    public List<GameObject> getGameObjects() {
        return gameObjects;
    }
    
    /**
     * Devuelve todos los ofsets de los GOs relativos al GO de esta clase
     * @return la lista con los transform relativos paralelas a la lista de GOs
     */
    public List<Transform> getOfsets() {
        return ofsets;
    }

    /**
     * Añade una GO a la lista
     * @param gameObject objeto a añadir a la lista
     */
    public void addGameObject(GameObject gameObject) {
        this.gameObjects.add(gameObject);
        if (first) {
            anchor = new Transform(gameObject.transform.position);
            Transform t = gameObject.transform.copy();
            t.position.zero();
            this.ofsets.add(t);
        } else {
            Transform t = gameObject.transform.copy();
            t.position.sub(anchor.position);
            this.ofsets.add(t);
        }
        
    }
}
