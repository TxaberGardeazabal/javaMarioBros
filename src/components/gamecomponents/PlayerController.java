/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.PhysicsController;
import components.StateMachine;
import components.TransitionMachine;
import components.propertieComponents.StageHazard;
import components.propertieComponents.Ground;
import gameEngine.Direction;
import gameEngine.GameObject;
import gameEngine.KeyListener;
import gameEngine.PrefabSave;
import gameEngine.Window;
import java.util.HashMap;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import physics2D.components.PillboxCollider;
import physics2D.components.Rigidbody2D;
import physics2D.enums.BodyType;
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
    public transient boolean isControlled = true;
    //public transient boolean isJumping = false;
    public transient boolean isCrouching = false;
    public transient boolean disableForces = false;
    private transient boolean hasEnded = false;
    private transient float groundDebounce = 0.0f;
    private transient float groundDebounceTime = 0.2f;
    private transient float airDebounce = 0.0f;
    private transient float airDebounceTime = 0.08f;
    private transient StateMachine stateMachine;
    private transient TransitionMachine transitionMachine;
    private transient float bigJumpBoostFactor = 1.05f;
    private transient float playerWidth = 0.25f;
    private transient int jumpTime = 0;
    private transient boolean isDead = false;
    private transient boolean isInvincible = false;
    private transient boolean isHurtInvincible = false;
    private transient int enemyBounce = 0;
    private transient final float heightMarioSmall = 0.27f;
    private transient final float heightMarioBig = 0.63f;
    private transient int stompCombo = 0;
    
    private transient int invicivilityFrames = 1700;
    private transient int invicivilityFramesLeft = 0;
    private transient float blinkTime = 0.0f;
    private transient int transformFrames = 100;
    private transient int transformFramesLeft = 0;
    private transient int invincBlinkFrames = 0;
    
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
            GameObject flag = Window.getScene().getGameObjectByName("flag");
            
            if (!transitionMachine.isPlaying() && hasEnded) {
                
                EventSystem.notify(this.gameObject, new Event(EventType.MarioWin));
                stateMachine.trigger("stopMoving");
                stopAllForces();
                playWinAnimation = false;
                
            } else if (flag != null) { 
                if (!flag.getComponent(TransitionMachine.class).isPlaying()) {
                    disableForces = false;
                    if (!transitionMachine.isPlaying()) {
                        transitionMachine.startTransition(6);
                        EventSystem.notify(this.gameObject, new Event(EventType.PlayEndTrack));
                    }
                    stateMachine.trigger("toWalk");
                    hasEnded = true;
                } else if (onGround) {
                    transitionMachine.stop();
                }
            } 
        }
        
        if (transformFramesLeft > 0) {
            transformAnimation(dt);
            return;
        } else if (invincBlinkFrames > 0) {
            invBlinkAnimation(dt);
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
        
        /*if (!transitionMachine.isPlaying()) {
            isControlled = true;
        }*/
        
        // controlls here
        if (isControlled) {
            if (KeyListener.isKeyPressed(Settings.MOVERIGHT) && !KeyListener.isKeyPressed(Settings.MOVELEFT) && !isCrouching) {
                if (KeyListener.isKeyPressed(Settings.SPRINT)) {
                    sprint(dt, true);
                } else {
                    move(dt, true);
                }

            } else if (KeyListener.isKeyPressed(Settings.MOVELEFT) && !KeyListener.isKeyPressed(Settings.MOVERIGHT) && !isCrouching) {
                if (KeyListener.isKeyPressed(Settings.SPRINT)) {
                    sprint(dt, false);
                } else {
                    move(dt, false);
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
            
            if (KeyListener.isKeyPressed(Settings.CROUCH)) {
                crouch(dt);
            } else if (isCrouching) {
                isCrouching = false;
                if (this.playerState != PlayerState.Small) {
                    PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
                    if (pb != null) {
                        pb.setOffset(new Vector2f(0,0));
                        pb.setHeight(heightMarioBig);
                    }
                }
            }

            if (KeyListener.keyBeginPress(Settings.FIREBALL) && playerState == PlayerState.Fire && Fireball.canSpawn()) {
                fireball();
            }
        }
        
        checkOnGround();
        if (isControlled) {
            // TODO: possibly refactor this into the jump function so it can be controlled outside of the player controlls
            if (KeyListener.isKeyPressed(Settings.JUMP) && (jumpTime > 0 || onGround || groundDebounce > 0)) {
                jump(dt);
                
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
        }
        
        
        if (!this.disableForces) {
            applyForces(dt);
        } else {
            this.stopAllForces();
        }
                
        if (!onGround) {
            // offsets the transition to jump by a bit so mario doesn't freak out when in constant state changes to jump
            // such as stepping on staircases or moving platforms
            airDebounce -= dt; 
            if (airDebounce < 0) {
                stateMachine.trigger("jump");
                airDebounce = airDebounceTime; 
            }
        } else {
            stateMachine.trigger("touchFloorIdle");
            airDebounce = airDebounceTime;
            
            // reset combo
            stompCombo = 0;
        }
    }
    
    public void move(float dt, boolean right) {
        if (right) {
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = accel;
            
            if (this.velocity.x < 0) {
                this.velocity.x += slowdownForce;
            } else {
                this.stateMachine.trigger("toWalk");
            } 
        } else {
            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -accel;
            
            
            if (this.velocity.x > 0) {
                this.velocity.x -= slowdownForce;
            } else {
                this.stateMachine.trigger("toWalk");
            } 
        }
        this.terminalVelocity.x = walkSpeed;
    }
    
    public void sprint(float dt, boolean right) {
        if (right) {
            this.gameObject.transform.scale.x = playerWidth;
            this.acceleration.x = accel;
            
            if (this.velocity.x < 0) {
                this.stateMachine.trigger("toSkid");
                this.velocity.x += slowdownForce;
            } else {
                this.stateMachine.trigger("toSprint");
            } 
        } else {
            this.gameObject.transform.scale.x = -playerWidth;
            this.acceleration.x = -accel;
            
            
            if (this.velocity.x > 0) {
                this.stateMachine.trigger("toSkid");
                this.velocity.x -= slowdownForce;
            } else {
                this.stateMachine.trigger("toSprint");
            } 
        }
        this.terminalVelocity.x = sprintSpeed;
    }
    
    public void jump(float dt) {
        if ((onGround || groundDebounce > 0) && jumpTime == 0) {
            if (playerState == PlayerState.Small) {
                AssetPool.getSound("assets/sounds/jump-small.ogg").stop();
                AssetPool.getSound("assets/sounds/jump-small.ogg").play();
            } else {
                AssetPool.getSound("assets/sounds/jump-super.ogg").stop();
                AssetPool.getSound("assets/sounds/jump-super.ogg").play();
            }
            
            if (isCrouching) {
                this.velocity.x += 0.2f;
                groundDebounce = groundDebounceTime;
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
        
    }
    
    public void crouch(float dt) {
        if (this.playerState != PlayerState.Small) {
            PillboxCollider pb = gameObject.getComponent(PillboxCollider.class);
            if (pb != null && isCrouching) {
                pb.setHeight(heightMarioSmall);
                pb.setOffset(new Vector2f(0,-0.09f));
            }
            isCrouching = true;
            stateMachine.trigger("toCrouch");
        }
    }
    
    public void fireball() {
        if (playerState == PlayerState.Fire && Fireball.canSpawn()) {
            Vector2f position = new Vector2f(gameObject.transform.position)
                    .add(gameObject.transform.scale.x > 0 ? new Vector2f(0.26f,0)
                    : new Vector2f(-0.26f,0));

            this.stateMachine.trigger("throwFireball");
            PrefabSave firePre = new PrefabSave("assets/prefabs/particles/marioFireball.prefab");
            GameObject fireball = firePre.load();
            if (fireball != null) {
                fireball.transform.position.set(position);
                fireball.getComponent(Fireball.class).goingRight = 
                        gameObject.transform.scale.x > 0;
                fireball.getComponent(Rigidbody2D.class).setAngularVelocity(-30);

                Window.getScene().addGameObjectToScene(fireball);
                AssetPool.getSound("assets/sounds/fireball.ogg").play();
            }
        }
    }
    
    /**
     * Esta mario en su estado de victoria
     * @return true si mario esta en su estado de victoria, false de lo contrario
     */
    public boolean hasWon() {
        // technically when mario wins inmediatly starts its winAnimation
        return playWinAnimation;
    }
    
    public void setPosition(Vector2f newPos) {
        this.gameObject.transform.position.set(newPos);
        this.rb.setPosition(newPos);
    }
    
    public void enemyBounce() {
        this.enemyBounce = 16;
    }

    public PlayerState getPlayerState() {
        return playerState;
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
        return isInvincible || isHurtInvincible || playWinAnimation;
    }
    
    public boolean isStarInvincible() {
        return isInvincible;
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
        if (collidingObj.getComponent(StageHazard.class) != null) {
            if (!isInvincible()) {
                this.hurt();
            }
            contact.setEnabled(false);
        }
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
                    pb.setHeight(heightMarioBig);
                }
                setCastVal(-0.22f);
                
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
                AssetPool.getSound("assets/sounds/powerup.ogg").play();
                break;
        } 
    }
    
    public void invinciblePowerUp() {
        invincBlinkFrames = invicivilityFrames;
        isInvincible = true;
        
        EventSystem.notify(this.gameObject, new Event(EventType.PlayInvincible));
    }
    
    public void die() {
        this.stateMachine.trigger("die");
        this.velocity.set(0,0);
        this.acceleration.set(0.0);
        this.isDead = true;
        this.rb.setIsSensor(true);
        AssetPool.getSound("assets/sounds/mario_die.ogg").play();
        EventSystem.notify(this.gameObject, new Event(EventType.StopAllTracks));
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
                EventSystem.notify(this.gameObject, new Event(EventType.StopAllTracks));
                
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
                    pb.setHeight(heightMarioSmall);
                }
            }
        }
    }
    
    public transient int blinkS = 0;
    /**
     * Ejecuta la animacion de parpadeo estrella de mario, mario es invencible durante esta animacion y mata enemigos al contacto
     * @param dt delta time
     */
    public void invBlinkAnimation(float dt) {
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
                EventSystem.notify(this.gameObject, new Event(EventType.StopAllTracks));
                EventSystem.notify(this.gameObject, new Event(EventType.ResumeMainTrack));
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
    /**
     * Ejecuta la animacion de victoria de mario
     * @param flagPole el gameobject de la bandera (para la posicion)
     */
    public void playWinAnimation(GameObject flagPole) {
        if (!playWinAnimation) {
            playWinAnimation = true;
            stopAllForces();
            isControlled = false;
            disableForces = true;
            gameObject.transform.position.x = flagPole.transform.position.x + 0.1f;
            AssetPool.getSound("assets/sounds/flagpole.ogg").play();
            stateMachine.trigger("startCimbing");
            gameObject.transform.scale.x = -0.25f;
            transitionMachine.startTransition(5);
            EventSystem.notify(this.gameObject, new Event(EventType.StopAllTracks));
        }
    }
    
    public void playWinAnimation2() {
        if (!playWinAnimation) {
            playWinAnimation = true;
            stopAllForces();
            isControlled = false;
            hasEnded = true;
            transitionMachine.startTransition(6);
            AssetPool.getSound("assets/sounds/bowserfalls.ogg").play();
            EventSystem.notify(this.gameObject, new Event(EventType.PlayEndTrack));
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
    
    public void stompIncrement() {
        Map payload = new HashMap<>();
        
        switch(stompCombo) {
            case 0:
                payload.put("points", "100");
                break;
            case 1:
                payload.put("points", "200");
                break;
            case 2:
                payload.put("points", "400");
                break;
            case 3:
                payload.put("points", "500");
                break;
            case 4:
                payload.put("points", "800");
                break;
            case 5:
                payload.put("points", "1000");
                break;
            case 6:
                payload.put("points", "2000");
                break;
            case 7:
                payload.put("points", "4000");
                break;
            case 8:
                payload.put("points", "5000");
                break;
            case 9:
                payload.put("points", "8000");
                break;
            case 10:
                payload.put("points", "0");
                EventSystem.notify(this.gameObject, new Event(EventType.OneUp));
                stompCombo--;
                break;
            default:
                payload.put("points", "0");
                break;
        }
        stompCombo++;
        EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
    }
}
