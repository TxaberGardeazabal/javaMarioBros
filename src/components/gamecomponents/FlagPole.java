/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import gameEngine.GameObject;
import gameEngine.Window;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 *
 * @author txaber gardeazabal
 */
public class FlagPole extends Component{
    private boolean isTop = false;
    

    public FlagPole(boolean isTop) {
        this.isTop = true;
    }
    
    @Override
    public void beginCollision(GameObject obj, Contact contact, Vector2f normal) {
        PlayerController pb = obj.getComponent(PlayerController.class);
        if (pb != null) {
            pb.playWinAnimation(this.gameObject);
            Window.getScene().getGameObjectByName("flag").getComponent(Flag.class).reachFlag();
        }
    }
    
}
