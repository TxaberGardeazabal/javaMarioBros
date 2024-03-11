/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.Transform;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author txaber gardeazabal
 */
public class ComplexPrefabWrapper extends Component{
    public List<GameObject> gameObjects = new ArrayList();
    public List<Transform> ofsets = new ArrayList();

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public void addGameObject(GameObject gameObject) {
        this.gameObjects.add(gameObject);
        this.ofsets.add(gameObject.transform);
    }

    public List<Transform> getOfsets() {
        return ofsets;
    }
    
    
}
