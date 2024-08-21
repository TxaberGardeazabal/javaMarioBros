/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package physics2D;

import components.Component;
import gameEngine.GameObject;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;

/**
 * Implementacion de la clase contactListener de box2D.
 * Controla los datos de interacciones entre dos objetos fisicos del mundo, lo que significa que ocurre cuando dos objetos fisicos se tocan
 * @author txaber gardeazabal
 */
public class ContactListener2D implements ContactListener{

    @Override
    public void beginContact(Contact cntct) {
        GameObject objA = (GameObject)cntct.getFixtureA().getUserData();
        GameObject objB = (GameObject)cntct.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        cntct.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x,worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();
        
        for (Component c : objA.getAllComponents()) {
            c.beginCollision(objB, cntct, aNormal);
        }
        for (Component c : objB.getAllComponents()) {
            c.beginCollision(objA, cntct, bNormal);
        }
    }

    @Override
    public void endContact(Contact cntct) {
        GameObject objA = (GameObject)cntct.getFixtureA().getUserData();
        GameObject objB = (GameObject)cntct.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        cntct.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x,worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();
        
        for (Component c : objA.getAllComponents()) {
            c.endCollision(objB, cntct, aNormal);
        }
        for (Component c : objB.getAllComponents()) {
            c.endCollision(objA, cntct, bNormal);
        }    
    }

    @Override
    public void preSolve(Contact cntct, Manifold mnfld) {
        GameObject objA = (GameObject)cntct.getFixtureA().getUserData();
        GameObject objB = (GameObject)cntct.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        cntct.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x,worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();
        
        for (Component c : objA.getAllComponents()) {
            c.preSolve(objB, cntct, aNormal);
        }
        for (Component c : objB.getAllComponents()) {
            c.preSolve(objA, cntct, bNormal);
        }    
    }

    @Override
    public void postSolve(Contact cntct, ContactImpulse ci) {
        GameObject objA = (GameObject)cntct.getFixtureA().getUserData();
        GameObject objB = (GameObject)cntct.getFixtureB().getUserData();
        WorldManifold worldManifold = new WorldManifold();
        cntct.getWorldManifold(worldManifold);
        Vector2f aNormal = new Vector2f(worldManifold.normal.x,worldManifold.normal.y);
        Vector2f bNormal = new Vector2f(aNormal).negate();
        
        for (Component c : objA.getAllComponents()) {
            c.postSolve(objB, cntct, aNormal);
        }
        for (Component c : objB.getAllComponents()) {
            c.postSolve(objA, cntct, bNormal);
        }    
    }
    
}
