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
     * Añade una GO a la lista
     * @param gameObject objeto a añadir a la lista
     */
    public void addGameObject(GameObject gameObject) {
        // the first object passed becomes the anchor point (0,0) for the rest
        // of objects, then they are save separately from the level
        if (first) {
            anchor = new Transform(gameObject.transform.position);
            GameObject newObj = gameObject.copy();
            newObj.transform.setPosition(new Vector2f(0,0));
            this.gameObjects.add(newObj);
            first = false;
        } else {
            GameObject newObj = gameObject.copy();
            newObj.transform.position.sub(anchor.position);
            this.gameObjects.add(newObj);
        }
        
    }
}
