/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.Window;

/**
 *
 * @author txaber gardeazabal
 */
public class FixedHUD extends Component{
    
    private GameCamera gc;
    
    @Override
    public void start() {
        GameObject camera = Window.getScene().getGameObjectWith(GameCamera.class);
        if (camera != null) {
            gc = camera.getComponent(GameCamera.class);
        }
    }
    
    @Override
    public void lateUpdate(float dt) {
        if (gc != null) {
            float Dx = gc.getDeltaX();
            float Dy = gc.getDeltaY();
            
            this.gameObject.transform.position.x += Dx;
            this.gameObject.transform.position.y += Dy;
            
        }
    }
}
