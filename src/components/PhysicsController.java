/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.Window;
import org.joml.Vector2f;
import physics2D.Physics2D;
import physics2D.components.Rigidbody2D;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public abstract class PhysicsController extends Component{
    protected transient Rigidbody2D rb;
    protected transient Vector2f velocity = new Vector2f();
    protected transient Vector2f acceleration = new Vector2f();
    protected Vector2f terminalVelocity = new Vector2f(10f,10f);
    
    protected boolean hasGravity = true;
    protected transient boolean onGround = false;
    protected float innerWidth = 0.25f * 0.7f;
    protected float castVal = -0.14f;
    
    @Override
    public void start() {
        this.rb = gameObject.getComponent(Rigidbody2D.class);
        if (hasGravity) {
            this.acceleration.y = Window.getPhysics().getGravity().y * Settings.worldGravityMul;
        }
        
    }
    
    @Override
    public void update(float dt) {
        this.velocity.x += this.acceleration.x * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rb.setVelocity(velocity);
    }
    
    public void stopAllForces() {
        this.acceleration.zero();
        if (hasGravity) {
            this.acceleration.y = Window.getPhysics().getGravity().y * Settings.worldGravityMul;
        }
        this.velocity.zero();
        this.rb.setVelocity(velocity);
        this.rb.setAngularVelocity(0.0f);
        this.rb.setGravityScale(0.0f);
    }
    
    public void applyForces(float dt) {
        this.velocity.x += this.acceleration.x * dt;
        this.velocity.x = Math.max(Math.min(this.velocity.x, this.terminalVelocity.x), -this.terminalVelocity.x);
        this.velocity.y += this.acceleration.y * dt;
        this.velocity.y = Math.max(Math.min(this.velocity.y, this.terminalVelocity.y), -this.terminalVelocity.y);
        this.rb.setVelocity(velocity);
    }
    
    public void addGravity() {
        this.acceleration.y = Window.getPhysics().getGravity().y * Settings.worldGravityMul; 
    }
    
    public void checkOnGround() {
        onGround = Physics2D.checkOnGround(gameObject, innerWidth, castVal);
    }

    public void setTerminalVelocity(Vector2f terminalVelocity) {
        this.terminalVelocity = terminalVelocity;
    }

    public void setInnerWidth(float innerWidth) {
        this.innerWidth = innerWidth;
    }

    public void setCastVal(float castVal) {
        this.castVal = castVal;
    }
    
    public void setHasGravity(boolean hasGravity) {
        this.hasGravity = hasGravity;
    }
}
