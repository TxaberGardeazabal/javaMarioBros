/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.SpriteRenderer;
import components.StateMachine;
import gameEngine.GameObject;
import gameEngine.Prefab;
import gameEngine.PrefabSave;
import gameEngine.Window;
import org.joml.Vector4f;
import util.AssetPool;

/**
 * Bloque con interrogacion
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
    
    @Override
    public void update(float dt) {
        super.update(dt);
        if (isInvisible) {
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            spr.setColor(new Vector4f(1,1,1,0));
        }
    }
    
    @Override
    protected void hit(boolean shouldOpen) {
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
        }
    }

    private void doPowerup(boolean isSmall) {
        if (isSmall) {
            spawnMushroom();
        } else {
            spawnFlower();
        }
        
    }
    
    private void spawnMushroom() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        GameObject mush = Prefab.generateMushroom();
        mush.transform.position.set(gameObject.transform.position);
        mush.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(mush);
    }
    
    private void spawnFlower() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        GameObject flower = Prefab.generateFlower();
        flower.transform.position.set(gameObject.transform.position);
        flower.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(flower);
    }
    
    private void spawnVine() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private void spawnStar() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        GameObject star = Prefab.generateStar();
        star.transform.position.set(gameObject.transform.position);
        star.transform.position.y += 0.25f;
        Window.getScene().addGameObjectToScene(star);
    }
    
    private void spawn1up() {
        AssetPool.getSound("assets/sounds/powerup_appears.ogg").play();
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    private void doCoinMultiple() {
        if (numberCoins > 0) {
            doCoin();
            numberCoins--;
        }
    }

    public void setIsInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }
    
    
}

