/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.MouseListener;
import util.Settings;

/**
 *
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
    
    private void moveX() {
        mouseMoveX += MouseListener.getWorldDX();
                
        if (mouseMoveX > MOVERATE) {
            mouseMoveX -= MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x += MOVERATE;
            }

        } else if (mouseMoveX < -(MOVERATE)) {
            mouseMoveX += MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.position.x -= MOVERATE;
            }
        }
    }
    
    private void moveY() {
        mouseMoveY += MouseListener.getWorldDY();
                
        if (mouseMoveY > MOVERATE) {
            mouseMoveY -= MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y += MOVERATE;
            }
        } else if (mouseMoveY < -(MOVERATE)) {
            mouseMoveY += MOVERATE;
            for (GameObject go : activeGameObjects) {
                go.transform.position.y -= MOVERATE;
            }
        }
    }
    
    /*private void translateX(GameObject go) {
        go.transform.position.x += MouseListener.getWorldDX();
        for (GameObject go2 : go.getChildGOs()) {
            translateX(go2);
        }
    }
    
    private void translateY(GameObject go) {
        go.transform.position.y += MouseListener.getWorldDY();
        for (GameObject go2 : go.getChildGOs()) {
            translateY(go2);
        }
    }*/
}
