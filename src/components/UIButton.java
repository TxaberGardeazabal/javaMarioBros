/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.MouseListener;
import gameEngine.Window;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

/**
 *
 * @author txaber gardeazabal
 */
public class UIButton extends Component {

    private transient boolean isPressed;
    private transient boolean isHovered;
    private transient boolean isHolding;
    private transient SpriteRenderer spr;
    
    @Override
    public void start() {
        spr = this.gameObject.getComponent(SpriteRenderer.class);
        if (spr == null) {
            System.out.println("Warning: null sprite in object "+this.gameObject.name);
        }
    }
    
    @Override
    public void update(float dt) {
        updateVariables();
        /*
        System.out.println("isPressed: "+isPressed);
        System.out.println("isHovered: "+isHovered);
        System.out.println("isHolding: "+isHolding);*/
    }
    
    public void updateVariables() {
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
                    onClick();
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
            onHold();
        }
        if (isHovered) {
            onHover();
        }
        
    }
    
    public void onClick() {}
    
    public void onHover() {
        
    }
    
    public void onHold() {
        
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
    
    
}
