/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import components.PhysicsController;
import editor.ConsoleWindow;
import gameEngine.GameObject;
import gameEngine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2D.RaycastInfo;
import render.DebugDraw;

/**
 *
 * @author txaber gardeazabal
 */
public class HoleLogic extends Component{
    
    private final float levelSize = 50;
    private Vector2f levelEndPos;
    
    @Override
    public void start() {
        this.levelEndPos = new Vector2f(this.gameObject.transform.position.x + levelSize,this.gameObject.transform.position.y);
    }
    
    @Override
    public void update(float dt) {
        
        RaycastInfo info = Window.getPhysics().raycast(this.gameObject, this.gameObject.transform.position,levelEndPos);

        if (info.hit) {
            //ConsoleWindow.addLog("found object: "+info.hitObj.name, ConsoleWindow.LogCategory.info);
            
            GameObject objeto = info.hitObj;
            if (objeto.getComponent(PlayerController.class) != null) {
                PlayerController pc = objeto.getComponent(PlayerController.class);
                if (!pc.isDead()) {
                    pc.die();
                }
                
                PhysicsController phC = objeto.getComponent(PhysicsController.class);
                phC.stopAllForces();
                
            } else if (objeto.getComponent(Enemy.class) != null) {
                objeto.destroy();
            } else if (objeto.getComponent(Fireball.class) != null) {
                objeto.getComponent(Fireball.class).delete();
            }
        }
    }
    
    @Override
    public void editorUpdate(float dt) {
        DebugDraw.addLine2D( this.gameObject.transform.position, levelEndPos, new Vector3f(1,0,0));
    }
}
