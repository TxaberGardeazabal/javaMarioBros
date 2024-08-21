/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.Camera;
import gameEngine.KeyListener;
import gameEngine.MouseListener;
import org.joml.Vector2f;
import util.Settings;

/**
 * Camara en la vista de editor.
 * incluye funcionalidades varias para ayudar a la edicion: como mouse drag, zoom y reset de posicion.
 * @author txaber gardeazabal
 */
public class EditorCamera extends Component{

    private Camera levelEditorCamera;
    private Vector2f clickOrigin;
    private boolean reset = false;
    
    private float lerpTime = 0.0f;
    private float dragSensitivity = 30.0f;
    private float scrollSensitivity = 0.1f;
    private float resetLockThreshold = 0.02f;
    
    private float dragDebounce = 0.032f;
    
    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
        this.clickOrigin = new Vector2f();
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (MouseListener.MouseButtonDown(Settings.CAMERA_PINCH) && dragDebounce > 0) {
            this.clickOrigin = MouseListener.getWorld();
            dragDebounce -= dt;
            return;
        } else if (MouseListener.MouseButtonDown(Settings.CAMERA_PINCH)) {
            Vector2f mousePos = MouseListener.getWorld();
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(dt).mul(dragSensitivity));
            this.clickOrigin.lerp(mousePos, dt);
        }
        
        if (dragDebounce < 0.0f && !MouseListener.MouseButtonDown(Settings.CAMERA_PINCH)) {
            dragDebounce = 0.032f;
        }
        
        if (MouseListener.getScrollY() != 0.0f) {
            float addValue = (float)Math.pow(Math.abs(MouseListener.getScrollY()) * scrollSensitivity, 1 / levelEditorCamera.getZoom());
            addValue *= -Math.signum(MouseListener.getScrollY());
            levelEditorCamera.addZoom(addValue);
        }
        
        if (KeyListener.isKeyPressed(Settings.CAMERA_RESET)) {
            reset = true;
        }
        if (reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(levelEditorCamera.getZoom() +
                    ((1.0f - levelEditorCamera.getZoom()) * lerpTime));
            
            this.lerpTime += 0.1f * dt;
            if (Math.abs(levelEditorCamera.position.x) <= resetLockThreshold && Math.abs(levelEditorCamera.position.y) <= resetLockThreshold 
                    && levelEditorCamera.getZoom() <= 1.0f + resetLockThreshold && levelEditorCamera.getZoom() >= 1.0f - resetLockThreshold) {
                levelEditorCamera.position.set(0f, 0f);
                levelEditorCamera.setZoom(1.0f);
                this.lerpTime = 0.0f;
                reset = false;
            }
        }
    }
}
