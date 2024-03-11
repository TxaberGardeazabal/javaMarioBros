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
 *
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
    
    private void scaleX() {
        for (GameObject go : activeGameObjects) {
            go.transform.scale.x += MouseListener.getWorldDX();
        }
    }
    
    private void scaleY() {
        for (GameObject go : activeGameObjects) {
            go.transform.scale.y += MouseListener.getWorldDY();
        }
    }
}
