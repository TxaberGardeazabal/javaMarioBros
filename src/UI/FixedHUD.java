/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import components.Component;
import components.GameCamera;
import editor.ConsoleWindow;
import gameEngine.GameObject;
import gameEngine.Window;
import org.joml.Vector2f;

/**
 * Clase para componentes del HUD principal, que mantienen su posicion relativa a la camara
 * @author txaber gardeazabal
 */
public class FixedHUD extends Component{
    
    private GameCamera gc;
    
    @Override
    public void start() {
        GameObject camera = Window.getScene().getGameObjectWith(GameCamera.class);
        if (camera != null) {
            gc = camera.getComponent(GameCamera.class);
            System.out.println("camera added");
        }
    }
    
    @Override
    public void lateUpdate(float dt) {
        if (gc != null) {
            float Dx = gc.getDeltaX();
            float Dy = gc.getDeltaY();
            
            //this.gameObject.transform.position.x += Dx;
            //this.gameObject.transform.position.y += Dy;
            System.out.println(Dx);
            this.gameObject.transform.translate(new Vector2f(Dx,Dy));
        }
    }
}
