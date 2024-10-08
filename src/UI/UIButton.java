/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import components.Component;
import components.SpriteRenderer;
import editor.ConsoleWindow;
import gameEngine.GameObject;
import gameEngine.MouseListener;
import gameEngine.Window;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 * Objeto interactuable del UI, permite detectar acciones del raton y ejecutar funciones en base a la accion recibida
 * @author txaber gardeazabal
 */
public class UIButton extends Component {

    private transient boolean isPressed;
    private transient boolean isHovered;
    private transient boolean isHolding;
    private transient SpriteRenderer spr;
    
    private ButtonBehavior buttonBehavior;
    
    @Override
    public void start() {
        spr = this.gameObject.getComponent(SpriteRenderer.class);
        if (spr == null) {
            ConsoleWindow.addLog("null sprite in object "+this.gameObject.name,
                    ConsoleWindow.LogCategory.warning);
        }
    }
    
    @Override
    public void update(float dt) {
        
        int x = (int)MouseListener.getScreenX();
        int y = (int)MouseListener.getScreenY();
        int goId = Window.getPickingTexture().readPixel(x, y);
        GameObject pickObj = Window.getScene().getGameObject(goId);
        
        if (pickObj != null && pickObj.equals(this.gameObject)) {
            
            
            onHoverStart();
            if (MouseListener.MouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                // si el anterior frame estaba el boton siendo pulsado retirar el press
                // para no lanzar el evento varias veces, se vuelve un hold
                if (!isPressed && !isHolding) {
                    isPressed = true;
                    this.buttonBehavior.onClick();
                    onHoldStart();
                } else {
                    isPressed = false;
                }
            }
        } else {
            onHoverEnd();
            
        }
        
        if (isHolding && !MouseListener.MouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            onHoldEnd();
        }
        
        if (isHolding){
            this.buttonBehavior.onHold();
        }
        if (isHovered) {
            this.buttonBehavior.onHover();
        }
    }
    
    public void onHoverStart() {
        isHovered = true;
    }
    
    public void onHoverEnd() {
        isHovered = false;
    }
    
    public void onHoldStart() {
        isHolding = true;
    }
    
    public void onHoldEnd() {
        isHolding = false;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public boolean isHolding() {
        return isHolding;
    }

    public ButtonBehavior getButtonBehavior() {
        return buttonBehavior;
    }

    public void setButtonBehavior(ButtonBehavior buttonBehavior) {
        this.buttonBehavior = buttonBehavior;
    }
    
    
}
