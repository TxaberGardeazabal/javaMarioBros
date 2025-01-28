/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package physics2D;

import gameEngine.GameObject;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;
import physics2D.components.Box2DCollider;
import physics2D.components.Rigidbody2D;

/**
 * Wrapper para la informacion devuelta por el rayCast
 * @author txaber gardeazabal
 */
public class RaycastInfo implements RayCastCallback{
    public Fixture fixture;
    public Vector2f point;
    public Vector2f normal;
    public float fraction;
    public boolean hit;
    public GameObject hitObj;
    private GameObject requestingObj;
    
    public RaycastInfo(GameObject go) {
        fixture = null;
        point = new Vector2f();
        normal = new Vector2f();
        fraction = 0.0f;
        hit = false;
        hitObj = null;
        requestingObj = go;
    }
    
    @Override
    public float reportFixture(Fixture fxtr, Vec2 point, Vec2 normal, float fraction) {
        if (fxtr.getUserData().equals(requestingObj)) {
            return 1;
        }
        GameObject tmp = (GameObject)fxtr.getUserData();
        try {
            if (tmp.getComponent(Rigidbody2D.class).isSensor()) {
                return 1;
            }
        }
        catch (NullPointerException e) {}
        finally {
            this.fixture = fxtr;
            this.point = new Vector2f(point.x, point.y);
            this.normal = new Vector2f(normal.x, normal.y);
            this.fraction = fraction;
            this.hit = fraction != 0;
            this.hitObj = (GameObject)fxtr.getUserData();
        }
        
        return fraction;
    }
    
}
