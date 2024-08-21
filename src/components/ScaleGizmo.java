/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import components.Gizmo;
import editor.PropertiesWindow;
import gameEngine.GameObject;
import gameEngine.MouseListener;

/**
 * Componente del sistema de gizmos para escalar objetos.
 * la mayoria de funcionalidades estan en la clase Gizmo
 * @author txaber gardeazabal
 */
public class ScaleGizmo extends Gizmo{
    public ScaleGizmo(Sprite scaleSprite, Sprite blockSprite, MouseControls mouseControls) {
        super(scaleSprite, blockSprite, mouseControls);
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (this.activeGameObjects != null && !activeGameObjects.isEmpty()) {
            if (xAxisActive && !yAxisActive && !xyAxisActive) {
                scaleX();
            } else if (yAxisActive && !xAxisActive && !xyAxisActive) {
                scaleY();
            } else if (xyAxisActive && !yAxisActive && !xAxisActive) {
                scaleX();
                scaleY();
            }
        }
        super.editorUpdate(dt);
    }
    
    /**
     * Escala el objeto seleccionado y todos sus hijos en el eje X
     */
    private void scaleX() {
        for (GameObject go : activeGameObjects) {
            go.transform.scale(MouseListener.getWorldDX(), 0);
        }
    }
    
    /**
     * Escala el objeto seleccionado y todos sus hijos en el eje Y
     */
    private void scaleY() {
        for (GameObject go : activeGameObjects) {
            go.transform.scale(0,MouseListener.getWorldDY());
        }
    }
}
