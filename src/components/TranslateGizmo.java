/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.MouseListener;
import util.Settings;

/**
 * Componente del sistema de gizmos para mover objetos.
 * la mayoria de funcionalidades estan en la clase Gizmo
 * @author txaber gardeazabal
 */
public class TranslateGizmo extends Gizmo {

    public TranslateGizmo(Sprite arrowSprite, Sprite blockSprite, MouseControls mouseControls) {
        super(arrowSprite, blockSprite, mouseControls);
    }

    private float mouseMoveX = 0.0f;
    private float mouseMoveY = 0.0f;
    private final float MOVERATE = Settings.GRID_WIDTH * 0.1f;
    
    @Override
    public void editorUpdate(float dt) {   
        
        if (this.activeGameObjects != null && !activeGameObjects.isEmpty()) {
            if (xAxisActive && !yAxisActive && !xyAxisActive) {
                moveX();
            } else if (yAxisActive && !xAxisActive && !xyAxisActive) {
                moveY();
            } else if (xyAxisActive && !xAxisActive && !yAxisActive) {
                moveX();
                moveY();
            }
        }
        super.editorUpdate(dt);
    }
    
    /**
     * Mueve el objeto seleccionado y todos sus hijos en el eje X
     */
    private void moveX() {
        mouseMoveX += MouseListener.getWorldDX();
                
        if (mouseMoveX > MOVERATE) {
            mouseMoveX -= MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.translate(MOVERATE, 0);
            }

        } else if (mouseMoveX < -(MOVERATE)) {
            mouseMoveX += MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.translate(-MOVERATE, 0);
            }
        }
    }
    
    /**
     * Mueve el objeto seleccionado y todos sus hijos en el eje Y
     */
    private void moveY() {
        mouseMoveY += MouseListener.getWorldDY();
                
        if (mouseMoveY > MOVERATE) {
            mouseMoveY -= MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.translate(0,MOVERATE);
            }
        } else if (mouseMoveY < -(MOVERATE)) {
            mouseMoveY += MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.translate(0,-MOVERATE);
            }
        }
    }
}
