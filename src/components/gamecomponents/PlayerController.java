/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.PhysicsController;
import components.StateMachine;
import components.TransitionMachine;
import components.propertieComponents.Ground;
import gameEngine.Direction;
import gameEngine.GameObject;
import gameEngine.KeyListener;
import gameEngine.Prefab;
import gameEngine.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2D.components.PillboxCollider;
import physics2D.components.Rigidbody2D;
import physics2D.enums.BodyType;
import scene.LevelSceneInitializer;
import util.AssetPool;
import util.Settings;

/**
 * Controles del jugador
 * @author txaber gardeazabal
 */
public class PlayerController extends PhysicsController {
    
    public enum PlayerState {
        Small,
        Big,
        Fire
    }
    
    public float walkSpeed = 1.8f;
    public float sprintSpeed = 3.0f;
    public float accel = 3.0f;
    public float jumpBoost = 1.0f;
    public float slowdownForce = 0.05f;
    
    private PlayerState playerState = PlayerState.Small;
    public transient boolean isInvincible = false;
    public transient boolean disableForces = false;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.1f;
    private transient StateMachine stateMachine;
    private transient TransitionMachine transitionMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime = 0;
    private transient boolean isDead = false;
    private transient boolean isHurtInvincible = false;
    private transient int enemyBounce = 0;
    
    private transient int invicivilityFrames = 1000;
    private transient int invicivilityFramesLeft = 0;
    private transient float blinkTime = 0.0f;
    private transient int transformFrames = 100;
    private transient int transformFramesLeft = 0;
    private transient int invincBlinkFrames = 0;
    
    /**
     * Esta mario en su estado de victoria
     * @return true si mario esta en su estado de victoria, false de lo contrario
     */
    public boolean hasWon() {
        // technically when mario wins inmediatly starts its winAnimation
        return playWinAnimation;
    }
    
    @Override
    public void start() {
        super.start();
        setInnerWidth(playerWidth * 0.6f);
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        this.transitionMachine = gameObject.getComponent(TransitionMachine.class);
        this.rb.setGravityScale(0.0f);
        
        Fireball.reset();
    }
    
    /*@Override
    public void editorUpdate(float dt) {
        super.update(dt);
    }*/
    
    @Override
    public void update(float dt) {
        if (isDead) {
            dieAnimation(dt);
            return;
        }
        
        if (playWinAnimation) {
            checkOnGround();
            if (!onGround) {
                gameObject.transform.scale.x = -0.25f;
                gameObject.transform.position.y -= dt;
                stateMachine.trigger("startCimbing");
            } else {
                if (this.walkTime > 0) {
                    gameObject.transform.scale.x = 0.25f;
                    gameObject.transform.position.x += dt;
                    stateMachine.trigger("toWalk");
                }
                AssetPool.getSound("assets/sounds/stage_clear.ogg").playIfNotPlaying();
                
                timeToCastle -= dt;
                walkTime -= dt;
                
                if (timeToCastle <= 0) {
                    // end game
                    EventSystem.notify(this.gameObject, new Event(EventType.MarioWin));
                    Window.changeScene(new LevelSceneInitializer(), Window.getScene().getLevelFilepath());
                }
            }
            return;
        }
        
        if (transformFramesLeft > 0) {
            transformAnimation(dt);
            return;
        } else if (invincBlinkFrames > 0) {
            blinkAnimation(dt);
            if (!isInvincible) {
                // is transforming to fire mario
                return;
            }
        }
        
        if (isHurtInvincible) {
            if (!transitionMachine.isPlaying()) {
                isHurtInvincible = false;
            }
        }
        
        if (KeyListener.isKeyPressed(Settings.MOVERIGHT) && !KeyListener.isKeyPressed(Settings.MOVELEFT)) {
            this.gameObject.transform.scale.x = playerWidth;
            
            this.acceleration.x = accel;
            if (KeyListener.isKeyPressed(Settings.SPRINT)) {
                this.terminalVelocity.x = sprintSpeed;
            
                if (this.velocity.x < 0) {
                    this.stateMachine.trigger("toSkid");
                    this.velocity.x += slowdownForce;
                } else {
                    this.stateMachine.trigger("toSprint");
                } 
            } else {
                this.terminalVelocity.x = walkSpeed;
            
                if (this.velocity.x < 0) {
                    this.velocity.x += slowdownForce;
                } else {
                    if (KeyListener.isKeyPressed(Settings.SPRINT)) {
                        this.stateMachine.trigger("toSprint");
                    } else {
                        this.stateMachine.trigger("toWalk");
                    }
                } 
            }
            
        } else if (KeyListener.isKeyPressed(Settings.MOVELEFT) && !KeyListener.isKeyPressed(Settings.MOVERIGHT)) {
            this.gameObject.transform.scale.x = -playerWidth;
            
            this.acceleration.x = -accel;
            if (KeyListener.isKeyPressed(Settings.SPRINT)) {
                this.terminalVelocity.x = sprintSpeed;
            
                if (this.velocity.x > 0) {
                    this.stateMachine.trigger("toSkid");
                    this.velocity.x -= slowdownForce;
                } else {
                    this.stateMachine.trigger("toSprint");
                } 
            } else {
                this.terminalVelocity.x = walkSpeed;
            
                if (this.velocity.x > 0) {
                    this.velocity.x -= slowdownForce;
                } else {
                    if (KeyListener.isKeyPressed(Settings.SPRINT)) {
                        this.stateMachine.trigger("toSprint");
                    } else {
                        this.stateMachine.trigger("toWalk");
                    }
                }
            }
            
            
        } else {
            this.acceleration.x = 0;
            if (this.velocity.x > 0) {
                this.velocity.x = Math.max(0, this.velocity.x - slowdownForce);
            } else if (this.velocity.x < 0) {
                this.velocity.x = Math.min(0, this.velocity.x + slowdownForce);
            }
            
            if (this.velocity.x == 0) {
                this.stateMachine.trigger("stopMoving");
            }
        }
        
        if (KeyListener.keyBeginPress(Settings.FIREBALL) && playerState == PlayerState.Fire && Fireball.canSpawn()) {
            Vector2f position = new Vector2f(gameObject.transform.position)
                    .add(gameObject.transform.scale.x > 0 ? new Vector2f(0.26f,0)
                            : new Vector2f(-0.26f,0));
            
            this.stateMachine.trigger("throwFireball");
            GameObject fireball = Prefab.generateFireball();
            fireball.transform.position.set(position);
            fireball.getComponent(Fireball.class).goingRight = 
                    gameObject.transform.scale.x > 0;
            fireball.getComponent(Rigidbody2D.class).setAngularVelocity(-30);
            
            Window.getScene().addGameObjectToScene(fireball);
            AssetPool.getSound("assets/sounds/fireball.ogg").play();
        }
        
        checkOnGround();
        if (KeyListener.isKeyPressed(Settings.JUMP) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
            if ((onGround || groundDebounce > 0) && jumpTime == 0) {
                if (playerState == PlayerState.Small) {
                    AssetPool.getSound("assets/sounds/jump-small.ogg").stop();
                    AssetPool.getSound("assets/sounds/jump-small.ogg").play();
                } else {
                    AssetPool.getSound("assets/sounds/jump-super.ogg").stop();
                    AssetPool.getSound("assets/sounds/jump-super.ogg").play();
                }
                
                jumpTime = 57;
                //this.velocity.y = jumpImpulse;
            } else if (jumpTime > 0) {
                jumpTime --;
                this.velocity.y = ((jumpTime / 6f) * jumpBoost);
            } else {
                this.velocity.y = 0;
            }
            groundDebounce = 0;
            
        } else if (enemyBounce > 0) {
            enemyBounce--;
            this.velocity.y = ((enemyBounce / 2) * jumpBoost);
            
        } else if (!onGround) {
            if (this.jumpTime > 0) {
                this.velocity.y *= 0.5f;
                this.jumpTime = 0;
            }
            
            groundDebounce -= dt;
            this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
        } else {
            this.jumpTime = 0;
            this.velocity.y = 0;
            this.acceleration.y = 0;
            this.groundDebounce = this.groundDebounceTime;
        }
        
        if (!this.disableForces) {
            applyForces(dt);
        } else {
            this.stopAllForces();
        }
                
        if (!onGround) {
            stateMachine.trigger("jump");
        } else {
            stateMachine.trigger("touchFloorIdle");
        }
    }
    
    public void setPosition(Vector2f newPos) {
        this.gameObject.transform.position.set(newPos);
        this.rb.setPosition(newPos);
    }

    public PlayerState getPlayerState() {
        return playerState;
    }
    
    public void enemyBounce() {
        this.enemyBounce = 16;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isSmall() {
        return this.playerState == PlayerState.Small;
    }
    
    public boolean isHurtInvincible() {
        return isHurtInvincible;
    }
    
    public boolean isInvincible() {
        return isInvincible || this.isHurtInvincible() || playWinAnimation;
    }
    
    public boolean isStarInvincible() {
        return isInvincible;
    }
    
    public void powerUp() {
        switch(playerState) {
            case Small:
                // big mario
                playerState = PlayerState.Big;
                AssetPool.getSound("assets/sounds/powerup.ogg").play();
                gameObject.transform.scale.y = 0.42f;
                PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
                if (pb != null) {
                    jumpBoost *= bigJumpBoostFactor;
                    walkSpeed *= bigJumpBoostFactor;
                    pb.setHeight(0.63f);
                }
                setCastVal(-0.24f);
                
                stateMachine.changeSpriteSheetSkin(AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioBig.png"),0);
                stateMachine.trigger("jump");
                stateMachine.trigger("startGrow");
                transformFramesLeft = transformFrames;
                
                break;
            case Big:
                // fire mario
                playerState = PlayerState.Fire;
                AssetPool.getSound("assets/sounds/powerup.ogg").play();
                invincBlinkFrames = transformFrames;
                
                break;
            case Fire:
                break;
        } 
    }
    
    public void invinciblePowerUp() {
        invincBlinkFrames = invicivilityFrames;
        isInvincible = true;
        AssetPool.getSound("assets/sounds/powerup.ogg").play();
    }
    
    @Override
    public void beginCollision(GameObject collidingObj, Contact contact, Vector2f contactN) {
        if (isDead || !contact.isEnabled()) return;
        
        if (collidingObj.getComponent(Ground.class) != null) {
            if (Math.abs(contactN.x) > 0.8f) {
                this.velocity.x = 0;
            } else if (contactN.y > 0.8f) {
                this.velocity.y = 0;
                this.acceleration.y = 0;
                this.jumpTime = 0;
            }
        }
    }
    
    @Override
    public void preSolve(GameObject collidingObj, Contact contact, Vector2f contactN) {
        if (disableForces) {
            this.stopAllForces();
            return;
        }
    }
    
    public void die() {
        this.stateMachine.trigger("die");
        this.velocity.set(0,0);
        this.acceleration.set(0.0);
        this.isDead = true;
        this.rb.setIsSensor(true);
        AssetPool.getSound("assets/sounds/mario_die.ogg").play();
    }
    
    public void hurt() {
        switch(this.playerState) {
            case Small:
                this.stateMachine.trigger("die");
                this.velocity.set(0,0);
                this.acceleration.set(0.0);
                this.rb.setVelocity(new Vector2f());
                this.isDead = true;
                this.rb.setIsSensor(true);
                AssetPool.getSound("assets/sounds/mario_die.ogg").play();
                jumpTime = 25;
                this.rb.setBodyType(BodyType.Kinematic);
                break;
            case Big:
                this.playerState = PlayerState.Small;
                setCastVal(-0.14f);
                transitionMachine.startTransition(4);
                isHurtInvincible = true;
                stateMachine.trigger("jump");
                stateMachine.trigger("startShrink");
                transformFramesLeft = transformFrames;
                
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                break;
            case Fire:
                this.playerState = PlayerState.Big;
                isHurtInvincible = true;
                transitionMachine.startTransition(4);
                invincBlinkFrames = transformFrames;
                
                AssetPool.getSound("assets/sounds/pipe.ogg").play();
                break;
        }
    }
    
    private transient int dieStopFrames = 65;
    private transient int framesBeforeRestart = 400;
    /**
     * Ejecuta la animacion de muerte de mario
     * @param dt delta time
     */
    public void dieAnimation(float dt) {

        if (dieStopFrames > 0) {
            // time before last jump
            dieStopFrames--;
        } else {
            if (jumpTime > 0) {
                jumpTime --;
                this.velocity.y = ((jumpTime / 6f) * jumpBoost);
            } else {
                this.acceleration.y = Window.getPhysics().getGravity().y * 0.7f;
            }
        }
        
        applyForces(dt);
        framesBeforeRestart--;
        if (framesBeforeRestart == 0) {
            // die for real
            EventSystem.notify(this.gameObject, new Event(EventType.MarioDie));
        }
    }
    
    /**
     * Ejecuta la animacion de transformacion de mario
     * @param dt delta time
     */
    public void transformAnimation(float dt) {
        transformFramesLeft--;
        stopAllForces();
                
        if (transformFramesLeft == 0) {
            stateMachine.trigger("stopGrow");
            stateMachine.trigger("stopShrink");
            if (this.playerState == PlayerState.Small) {
                stateMachine.changeSpriteSheetSkin(AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png"), 0);
                gameObject.transform.scale.y = 0.25f;
                PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
                if (pb != null) {
                    jumpBoost /= bigJumpBoostFactor;
                    walkSpeed /= bigJumpBoostFactor;
                    pb.setHeight(0.31f);
                }
            }
        }
    }
    
    public transient int blinkS = 0;
    /**
     * Ejecuta la animacion de parpadeo estrella de mario, mario es invencible durante esta animacion y mata enemigos al contacto
     * @param dt delta time
     */
    public void blinkAnimation(float dt) {
        invincBlinkFrames--;
        if (!isInvincible) {
            stopAllForces();
        }
        
        if (invincBlinkFrames % 16 == 0) {
            if (this.playerState == PlayerState.Small) {
                stateMachine.changeSpriteSheetSkin(AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmallStar.png"), blinkS * 15);
            } else {
                stateMachine.changeSpriteSheetSkin(AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioBigStar.png"), blinkS * 18);
            }
        }
        
        blinkS++;
        if (blinkS == 3) {
            blinkS = 0;
        }
        
        if (invincBlinkFrames == 0) {
            blinkS = 0;
            if (isInvincible) {
                this.isInvincible = false;
            }
            switch (this.playerState) {
                case Small:
                    stateMachine.changeSpriteSheetSkin(AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png"), 0);
                    break;
                case Big:
                    stateMachine.changeSpriteSheetSkin(AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioBig.png"),0);
                    break;
                case Fire:
                    stateMachine.changeSpriteSheetSkin(AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioBig.png"),36);
                    break;
            }
            
        }
    }
    
    private transient boolean playWinAnimation = false;
    private transient float timeToCastle = 4.5f;
    private transient float walkTime = 2.2f;
    /**
     * Ejecuta la animacion de victoria de mario
     * @param flagPole el gameobject de la bandera (para la posicion)
     */
    public void playWinAnimation(GameObject flagPole) {
        if (!playWinAnimation) {
            playWinAnimation = true;
            stopAllForces();
            rb.setIsSensor(true);
            rb.setBodyType(BodyType.Static);
            gameObject.transform.position.x = flagPole.transform.position.x + 0.1f;
            AssetPool.getSound("assets/sounds/flagpole.ogg").play();
        }
    }
    
    public void playPipeEnterAnimation(Direction d) {
        switch(d) {
            case Up:
                transitionMachine.startTransition(3);
                break;
            case Down:
                transitionMachine.startTransition(2);
                break;
            case Left:
                transitionMachine.startTransition(0);
                break;
            case Right:
                transitionMachine.startTransition(1);
                break;
        }
    }
    
    public void playPipeExitAnimation(Direction d) {
        // same as enter but with flipped animations
        switch(d) {
            case Up:
                transitionMachine.startTransition(2);
                break;
            case Down:
                transitionMachine.startTransition(3);
                break;
            case Left:
                transitionMachine.startTransition(1);
                break;
            case Right:
                transitionMachine.startTransition(0);
                break;
        }
    }

    public TransitionMachine getTransitionMachine() {
        return transitionMachine;
    }

    public void setDisableForces(boolean disableForces) {
        this.disableForces = disableForces;
    }
    
    
}
