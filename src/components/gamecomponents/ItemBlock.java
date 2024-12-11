/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.SpriteRenderer;
import components.StateMachine;
import components.TransitionMachine;
import components.TranslateTransition;
import components.propertieComponents.Ground;
import editor.ConsoleWindow;
import gameEngine.GameObject;
import gameEngine.PrefabSave;
import gameEngine.Window;
import java.util.HashMap;
import java.util.Map;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import physics2D.RaycastInfo;
import org.jbox2d.dynamics.contacts.Contact;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import physics2D.components.Rigidbody2D;
import physics2D.enums.BodyType;
import render.DebugDraw;
import util.AssetPool;
import util.Settings;

/**
 * controlador del bloque con interrogacion
 * @author txaber gardeazabal
 */
public class ItemBlock extends Block{

    private enum ContentType {
        Coin,
        CoinMultiple,
        Powerup,
        Star,
        Vine,
        Flower,
        Mushroom,
        LiveMushroom
    }
    
    private ContentType contents = ContentType.Coin;
    private int numberCoins = 5;
    private transient StateMachine stateMachine;
    private boolean isInvisible = false;
    
    @Override
    public void start() {
        super.start();
        this.stateMachine = gameObject.getComponent(StateMachine.class);
        
    }
    
    private transient boolean firstFlag = true;
    @Override
    public void update(float dt) {
        super.update(dt);
        
        if (isInvisible && firstFlag) {
            this.gameObject.removeComponent(Ground.class);
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            spr.setColor(new Vector4f(1,1,1,0));
        }
        firstFlag = false;
    }
    
    @Override
    protected void hit(boolean shouldOpen) {
        // detect an enemy on top 
        Vector2f raycastBegin = new Vector2f(this.gameObject.transform.position);
        raycastBegin.sub(0.145f / 2.0f, 0f);
        Vector2f raycastEnd = new Vector2f(raycastBegin).add(0.0f, 0.2f);
        
        RaycastInfo info = Window.getPhysics().raycast(this.gameObject, raycastBegin, raycastEnd);
        
        Vector2f raycast2Begin = new Vector2f(raycastBegin).add(0.145f, 0.0f);
        Vector2f raycast2End = new Vector2f(raycastEnd).add(0.145f, 0.0f);
        
        RaycastInfo info2 = Window.getPhysics().raycast(this.gameObject, raycast2Begin, raycast2End);
        
        if (Settings.mOtherRaycast) {
            DebugDraw.addLine2D(raycastBegin, raycastEnd, new Vector3f(1,0,0));
            DebugDraw.addLine2D(raycast2Begin, raycast2End, new Vector3f(1,0,0));
        }

        if ((info.hit && info.hitObj != null && info.hitObj.getComponent(Enemy.class) != null)
                || (info2.hit && info2.hitObj != null && info2.hitObj.getComponent(Enemy.class) != null)) {
            if (info.hitObj != null) {
                info.hitObj.getComponent(Enemy.class).die();
            } else {
                info2.hitObj.getComponent(Enemy.class).die();
            }
            
        }
        
        switch(contents) {
            case Coin:
                doCoin();
                break;
            case Powerup:
                doPowerup(shouldOpen);
                break;
            case Vine:
                spawnVine();
                break;
            case Star:
                spawnStar();
                break;
            case Flower:
                spawnFlower();
                break;
            case Mushroom:
                spawnMushroom();
                break;
            case LiveMushroom:
                spawn1up();
                break;
            case CoinMultiple:
                doCoinMultiple();
                break;
        }
        
        if (!(contents == ContentType.CoinMultiple) || numberCoins == 0) {
            stateMachine.trigger("setInactive");
            this.setInactive();
            if (isInvisible) {
                isInvisible = false;
                SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
                spr.setColor(new Vector4f(1,1,1,1));
                this.gameObject.addComponent(new Ground());
            }
        }
    }
    
    private void doCoin() {
        AssetPool.getSound("assets/sounds/coin.ogg").play();
        PrefabSave coinPre = new PrefabSave("assets/prefabs/particles/blockCoin.prefab");
        GameObject coin = coinPre.load();
        if (coin != null) {
            coin.transform.position.set(gameObject.transform.position);
            coin.transform.position.y += 0.25f;
            Window.getScene().addGameObjectToScene(coin);
            
            Map payload = new HashMap<>();
            payload.put("points", "200");
            EventSystem.notify(this.gameObject, new Event(EventType.ScoreUpdate, payload));
        }
    }

    private void doPowerup(boolean isBig) {
        if (isBig) {
            spawnFlower();
        } else {
            spawnMushroom();
        }
    }
    
    private void spawnMushroom() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        PrefabSave mushPre = new PrefabSave("assets/prefabs/entities/mushroom.prefab");
        GameObject mush = mushPre.load();
        if (mush != null) {
            mush.getComponent(Rigidbody2D.class).setBodyType(BodyType.Static);
            TranslateTransition move = new TranslateTransition(new Vector2f(0,0.2f), 1);
            TransitionMachine mushtm = new TransitionMachine(true,false);
            mushtm.addTransition(move);
            mush.addComponent(mushtm);
            
            mush.transform.position.set(gameObject.transform.position);
            mush.transform.position.y += 0.05f;
            mush.getComponent(MushroomAI.class).setActive(false);
            
            mushtm.start();
            mushtm.begin();
            Window.getScene().addGameObjectToScene(mush);
        }
    }
    
    private void spawnFlower() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();        
        PrefabSave flowerPre = new PrefabSave("assets/prefabs/entities/fireFlower.prefab");
        GameObject flower = flowerPre.load();
        if (flower != null) {
            TranslateTransition move = new TranslateTransition(new Vector2f(0,0.2f), 1);
            TransitionMachine flowertm = new TransitionMachine(true,false);
            flowertm.addTransition(move);
            flower.addComponent(flowertm);
            
            flower.transform.position.set(gameObject.transform.position);
            flower.transform.position.y += 0.05f;
            
            flower.start();
            flowertm.begin();
            Window.getScene().addGameObjectToScene(flower);
        }
    }
    
    private void spawnVine() {
        ConsoleWindow.addLog("OOPS, not implemented yet", ConsoleWindow.LogCategory.info);
    }

    private void spawnStar() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        PrefabSave starPre = new PrefabSave("assets/prefabs/entities/star.prefab");
        GameObject star = starPre.load();
        if (star != null) {
            star.getComponent(Rigidbody2D.class).setBodyType(BodyType.Static);
            TranslateTransition move = new TranslateTransition(new Vector2f(0,0.2f), 1);
            TransitionMachine startm = new TransitionMachine(true,false);
            startm.addTransition(move);
            star.addComponent(startm);
            
            star.transform.position.set(gameObject.transform.position);
            star.transform.position.y += 0.05f;
            star.getComponent(StarAI.class).setActive(false);
            
            startm.start();
            startm.begin();
            Window.getScene().addGameObjectToScene(star);
        }
    }
    
    private void spawn1up() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        PrefabSave lifePre = new PrefabSave("assets/prefabs/entities/oneUpMushroom.prefab");
        GameObject life = lifePre.load();
        if (life != null) {
            life.getComponent(Rigidbody2D.class).setBodyType(BodyType.Static);
            TranslateTransition move = new TranslateTransition(new Vector2f(0,0.2f), 1);
            TransitionMachine mushtm = new TransitionMachine(true,false);
            mushtm.addTransition(move);
            life.addComponent(mushtm);
            
            life.transform.position.set(gameObject.transform.position);
            life.transform.position.y += 0.05f;
            life.getComponent(LiveMushroom.class).setActive(false);
            
            mushtm.start();
            mushtm.begin();
            Window.getScene().addGameObjectToScene(life);
        }
    }
    
    private void doCoinMultiple() {
        if (numberCoins > 0) {
            doCoin();
            numberCoins--;
        }
    }

    public void setIsInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
        if (isInvisible) {
            this.gameObject.removeComponent(Ground.class);
        } else {
            this.gameObject.addComponent(new Ground());
        }
    }
    
    @Override
    public void beginCollision(GameObject collidingObj, Contact contact, Vector2f contactN) {
        PlayerController playerController = collidingObj.getComponent(PlayerController.class);
        
        if (active && playerController != null && contactN.y < -0.8f) {
                if (tm != null) {
                    tm.begin();
                }
                AssetPool.getSound("assets/sounds/bump.ogg").play();
                hit(!playerController.isSmall());
                if (isInvisible) {
                    isInvisible = false;
                }
            }

            if (active && collidingObj.getComponent(KoopaAI.class) != null && !isInvisible) {

                KoopaAI koopa = collidingObj.getComponent(KoopaAI.class);
                if (koopa.isShelled && koopa.isShellMoving && contactN.y < 0.8f) { 
                    if (tm != null) {
                        tm.begin();
                    }
                    hit(true);
                }
            }
    }
    
    @Override
    public void preSolve(GameObject collidingObj, Contact contact, Vector2f contactN) {
        if (isInvisible) {
            contact.setEnabled(false);
        }
    }
}

