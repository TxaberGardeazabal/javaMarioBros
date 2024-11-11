/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.StateMachine;
import gameEngine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import physics2D.RaycastInfo;
import render.DebugDraw;
import util.AssetPool;

/**
 * bloques de ladrillos que se rompen al golpear
 * @author txaber gardeazabal
 */
public class BreakableBrick extends Block {

    private transient boolean isBroken = false;
    private transient float timeToBreak = 0.2f;
    
    @Override
    public void hit(boolean shouldOpen) {
        if (shouldOpen) {
            AssetPool.getSound("assets/sounds/break_block.ogg").play();
            
            if (this.gameObject.getComponent(StateMachine.class) != null) {
                this.gameObject.getComponent(StateMachine.class).trigger("break");
            }
            tm.stop();
            isBroken = true;
        }
        
        // detect an enemy on top 
        Vector2f raycastBegin = new Vector2f(this.gameObject.transform.position);
        raycastBegin.sub(0.145f / 2.0f, 0f);
        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, 0.2f);
        
        RaycastInfo info = Window.getPhysics().raycast(this.gameObject, raycastBegin, raycastEnd);
        
        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(0.145f, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(0.145f, 0.0f);
        
        RaycastInfo info2 = Window.getPhysics().raycast(this.gameObject, raycast2Begin, raycast2End);
        
        // fix this for debugging only
        DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1,0,0));
        DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1,0,0));

        if (info.hit && info.hitObj != null && info.hitObj.getComponent(Enemy.class) != null) {
            info.hitObj.getComponent(Enemy.class).die();
        }
    }
    
    @Override
    public void update(float dt) {
        if (isBroken) {
            timeToBreak -= dt;
            if (timeToBreak <= 0) {
                gameObject.destroy();
            }
        }
    }
}
