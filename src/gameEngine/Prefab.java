/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gameEngine;

import components.gamecomponents.GoombaAI;
import components.AnimationState;
import components.ComplexPrefabWrapper;
import components.FontRenderer;
import components.gamecomponents.BlockCoin;
import components.gamecomponents.Coin;
import components.gamecomponents.Flower;
import components.gamecomponents.ItemBlock;
import components.gamecomponents.KoopaAI;
import components.gamecomponents.MushroomAI;
import components.gamecomponents.Pipe;
import components.gamecomponents.PlayerController;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.StateMachine;
import components.UIButton;
import components.gamecomponents.Fireball;
import components.gamecomponents.Flag;
import components.gamecomponents.FlagPole;
import components.gamecomponents.BreakableBrick;
import components.propertieComponents.Ground;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector4f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.PillboxCollider;
import physics2D.components.Rigidbody2D;
import physics2D.enums.BodyType;
import util.AssetPool;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class Prefab {
    
    public static GameObject generateEmptyObject() {
        GameObject go = Window.getScene().createGameObject("empty_obj_gen");
        return go;
    }
    
    public static GameObject generateSpriteObject(Sprite sprite,float sizeX, float sizeY) {
        GameObject go = Window.getScene().createGameObject("sprite_obj_gen");
        go.transform.scale.x = sizeX;
        go.transform.scale.y = sizeY;
        SpriteRenderer spr = new SpriteRenderer();
        spr.setSprite(sprite);
        go.addComponent(spr);
        return go;
    }
    
    public static GameObject generateGroundObject(Sprite sprite,float sizeX, float sizeY) {
        GameObject go = generateSpriteObject(sprite, sizeX, sizeY);
        go.name = "ground object";
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        go.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        go.addComponent(b2d);
        go.addComponent(new Ground());
        
        return go;
    }
    
    public static GameObject generateBreakableObject(Sprite sprite,float sizeX, float sizeY) {
        GameObject go = generateSpriteObject(sprite,sizeX, sizeY);
        go.name = "breakable object";
        go.addComponent(new BreakableBrick());
        return go;
    }
    
    public static GameObject generateMario() {
        SpriteSheet playerSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png");
        GameObject mario = generateSpriteObject(playerSprites.getSprite(0), 0.25f, 0.25f);
        mario.name = "mario";
        mario.transform.zIndex = 1;
        float defaultFrameTime = 0.19f;
        
        // animation states
        
        AnimationState idle = new AnimationState();
        idle.title = "idle";
        idle.addFrame(playerSprites.getSprite(0), 0, 0);
        
        AnimationState walk = new AnimationState();
        walk.title = "walk";
        walk.addFrame(playerSprites.getSprite(1), defaultFrameTime, 1);
        walk.addFrame(playerSprites.getSprite(2), defaultFrameTime, 2);
        walk.addFrame(playerSprites.getSprite(3), defaultFrameTime, 3);
        walk.setLoop(true);
        
        AnimationState run = new AnimationState();
        run.title = "sprint";
        run.addFrame(playerSprites.getSprite(1), 0.09f,1);
        run.addFrame(playerSprites.getSprite(2), 0.09f,2);
        run.addFrame(playerSprites.getSprite(3), 0.09f,3);
        run.setLoop(true);
        
        AnimationState skid = new AnimationState();
        skid.title = "skid";
        skid.addFrame(playerSprites.getSprite(4), 0, 4);
        
        AnimationState jump = new AnimationState();
        jump.title = "jump";
        jump.addFrame(playerSprites.getSprite(5), 0, 5);
        
        AnimationState climb = new AnimationState();
        climb.title = "climb";
        climb.addFrame(playerSprites.getSprite(7), defaultFrameTime,7);
        climb.addFrame(playerSprites.getSprite(8), defaultFrameTime,8);
        climb.setLoop(true);
        
        AnimationState swim = new AnimationState();
        swim.title = "swim";
        swim.addFrame(playerSprites.getSprite(13), defaultFrameTime,13);
        swim.addFrame(playerSprites.getSprite(14), defaultFrameTime,14);
        swim.setLoop(true);
        
        AnimationState flap = new AnimationState();
        flap.title = "flap";
        flap.addFrame(playerSprites.getSprite(10), defaultFrameTime, 10);
        flap.addFrame(playerSprites.getSprite(12), defaultFrameTime, 12);
        flap.addFrame(playerSprites.getSprite(14), defaultFrameTime, 14);
        
        AnimationState crouch = new AnimationState();
        crouch.title = "crouch";
        crouch.addFrame(playerSprites.getSprite(6), 0, 6);
        
        AnimationState grow = new AnimationState();
        grow.title = "grow";
        grow.addFrame(playerSprites.getSprite(15), 0.1f, 15);
        grow.addFrame(playerSprites.getSprite(0), 0.1f, 0);
        grow.setLoop(true);
        
        AnimationState shrink = new AnimationState();
        shrink.title = "shrink";
        shrink.addFrame(playerSprites.getSprite(0), 0.1f, 0);
        shrink.addFrame(playerSprites.getSprite(15), 0.1f, 15);
        shrink.setLoop(true);
        
        AnimationState fireball = new AnimationState();
        fireball.title = "fireball";
        fireball.addFrame(playerSprites.getSprite(16), 0, 16);
        
        AnimationState die = new AnimationState();
        die.title = "die";
        die.addFrame(playerSprites.getSprite(6), 0, 6);
        
        // add states
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.addState(walk);
        stateMachine.addState(run);
        stateMachine.addState(skid);
        stateMachine.addState(jump);
        stateMachine.addState(climb);
        stateMachine.addState(swim);
        stateMachine.addState(flap);
        stateMachine.addState(crouch);
        stateMachine.addState(grow);
        stateMachine.addState(shrink);
        stateMachine.addState(fireball);
        stateMachine.addState(die);
        stateMachine.setDefaultState(idle.title);
        
        // state transfers
        
        stateMachine.addStateTrigger(walk.title, idle.title, "stopMoving");
        stateMachine.addStateTrigger(run.title, idle.title, "stopMoving");
        stateMachine.addStateTrigger(skid.title, idle.title, "stopMoving");
        stateMachine.addStateTrigger(crouch.title, idle.title, "stopMoving");
        stateMachine.addStateTrigger(idle.title, walk.title, "toWalk");
        stateMachine.addStateTrigger(skid.title, walk.title, "toWalk");
        stateMachine.addStateTrigger(run.title, walk.title, "toWalk");
        stateMachine.addStateTrigger(idle.title, run.title, "toSprint");
        stateMachine.addStateTrigger(walk.title, run.title, "toSprint");
        stateMachine.addStateTrigger(skid.title, run.title, "toSprint");
        stateMachine.addStateTrigger(run.title, skid.title, "toSkid");
        stateMachine.addStateTrigger(idle.title, crouch.title, "toCrouch");
        stateMachine.addStateTrigger(walk.title, crouch.title, "toCrouch");
        stateMachine.addStateTrigger(run.title, crouch.title, "toCrouch");
        
        stateMachine.addStateTrigger(jump.title, grow.title, "startGrow");
        stateMachine.addStateTrigger(grow.title, jump.title, "stopGrow");
        stateMachine.addStateTrigger(jump.title, shrink.title, "startShrink");
        stateMachine.addStateTrigger(shrink.title, jump.title, "stopShrink");
        stateMachine.addStateTrigger(idle.title, fireball.title, "throwFireball");
        stateMachine.addStateTrigger(jump.title, fireball.title, "throwFireball");
        stateMachine.addStateTrigger(fireball.title, idle.title, "stopThrow");
        stateMachine.addStateTrigger(idle.title, jump.title, "jump");
        stateMachine.addStateTrigger(walk.title, jump.title, "jump");
        stateMachine.addStateTrigger(run.title, jump.title, "jump");
        stateMachine.addStateTrigger(fireball.title, jump.title, "jump");
        stateMachine.addStateTrigger(jump.title, idle.title, "touchFloorIdle");
        stateMachine.addStateTrigger(jump.title, climb.title, "startCimbing");
        stateMachine.addStateTrigger(climb.title, jump.title, "stopClimbing");
        stateMachine.addStateTrigger(climb.title, walk.title, "toWalk");
        stateMachine.addStateTrigger(jump.title, swim.title, "touchWater");
        stateMachine.addStateTrigger(swim.title, jump.title, "exitWater");
        stateMachine.addStateTrigger(swim.title, flap.title, "flap");
        stateMachine.addStateTrigger(flap.title, swim.title, "resumeSwiming");
        
        stateMachine.addStateTrigger(idle.title, die.title, "die");
        stateMachine.addStateTrigger(walk.title, die.title, "die");
        stateMachine.addStateTrigger(run.title, die.title, "die");
        stateMachine.addStateTrigger(skid.title, die.title, "die");
        stateMachine.addStateTrigger(jump.title, die.title, "die");
        stateMachine.addStateTrigger(climb.title, die.title, "die");
        stateMachine.addStateTrigger(swim.title, die.title, "die");
        stateMachine.addStateTrigger(flap.title, die.title, "die");
        stateMachine.addStateTrigger(crouch.title, die.title, "die");
        stateMachine.addStateTrigger(fireball.title, die.title, "die");
        
        mario.addComponent(stateMachine);
        
        // pillbox collider
        PillboxCollider pb = new PillboxCollider();
        pb.width = 0.39f;
        pb.height = 0.31f;
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setContinuousCollision(false);
        rb.setFixedRotation(true);
        rb.setMass(25.0f);
        mario.addComponent(rb);
        mario.addComponent(pb);
        
        // player 
        PlayerController pc = new PlayerController();
        pc.setTerminalVelocity(new Vector2f(3.0f,3.1f));
        pc.setInnerWidth(0.25f * 0.6f);
        pc.setCastVal(-0.14f);
        mario.addComponent(pc);
        
        return mario;
    }
    
    public static GameObject generateItemBlock(SpriteSheet sprites) {
        GameObject itemBlock = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.25f);
        itemBlock.name = "item block";
        
        AnimationState idle = new AnimationState();
        idle.title = "flicker";
        float defaultFrameTime = 0.23f;
        idle.addFrame(sprites.getSprite(0), defaultFrameTime);
        idle.addFrame(sprites.getSprite(1), defaultFrameTime);
        idle.addFrame(sprites.getSprite(2), defaultFrameTime);
        idle.setLoop(true);
        
        AnimationState inactive = new AnimationState();
        inactive.title = "inactive";
        inactive.addFrame(sprites.getSprite(3), 0.1f);
        
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(idle.title);
        stateMachine.addStateTrigger(idle.title, inactive.title, "setInactive");
        itemBlock.addComponent(stateMachine);
        itemBlock.addComponent(new ItemBlock());
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        itemBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        itemBlock.addComponent(b2d);
        itemBlock.addComponent(new Ground());
        
        return itemBlock;
    }
    
    public static GameObject generateItemBrickBlock(Sprite sprite1, SpriteSheet sprites2) {
        GameObject itemBlock = generateSpriteObject(sprite1, 0.25f, 0.25f);
        itemBlock.name = "brick item block";
        
        AnimationState idle = new AnimationState();
        idle.title = "active";
        idle.addFrame(sprite1, 0.1f);
        
        AnimationState inactive = new AnimationState();
        inactive.title = "inactive";
        inactive.addFrame(sprites2.getSprite(3), 0.1f);
        
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(idle.title);
        stateMachine.addStateTrigger(idle.title, inactive.title, "setInactive");
        itemBlock.addComponent(stateMachine);
        itemBlock.addComponent(new ItemBlock());
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        itemBlock.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        itemBlock.addComponent(b2d);
        itemBlock.addComponent(new Ground());
        
        return itemBlock;
    }
    
    public static GameObject generateInvItemBlock(SpriteSheet sprites) {
        GameObject itemBlock = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.25f);
        itemBlock.name = "invisible block";
        
        AnimationState idle = new AnimationState();
        idle.title = "active";
        idle.addFrame(sprites.getSprite(0), 0.1f);
        
        AnimationState inactive = new AnimationState();
        inactive.title = "inactive";
        inactive.addFrame(sprites.getSprite(3), 0.1f);
        
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.addState(inactive);
        stateMachine.setDefaultState(idle.title);
        stateMachine.addStateTrigger(idle.title, inactive.title, "setInactive");
        itemBlock.addComponent(stateMachine);
        
        ItemBlock it = new ItemBlock();
        it.setIsInvisible(true);
        itemBlock.addComponent(it);
        
        itemBlock.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,0.5f));
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        itemBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        itemBlock.addComponent(b2d);
        itemBlock.addComponent(new Ground());
        
        return itemBlock;
    }

    public static GameObject generateBlockCoin() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
        GameObject coin = generateSpriteObject(Sprites.getSprite(4), 0.25f, 0.25f);
        coin.name = "coin particle";
        
        AnimationState idle = new AnimationState();
        idle.title = "flicker";
        float defaultFrameTime = 0.23f;
        idle.addFrame(Sprites.getSprite(4), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(5), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(6), defaultFrameTime);
        idle.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.setDefaultState(idle.title);
        coin.addComponent(stateMachine);
        
        coin.addComponent(new BlockCoin());
        return coin;
    }
    
    public static GameObject generateMushroom() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
        GameObject mushroom = generateSpriteObject(Sprites.getSprite(0), 0.25f, 0.25f);
        mushroom.name = "mushroom";

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        mushroom.addComponent(rb);
        
        CircleCollider cc = new CircleCollider();
        cc.setRadius(0.14f);
        mushroom.addComponent(cc);
        mushroom.addComponent(new MushroomAI());
        
        return mushroom;
    }
    
    public static GameObject generateFlower() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
        GameObject flower = generateSpriteObject(Sprites.getSprite(1), 0.25f, 0.25f);
        flower.name = "flower";

        AnimationState idle = new AnimationState();
        idle.title = "flicker";
        float defaultFrameTime = 0.23f;
        idle.addFrame(Sprites.getSprite(1), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(2), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(3), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(4), defaultFrameTime);
        idle.setLoop(true);
        
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.setDefaultState(idle.title);
        flower.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        flower.addComponent(rb);
        
        CircleCollider cc = new CircleCollider();
        cc.setRadius(0.14f);
        flower.addComponent(cc);
        flower.addComponent(new Flower());
        
        return flower;
    }
    
    public static GameObject generateGoomba() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png");
        GameObject goomba = generateSpriteObject(Sprites.getSprite(6), 0.25f, 0.25f);
        goomba.name = "goomba";
        
        AnimationState walk = new AnimationState();
        walk.title = "walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(Sprites.getSprite(0), defaultFrameTime);
        walk.addFrame(Sprites.getSprite(1), defaultFrameTime);
        walk.setLoop(true);
        
        AnimationState xqzd = new AnimationState();
        xqzd.title = "squashed";
        xqzd.addFrame(Sprites.getSprite(2), defaultFrameTime);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(xqzd);
        stateMachine.setDefaultState(walk.title);
        stateMachine.addStateTrigger(walk.title, xqzd.title, "squash");
        goomba.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        goomba.addComponent(rb);
        
        CircleCollider cc = new CircleCollider();
        cc.setRadius(0.12f);
        goomba.addComponent(cc);
        
        GoombaAI gAi = new GoombaAI();
        gAi.setTerminalVelocity(new Vector2f(2.1f,3.1f));
        gAi.setInnerWidth(0.25f * 0.7f);
        gAi.setCastVal(-0.14f);
        goomba.addComponent(gAi);
        
        return goomba;
    }
    
    public static GameObject generateKoopa() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png");
        GameObject turtle = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.35f);
        turtle.name = "Koopa";
        
        AnimationState walk = new AnimationState();
        walk.title = "walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(sprites.getSprite(0), defaultFrameTime);
        walk.addFrame(sprites.getSprite(1), defaultFrameTime);
        walk.setLoop(true);
        
        AnimationState xqzd = new AnimationState();
        xqzd.title = "shell";
        xqzd.addFrame(sprites.getSprite(6), defaultFrameTime);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.addState(xqzd);
        stateMachine.setDefaultState(walk.title);
        stateMachine.addStateTrigger(walk.title, xqzd.title, "squash");
        turtle.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        turtle.addComponent(rb);
        
        CircleCollider cc = new CircleCollider();
        cc.setRadius(0.13f);
        cc.setOffset(new Vector2f(0, -0.05f));
        turtle.addComponent(cc);
        
        KoopaAI kAi = new KoopaAI();
        kAi.setTerminalVelocity(new Vector2f(2.1f,3.1f));
        kAi.setInnerWidth(0.25f * 0.7f);
        kAi.setCastVal(-0.2f);
        turtle.addComponent(kAi);
        
        return turtle;
    }
    
    public static GameObject generatePipeExp() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png");
        GameObject pipe = generateSpriteObject(Sprites.getSprite(6), 0.25f, 0.25f);
        pipe.name = "pipe";
        
        GameObject pipePart = generateSpriteObject(Sprites.getSprite(7), 0.25f, 0.25f);
        pipePart.name = "pipeQuarter1";
        pipePart.transform.translate(0.25f, 0);
        pipe.addChild(pipePart);
        pipePart = generateSpriteObject(Sprites.getSprite(14), 0.25f, 0.25f);
        pipePart.name = "pipeQuarter2";
        pipePart.transform.translate(0, -0.25f);
        pipe.addChild(pipePart);
        pipePart = generateSpriteObject(Sprites.getSprite(15), 0.25f, 0.25f);
        pipePart.name = "pipeQuarter3";
        pipePart.transform.translate(0.25f, -0.25f);
        pipe.addChild(pipePart);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        pipe.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        pipe.addComponent(b2d);
        pipe.addComponent(new Ground());
        
        return pipe;
    }
    
    public static GameObject generatePipe(Direction d) {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png");
        int spriteIndex = -1;
        switch(d) {
            case Up:
                spriteIndex = 0;
                break;
            case Left:
                spriteIndex = 1;
                break;
            case Down:
                spriteIndex = 2;
                break;
            case Right:
                spriteIndex = 3;
                break;
        }
        GameObject pipe = generateSpriteObject(Sprites.getSprite(spriteIndex), 0.5f, 0.5f);
        pipe.name = "pipe";
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        pipe.addComponent(rb);
        
        Box2DCollider box = new Box2DCollider();
        box.setHalfSize(new Vector2f(0.5f, 0.5f));
        pipe.addComponent(box);
        
        pipe.addComponent(new Pipe(d));
        pipe.addComponent(new Ground());
        
        return pipe;
    }
    
    public static GameObject generateCoin() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
        GameObject coin = generateSpriteObject(Sprites.getSprite(4), 0.25f, 0.25f);
        coin.name = "coin";
        
        AnimationState flicker = new AnimationState();
        flicker.title = "walk";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(Sprites.getSprite(4), defaultFrameTime);
        flicker.addFrame(Sprites.getSprite(5), defaultFrameTime);
        flicker.addFrame(Sprites.getSprite(6), defaultFrameTime);
        flicker.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.setDefaultState(flicker.title);
        coin.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        coin.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.1f,0.14f));
        b2d.setOffset(new Vector2f(0.0f,-0.03f));
        coin.addComponent(b2d);
        
        coin.addComponent(new Coin());
        return coin;
    }
    
    public static GameObject generateFireball(Vector2f position) {
        Sprite sprite = new Sprite(AssetPool.getTexture("assets/images/spriteSheets/particles/marioFireball.png"));
        GameObject fireball = generateSpriteObject(sprite, 0.12f, 0.12f);
        fireball.name = "fireball";
        fireball.transform.position.set(position);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        rb.setMass(0.1f);
        fireball.addComponent(rb);
        
        CircleCollider cc = new CircleCollider();
        cc.setRadius(0.08f);
        fireball.addComponent(cc);
        
        Fireball fb = new Fireball();
        fb.setTerminalVelocity(new Vector2f(2.1f,3.1f));
        fb.setInnerWidth( 0.25f * 0.7f);
        fb.setCastVal(-0.09f);
        fireball.addComponent(new Fireball());
        
        return fireball;
    }
    
    public static GameObject generateBush1() {
        SpriteSheet bushSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png");
        
        // create the parts
        GameObject go1 = generateSpriteObject(bushSprites.getSprite(8), Settings.GRID_WIDTH,  Settings.GRID_HEIGHT);
        GameObject go2 = generateSpriteObject(bushSprites.getSprite(9), Settings.GRID_WIDTH,  Settings.GRID_HEIGHT);
        GameObject go3 = generateSpriteObject(bushSprites.getSprite(10), Settings.GRID_WIDTH,  Settings.GRID_HEIGHT);
        go1.name = "bush";
        go2.name = "bush";
        go3.name = "bush";
        go1.transform.zIndex = -1;
        go2.transform.zIndex = -1;
        go3.transform.zIndex = -1;
        
        // add offsets
        go2.transform.position.add(new Vector2f(Settings.GRID_WIDTH * 1,0));
        go3.transform.position.add(new Vector2f(Settings.GRID_WIDTH * 2,0));
        
        GameObject ret = generateEmptyObject();
        ComplexPrefabWrapper pcw = new ComplexPrefabWrapper();
        pcw.addGameObject(go1);
        pcw.addGameObject(go2);
        pcw.addGameObject(go3);
        ret.addComponent(pcw);
        //System.out.println(pcw.getGameObjects().size());
        return ret;
    }
    
    public static GameObject generateFlag() {
        SpriteSheet FlagSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioParticles.png");
        SpriteSheet poleSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png");
        SpriteSheet baseSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png");
        int poleSize = 10;
        GameObject ret = generateEmptyObject();
        ComplexPrefabWrapper pcw = new ComplexPrefabWrapper();
        // flag object
        GameObject flag = generateSpriteObject(FlagSprites.getSprite(12), 0.25f, 0.25f);
        flag.name = "flag";
        flag.transform.position.set(-0.14f, Settings.GRID_HEIGHT * (poleSize - 1));
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        flag.addComponent(rb);
        flag.addComponent(new Flag());
        
        //b2d = new Box2DCollider();
        //b2d.setHalfSize(new Vector2f(0.02f,0.12f));
        //b2d.setOffset(new Vector2f(0f,-0.065f));
        //flag.addComponent(b2d);
        //flag.addComponent(new FlagPole(true));
        
        pcw.addGameObject(flag);
        
        // flag top
        GameObject flagPole = generateSpriteObject(poleSprites.getSprite(23), 0.25f, 0.25f);
        flagPole.name = "flagTop";
        
        rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        flagPole.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.02f,0.12f));
        b2d.setOffset(new Vector2f(0f,-0.065f));
        flagPole.addComponent(b2d);
        flagPole.addComponent(new FlagPole(true));
        
        flagPole.transform.position.add(new Vector2f(0,Settings.GRID_HEIGHT * poleSize));
        
        pcw.addGameObject(flagPole);
        // pole
        for (int i = 0; i < poleSize -1; i++) {
            flagPole = generateSpriteObject(poleSprites.getSprite(31), 0.25f, 0.25f);
            flagPole.name = "flagPole";

            rb = new Rigidbody2D();
            rb.setBodyType(BodyType.Static);
            rb.setFixedRotation(true);
            rb.setMass(0.1f);
            flagPole.addComponent(rb);

            b2d = new Box2DCollider();
            b2d.setHalfSize(new Vector2f(0.02f,0.25f));
            flagPole.addComponent(b2d);
            flagPole.addComponent(new FlagPole(false));
            
            flagPole.transform.position.add(new Vector2f(0,Settings.GRID_HEIGHT * (i + 1)));

            pcw.addGameObject(flagPole);
        }
        
        flagPole = generateSpriteObject(baseSprites.getSprite(7), 0.25f, 0.25f);
        flagPole.name = "flagBase";

        rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        flagPole.addComponent(rb);

        b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f,0.25f));
        flagPole.addComponent(b2d);
        flagPole.addComponent(new Ground());

        pcw.addGameObject(flagPole);
        ret.addComponent(pcw);
        return ret;
    }
    
    public static GameObject generateUIButton() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png");
        GameObject ret = generateSpriteObject(sprites.getSprite(1), 0.55f, 0.25f);
        ret.name = "font_render_text_object";
        
        ret.addComponent(new UIButton());
        ret.addComponent(new FontRenderer());
        return ret;
    }
}
