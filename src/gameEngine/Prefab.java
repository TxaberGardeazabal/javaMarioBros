/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gameEngine;

import components.gamecomponents.PiranhaPlantAI;
import UI.Digit;
import UI.Digitalizer;
import components.gamecomponents.GoombaAI;
import components.AnimationState;
import UI.FixedHUD;
import components.ComplexPrefabWrapper;
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
import UI.UIButton;
import UI.buttonBehaviors.EditorButtonBehavior;
import UI.buttonBehaviors.ExitButtonBehavior;
import UI.buttonBehaviors.LevelSelectButtonBehavior;
import UI.buttonBehaviors.StartButtonBehavior;
import UI.buttonBehaviors.TestBehavior;
import components.BlinkTransition;
import components.RotationTransition;
import components.TransitionMachine;
import components.TransitionState;
import components.TranslateTransition;
import components.gamecomponents.BowserAI;
import components.gamecomponents.Fireball;
import components.gamecomponents.Flag;
import components.gamecomponents.FlagPole;
import components.gamecomponents.BreakableBrick;
import components.gamecomponents.BridgeController;
import components.gamecomponents.FireRod;
import components.gamecomponents.LevelController;
import components.gamecomponents.LiveMushroom;
import components.gamecomponents.MovingPlatform;
import components.gamecomponents.MovingPlatformSource;
import components.gamecomponents.ParatroopaAI;
import components.gamecomponents.PlayerControlTransition;
import components.gamecomponents.PlayerControlTransition.PlayerControl;
import components.gamecomponents.StarAI;
import components.propertieComponents.Ground;
import components.propertieComponents.StageHazard;
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
 * Crea objetos del juego prefabricados y preprogramados con sus funcionalidades.
 * @author txaber gardeazabal
 */
public class Prefab {
    
    /*
        Funciones principales para el motor:
    */
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
    
    /*
        Funciones para crear gameobjects para el resto de la aplicacion, se mantienen por si los prefabs dejan de funcionar.
    */
    public static GameObject generateBreakableObject(Sprite sprite,float sizeX, float sizeY) {
        SpriteSheet particleSprite = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioParticles.png");
        GameObject go = generateGroundObject(sprite,sizeX, sizeY);
        go.name = "breakable brick block";
        
        AnimationState idle = new AnimationState();
        idle.title = "idle";
        idle.addFrame(sprite, 0);
        
        AnimationState broke = new AnimationState();
        broke.title = "broken";
        broke.addFrame(particleSprite.getSprite(0), 0);
        
        StateMachine sm = new StateMachine();
        sm.addState(idle);
        sm.addState(broke);
        sm.setDefaultState(idle.title);
        
        sm.addStateTrigger(idle.title, broke.title, "break");
        go.addComponent(sm);
        
        TransitionState move1 = new TranslateTransition(new Vector2f(0,0.05f),0.15f);
        TransitionState move2 = new TranslateTransition(new Vector2f(0,-0.05f),0.15f);
        
        TransitionMachine tm = new TransitionMachine(true,false);
        tm.addTransition(move1);
        tm.addTransition(move2);
        go.addComponent(tm);
        
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
        stateMachine.addStateTrigger(fireball.title, idle.title, "stopMoving");
        stateMachine.addStateTrigger(idle.title, walk.title, "toWalk");
        stateMachine.addStateTrigger(skid.title, walk.title, "toWalk");
        stateMachine.addStateTrigger(run.title, walk.title, "toWalk");
        //stateMachine.addStateTrigger(fireball.title, walk.title, "toWalk");
        stateMachine.addStateTrigger(idle.title, run.title, "toSprint");
        stateMachine.addStateTrigger(walk.title, run.title, "toSprint");
        stateMachine.addStateTrigger(skid.title, run.title, "toSprint");
        //stateMachine.addStateTrigger(fireball.title, run.title, "toSprint");
        stateMachine.addStateTrigger(run.title, skid.title, "toSkid");
        stateMachine.addStateTrigger(idle.title, crouch.title, "toCrouch");
        stateMachine.addStateTrigger(walk.title, crouch.title, "toCrouch");
        stateMachine.addStateTrigger(run.title, crouch.title, "toCrouch");
        stateMachine.addStateTrigger(fireball.title, crouch.title, "toCrouch");
        
        stateMachine.addStateTrigger(jump.title, grow.title, "startGrow");
        stateMachine.addStateTrigger(grow.title, jump.title, "stopGrow");
        stateMachine.addStateTrigger(jump.title, shrink.title, "startShrink");
        stateMachine.addStateTrigger(shrink.title, jump.title, "stopShrink");
        stateMachine.addStateTrigger(idle.title, fireball.title, "throwFireball");
        stateMachine.addStateTrigger(jump.title, fireball.title, "throwFireball");
        stateMachine.addStateTrigger(walk.title, fireball.title, "throwFireball");
        stateMachine.addStateTrigger(run.title, fireball.title, "throwFireball");
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
        
        // pipe and blink transitions
        TranslateTransition pipeRight = new TranslateTransition(new Vector2f(0.5f,0),1f);
        TranslateTransition pipeLeft = new TranslateTransition(new Vector2f(-0.5f,0),1f);
        TranslateTransition pipeTop = new TranslateTransition(new Vector2f(0,0.5f),1f);
        TranslateTransition pipeBottom = new TranslateTransition(new Vector2f(0,-0.5f),1f);
        
        BlinkTransition bt = new BlinkTransition(3);
        
        // cutscene transitions
        TranslateTransition flag1 = new TranslateTransition(new Vector2f(0,-2),2);
        PlayerControlTransition flag2 = new PlayerControlTransition();
        flag2.addSubStep(PlayerControl.MoveRight, 2);
        
        /*PlayerControlTransition test = new PlayerControlTransition();
        test.addSubStep(PlayerControl.MoveRight, 1);
        //test.addSubStep(PlayerControl.Jump, 1);
        test.addSubStep(PlayerControl.Wait, 0.5f);
        test.addSubStep(PlayerControl.MoveLeft, 1);
        */
        TransitionMachine tm = new TransitionMachine(false,false);
        tm.addTransition(pipeRight);
        tm.addTransition(pipeLeft);
        tm.addTransition(pipeTop);
        tm.addTransition(pipeBottom);
        tm.addTransition(bt);
        tm.addTransition(flag1);
        tm.addTransition(flag2);
        
        mario.addComponent(tm);
        
        // pillbox collider
        PillboxCollider pb = new PillboxCollider();
        pb.width = 0.39f;
        pb.height = 0.30f;
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
        itemBlock.transform.zIndex = 1;
        
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
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        itemBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        itemBlock.addComponent(b2d);
        itemBlock.addComponent(new Ground());
        
        TransitionState move1 = new TranslateTransition(new Vector2f(0,0.05f),0.15f);
        TransitionState move2 = new TranslateTransition(new Vector2f(0,-0.05f),0.15f);
        
        TransitionMachine tm = new TransitionMachine(true,false);
        tm.addTransition(move1);
        tm.addTransition(move2);
        itemBlock.addComponent(tm);
        itemBlock.addComponent(new ItemBlock());
        
        return itemBlock;
    }
    
    public static GameObject generateItemBrickBlock(Sprite sprite1, SpriteSheet sprites2) {
        GameObject itemBlock = generateSpriteObject(sprite1, 0.25f, 0.25f);
        itemBlock.name = "brick item block";
        itemBlock.transform.zIndex = 1;
        
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
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        itemBlock.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        itemBlock.addComponent(b2d);
        itemBlock.addComponent(new Ground());
        
        TransitionState move1 = new TranslateTransition(new Vector2f(0,0.05f),0.15f);
        TransitionState move2 = new TranslateTransition(new Vector2f(0,-0.05f),0.15f);
        
        TransitionMachine tm = new TransitionMachine(true,false);
        tm.addTransition(move1);
        tm.addTransition(move2);
        itemBlock.addComponent(tm);
        itemBlock.addComponent(new ItemBlock());
        
        return itemBlock;
    }
    
    public static GameObject generateInvItemBlock(SpriteSheet sprites) {
        GameObject itemBlock = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.25f);
        itemBlock.name = "invisible block";
        itemBlock.transform.zIndex = 1;
        
        itemBlock.getComponent(SpriteRenderer.class).setColor(new Vector4f(1,1,1,0.5f));
        
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
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        itemBlock.addComponent(rb);
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f, 0.25f));
        itemBlock.addComponent(b2d);
        itemBlock.addComponent(new Ground());
        
        TransitionState move1 = new TranslateTransition(new Vector2f(0,0.05f),0.15f);
        TransitionState move2 = new TranslateTransition(new Vector2f(0,-0.05f),0.15f);
        
        TransitionMachine tm = new TransitionMachine(true,false);
        tm.addTransition(move1);
        tm.addTransition(move2);
        itemBlock.addComponent(tm);
        ItemBlock it = new ItemBlock();
        itemBlock.addComponent(it);
        it.setIsInvisible(true);
        
        return itemBlock;
    }

    public static GameObject generateBlockCoin() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/blockCoin.png");
        GameObject coin = generateSpriteObject(Sprites.getSprite(0), 0.12f, 0.25f);
        coin.name = "coin particle";
        
        AnimationState idle = new AnimationState();
        idle.title = "flicker";
        float defaultFrameTime = 0.08f;
        idle.addFrame(Sprites.getSprite(0), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(1), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(2), defaultFrameTime);
        idle.addFrame(Sprites.getSprite(3), defaultFrameTime);
        idle.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.setDefaultState(idle.title);
        coin.addComponent(stateMachine);
        
        TranslateTransition move1 = new TranslateTransition(new Vector2f(0,0.75f),0.3f);
        TranslateTransition move2 = new TranslateTransition(new Vector2f(0,-0.5f),0.2f);
        
        TransitionMachine tm = new TransitionMachine(true,false);
        tm.addTransition(move1);
        tm.addTransition(move2);
        coin.addComponent(tm);
        
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
        rb.setGravityScale(0);
        mushroom.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.2f, 0.2f));
        b2d.setOffset(new Vector2f(0,0.02f));
        mushroom.addComponent(b2d);
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
        rb.setGravityScale(0);
        flower.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.2f, 0.2f));
        b2d.setOffset(new Vector2f(0,0.02f));
        flower.addComponent(b2d);
        
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
        rb.setGravityScale(0);
        goomba.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.17f, 0.1f));
        b2d.setOffset(new Vector2f(0, -0.01f));
        goomba.addComponent(b2d);
        
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
        rb.setGravityScale(0);
        turtle.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.19f, 0.19f));
        b2d.setOffset(new Vector2f(0, -0.05f));
        turtle.addComponent(b2d);
        
        KoopaAI kAi = new KoopaAI();
        kAi.setTerminalVelocity(new Vector2f(2.1f,3.1f));
        kAi.setInnerWidth(0.25f * 0.7f);
        kAi.setCastVal(-0.19f);
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
    
    public static GameObject generateFireball() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioParticles.png");
        Sprite sprite = new Sprite(AssetPool.getTexture("assets/images/spriteSheets/particles/marioFireball.png"));
        
        GameObject fireball = generateSpriteObject(sprite, 0.12f, 0.12f);
        fireball.name = "fireball";
        
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
        
        TransitionMachine tm = new TransitionMachine(true,false);
        tm.addTransition(new TranslateTransition(new Vector2f(0,-2f),2));
        flag.addComponent(tm);
        
        flag.addComponent(new Flag());
        pcw.addGameObject(flag);
        
        // flag top
        GameObject flagPole = generateSpriteObject(poleSprites.getSprite(23), 0.25f, 0.25f);
        flagPole.name = "flagTop";
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        flagPole.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.02f,0.12f));
        b2d.setOffset(new Vector2f(0f,-0.065f));
        flagPole.addComponent(b2d);
        flagPole.addComponent(new FlagPole(true, 10));
        
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
            flagPole.addComponent(new FlagPole(false ,i + 1));
            
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
    
    public static GameObject generateUIText(Sprite sprite) {
        GameObject ret = generateSpriteObject(sprite, sprite.getWidth()/338, sprite.getHeight()/338);
        ret.name = "text_object";
        ret.transform.zIndex = 50;
        
        return ret;
    }
    
    public static GameObject generateUIButton(Sprite sprite) {
        GameObject ret = generateSpriteObject(sprite, sprite.getWidth()/338, sprite.getHeight()/338);
        ret.name = "text_button_object";
        
        UIButton btn = new UIButton();
        btn.setButtonBehavior(new TestBehavior());
        ret.addComponent(btn);
        return ret;
    }
    
    public static GameObject generateUICoin() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/UIExtra.png");
        GameObject ret = generateSpriteObject(sprites.getSprite(1), sprites.getSprite(1).getWidth()/50, sprites.getSprite(1).getHeight()/50);
        ret.name = "animated_coin";
        
        AnimationState flicker = new AnimationState();
        flicker.title = "idle";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(sprites.getSprite(1), defaultFrameTime + 0.10f);
        flicker.addFrame(sprites.getSprite(2), defaultFrameTime);
        flicker.addFrame(sprites.getSprite(3), defaultFrameTime);
        flicker.addFrame(sprites.getSprite(2), defaultFrameTime);
        flicker.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.setDefaultState(flicker.title);
        ret.addComponent(stateMachine);
        
        return ret;
    }
    
    public static GameObject generateDigitalizer(int size) {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/text/fontFace.png");
        GameObject digitalizer = generateEmptyObject();
        digitalizer.name = "text_digitalizer";
        digitalizer.addComponent(new Digitalizer());
        
        float offset = 0.20f;
        float currentOffset = 0;
        for (int i = 0; i < size; i++) {
            GameObject go = generateSpriteObject(sprites.getSprite(0), sprites.getSprite(0).getWidth()/338, sprites.getSprite(0).getHeight()/338);
            
            go.transform.translate(currentOffset, 0);
            currentOffset += offset;
            
            AnimationState zero = new AnimationState();
            zero.title = "0";
            zero.addFrame(sprites.getSprite(0), 0);
            
            AnimationState one = new AnimationState();
            one.title = "1";
            one.addFrame(sprites.getSprite(1), 0);
            
            AnimationState two = new AnimationState();
            two.title = "2";
            two.addFrame(sprites.getSprite(2), 0);
            
            AnimationState three = new AnimationState();
            three.title = "3";
            three.addFrame(sprites.getSprite(3), 0);
            
            AnimationState four = new AnimationState();
            four.title = "4";
            four.addFrame(sprites.getSprite(4), 0);
            
            AnimationState five = new AnimationState();
            five.title = "5";
            five.addFrame(sprites.getSprite(5), 0);
            
            AnimationState six = new AnimationState();
            six.title = "6";
            six.addFrame(sprites.getSprite(6), 0);
            
            AnimationState seven = new AnimationState();
            seven.title = "7";
            seven.addFrame(sprites.getSprite(7), 0);
            
            AnimationState eigth = new AnimationState();
            eigth.title = "8";
            eigth.addFrame(sprites.getSprite(8), 0);
            
            AnimationState nine = new AnimationState();
            nine.title = "9";
            nine.addFrame(sprites.getSprite(9), 0);
            
            StateMachine sm = new StateMachine();
            sm.addState(zero);
            sm.addState(one);
            sm.addState(two);
            sm.addState(three);
            sm.addState(four);
            sm.addState(five);
            sm.addState(six);
            sm.addState(seven);
            sm.addState(eigth);
            sm.addState(nine);
            sm.setDefaultState(zero.title);
            
            sm.addStateTrigger(one.title, zero.title, "0");
            sm.addStateTrigger(two.title, zero.title, "0");
            sm.addStateTrigger(three.title, zero.title, "0");
            sm.addStateTrigger(four.title, zero.title, "0");
            sm.addStateTrigger(five.title, zero.title, "0");
            sm.addStateTrigger(six.title, zero.title, "0");
            sm.addStateTrigger(seven.title, zero.title, "0");
            sm.addStateTrigger(eigth.title, zero.title, "0");
            sm.addStateTrigger(nine.title, zero.title, "0");
            
            sm.addStateTrigger(zero.title, one.title, "1");
            sm.addStateTrigger(two.title, one.title, "1");
            sm.addStateTrigger(three.title, one.title, "1");
            sm.addStateTrigger(four.title, one.title, "1");
            sm.addStateTrigger(five.title, one.title, "1");
            sm.addStateTrigger(six.title, one.title, "1");
            sm.addStateTrigger(seven.title, one.title, "1");
            sm.addStateTrigger(eigth.title, one.title, "1");
            sm.addStateTrigger(nine.title, one.title, "1");
            
            sm.addStateTrigger(zero.title, two.title, "2");
            sm.addStateTrigger(one.title, two.title, "2");
            sm.addStateTrigger(three.title, two.title, "2");
            sm.addStateTrigger(four.title, two.title, "2");
            sm.addStateTrigger(five.title, two.title, "2");
            sm.addStateTrigger(six.title, two.title, "2");
            sm.addStateTrigger(seven.title, two.title, "2");
            sm.addStateTrigger(eigth.title, two.title, "2");
            sm.addStateTrigger(nine.title, two.title, "2");
            
            sm.addStateTrigger(zero.title, three.title, "3");
            sm.addStateTrigger(one.title, three.title, "3");
            sm.addStateTrigger(two.title, three.title, "3");
            sm.addStateTrigger(four.title, three.title, "3");
            sm.addStateTrigger(five.title, three.title, "3");
            sm.addStateTrigger(six.title, three.title, "3");
            sm.addStateTrigger(seven.title, three.title, "3");
            sm.addStateTrigger(eigth.title, three.title, "3");
            sm.addStateTrigger(nine.title, three.title, "3");
            
            sm.addStateTrigger(zero.title, four.title, "4");
            sm.addStateTrigger(one.title, four.title, "4");
            sm.addStateTrigger(two.title, four.title, "4");
            sm.addStateTrigger(three.title, four.title, "4");
            sm.addStateTrigger(five.title, four.title, "4");
            sm.addStateTrigger(six.title, four.title, "4");
            sm.addStateTrigger(seven.title, four.title, "4");
            sm.addStateTrigger(eigth.title, four.title, "4");
            sm.addStateTrigger(nine.title, four.title, "4");
            
            sm.addStateTrigger(zero.title, five.title, "5");
            sm.addStateTrigger(one.title, five.title, "5");
            sm.addStateTrigger(two.title, five.title, "5");
            sm.addStateTrigger(three.title, five.title, "5");
            sm.addStateTrigger(four.title, five.title, "5");
            sm.addStateTrigger(six.title, five.title, "5");
            sm.addStateTrigger(seven.title, five.title, "5");
            sm.addStateTrigger(eigth.title, five.title, "5");
            sm.addStateTrigger(nine.title, five.title, "5");
            
            sm.addStateTrigger(zero.title, six.title, "6");
            sm.addStateTrigger(one.title, six.title, "6");
            sm.addStateTrigger(two.title, six.title, "6");
            sm.addStateTrigger(three.title, six.title, "6");
            sm.addStateTrigger(four.title, six.title, "6");
            sm.addStateTrigger(five.title, six.title, "6");
            sm.addStateTrigger(seven.title, six.title, "6");
            sm.addStateTrigger(eigth.title, six.title, "6");
            sm.addStateTrigger(nine.title, six.title, "6");
            
            sm.addStateTrigger(zero.title, seven.title, "7");
            sm.addStateTrigger(one.title, seven.title, "7");
            sm.addStateTrigger(two.title, seven.title, "7");
            sm.addStateTrigger(three.title, seven.title, "7");
            sm.addStateTrigger(four.title, seven.title, "7");
            sm.addStateTrigger(five.title, seven.title, "7");
            sm.addStateTrigger(six.title, seven.title, "7");
            sm.addStateTrigger(eigth.title, seven.title, "7");
            sm.addStateTrigger(nine.title, seven.title, "7");
            
            sm.addStateTrigger(zero.title, eigth.title, "8");
            sm.addStateTrigger(one.title, eigth.title, "8");
            sm.addStateTrigger(two.title, eigth.title, "8");
            sm.addStateTrigger(three.title, eigth.title, "8");
            sm.addStateTrigger(four.title, eigth.title, "8");
            sm.addStateTrigger(five.title, eigth.title, "8");
            sm.addStateTrigger(six.title, eigth.title, "8");
            sm.addStateTrigger(seven.title, eigth.title, "8");
            sm.addStateTrigger(nine.title, eigth.title, "8");
            
            sm.addStateTrigger(zero.title, nine.title, "9");
            sm.addStateTrigger(one.title, nine.title, "9");
            sm.addStateTrigger(two.title, nine.title, "9");
            sm.addStateTrigger(three.title, nine.title, "9");
            sm.addStateTrigger(four.title, nine.title, "9");
            sm.addStateTrigger(five.title, nine.title, "9");
            sm.addStateTrigger(six.title, nine.title, "9");
            sm.addStateTrigger(seven.title, nine.title, "9");
            sm.addStateTrigger(eigth.title, nine.title, "9");
            
            go.addComponent(sm);
            go.addComponent(new Digit());
            digitalizer.addChild(go);
        }
        
        GameObject ret = generateEmptyObject();
        ret.addComponent(new FixedHUD());
        ComplexPrefabWrapper pcw = new ComplexPrefabWrapper();
        pcw.addGameObject(digitalizer);
        ret.addComponent(pcw);
        return ret;
    }
    
    public static GameObject generateStartButton(Sprite sprite) {
        GameObject ret = generateSpriteObject(sprite, sprite.getWidth()/338, sprite.getHeight()/338);
        ret.name = "start_button";
        
        UIButton btn = new UIButton();
        btn.setButtonBehavior(new StartButtonBehavior());
        ret.addComponent(btn);
        ret.addComponent(new FixedHUD());
        return ret;
    }
    
    public static GameObject generateExitButton(Sprite sprite) {
        GameObject ret = generateSpriteObject(sprite, sprite.getWidth()/338, sprite.getHeight()/338);
        ret.name = "exit_button";
        
        UIButton btn = new UIButton();
        btn.setButtonBehavior(new ExitButtonBehavior());
        ret.addComponent(btn);
        ret.addComponent(new FixedHUD());
        return ret;
    }
    
    public static GameObject generateEditorButton(Sprite sprite) {
        GameObject ret = generateSpriteObject(sprite, sprite.getWidth()/338, sprite.getHeight()/338);
        ret.name = "editor_button";
        
        UIButton btn = new UIButton();
        btn.setButtonBehavior(new EditorButtonBehavior());
        ret.addComponent(btn);
        ret.addComponent(new FixedHUD());
        return ret;
    }
    
    public static GameObject generateSelectButton(Sprite sprite) {
        GameObject ret = generateSpriteObject(sprite, sprite.getWidth()/338, sprite.getHeight()/338);
        ret.name = "select_button";
        
        UIButton btn = new UIButton();
        btn.setButtonBehavior(new LevelSelectButtonBehavior());
        ret.addComponent(btn);
        ret.addComponent(new FixedHUD());
        return ret;
    }
    
    public static GameObject generateStar() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
        GameObject star = generateSpriteObject(sprites.getSprite(5), 0.25f, 0.25f);
        star.name = "star powerup";
        
        AnimationState flicker = new AnimationState();
        flicker.title = "flicker";
        float defaultFrameTime = 0.23f;
        flicker.addFrame(sprites.getSprite(5), defaultFrameTime);
        flicker.addFrame(sprites.getSprite(6), defaultFrameTime);
        flicker.addFrame(sprites.getSprite(7), defaultFrameTime);
        flicker.addFrame(sprites.getSprite(8), defaultFrameTime);
        flicker.setLoop(true);

        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(flicker);
        stateMachine.setDefaultState(flicker.title);
        star.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        rb.setGravityScale(0);
        star.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.2f, 0.2f));
        b2d.setOffset(new Vector2f(0,0.02f));
        star.addComponent(b2d);
        star.addComponent(new StarAI());
        
        return star;
    }
    
    public static GameObject generateLiveMushroom() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
        GameObject mushroom = generateSpriteObject(Sprites.getSprite(9), 0.25f, 0.25f);
        mushroom.name = "1-up";

        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setContinuousCollision(false);
        rb.setGravityScale(0);
        mushroom.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.2f, 0.2f));
        b2d.setOffset(new Vector2f(0,0.02f));
        mushroom.addComponent(b2d);
        mushroom.addComponent(new LiveMushroom());
        
        return mushroom;
    }
    
    public static GameObject generateLevelController() {
        GameObject lc = generateEmptyObject();
        lc.name = "levelController";
        lc.addComponent(new LevelController());
        return lc;
    }
    
    public static GameObject generatePiranhaPlant() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png");
        GameObject plant = generateSpriteObject(sprites.getSprite(4), 0.25f, 0.35f);
        plant.name = "Plant";
        
        AnimationState idle = new AnimationState();
        idle.title = "idle";
        idle.setLoop(true);
        float defaultFrameTime = 0.23f;
        idle.addFrame(sprites.getSprite(4), defaultFrameTime);
        idle.addFrame(sprites.getSprite(5), defaultFrameTime);
        
        StateMachine sm = new StateMachine();
        sm.addState(idle);
        sm.setDefaultState(idle.title);
        plant.addComponent(sm);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setGravityScale(0);
        rb.setContinuousCollision(false);
        plant.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.12f, 0.18f));
        b2d.setOffset(new Vector2f(0, 0.01f));
        plant.addComponent(b2d);
        
        plant.addComponent(new PiranhaPlantAI());
        return plant;
    }
    
    public static GameObject generateMovingPlatform() {
        GameObject platform = generateEmptyObject();
        platform.name = "Moving platform";
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setGravityScale(0);
        rb.setContinuousCollision(false);
        platform.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.24f,0.12f));
        platform.addComponent(b2d);
        platform.addComponent(new Ground());
        
        platform.addComponent(new MovingPlatform());
        
        return platform;
    }
    
    public static GameObject generatePlatformFoundation() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/platformsTileable.png");
        GameObject platform = generateSpriteObject(sprites.getSprite(0), 0.125f, 0.125f);
        platform.name = "Platform";
        
        return platform;
    }
    
    public static GameObject generatePlatformFoundation2() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/platformsTileable2.png");
        GameObject platform = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.125f);
        platform.name = "Platform";
        
        return platform;
    }
    
    public static GameObject generatePlatformSpawner() {
        GameObject spawner = generateEmptyObject();
        spawner.name = "Platform generator";
        
        spawner.addComponent(new MovingPlatformSource("assets/prefabs/blocks/movingPlatformX4.prefab"));
        return spawner;
    }
    
    public static GameObject generateRedKoopa() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesRed.png");
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
        xqzd.addFrame(sprites.getSprite(4), defaultFrameTime);

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
        rb.setGravityScale(0);
        turtle.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.19f, 0.19f));
        b2d.setOffset(new Vector2f(0, -0.05f));
        turtle.addComponent(b2d);
        
        KoopaAI kAi = new KoopaAI();
        kAi.setTerminalVelocity(new Vector2f(2.1f,3.1f));
        kAi.setInnerWidth(0.25f * 0.7f);
        kAi.setCastVal(-0.19f);
        kAi.setFallsOnEdges(false);
        turtle.addComponent(kAi);
        
        return turtle;
    }
    
    public static GameObject generateRedParatroopa() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesRed.png");
        GameObject turtle = generateSpriteObject(sprites.getSprite(0), 0.25f, 0.35f);
        turtle.name = "Paratroopa";
        
        AnimationState fly = new AnimationState();
        fly.title = "fly";
        float defaultFrameTime = 0.23f;
        fly.addFrame(sprites.getSprite(2), defaultFrameTime);
        fly.addFrame(sprites.getSprite(3), defaultFrameTime);
        fly.setLoop(true);
        
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(fly);
        stateMachine.setDefaultState(fly.title);
        turtle.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        rb.setGravityScale(0);
        turtle.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.19f, 0.19f));
        b2d.setOffset(new Vector2f(0, -0.05f));
        turtle.addComponent(b2d);
        
        turtle.addComponent(new MovingPlatform());
        
        ParatroopaAI kAi = new ParatroopaAI();
        kAi.setTerminalVelocity(new Vector2f(2.1f,3.1f));
        kAi.setInnerWidth(0.25f * 0.7f);
        kAi.setCastVal(-0.19f);
        turtle.addComponent(kAi);
        
        return turtle;
    }
    
    public static GameObject generateFireRod() {
        SpriteSheet Sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
        GameObject fire = generateSpriteObject(Sprites.getSprite(3), 0.25f, 0.25f);
        fire.name = "Fire rod";
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(false);
        rb.setMass(0.1f);
        fire.addComponent(rb);

        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.25f,0.25f));
        fire.addComponent(b2d);
        
        fire.addComponent(new FireRod());
        return fire;
    }
    
    public static GameObject generateBowser() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/bowser.png");
        GameObject bowser = generateSpriteObject(sprites.getSprite(0), 0.5f, 0.5f);
        bowser.name = "bowser";
        
        AnimationState walk = new AnimationState();
        walk.title = "walk";
        float defaultFrameTime = 0.23f;
        walk.addFrame(sprites.getSprite(0), defaultFrameTime);
        walk.addFrame(sprites.getSprite(1), defaultFrameTime);
        walk.setLoop(true);
        
        AnimationState walk2 = new AnimationState();
        walk2.title = "alternateWalk";
        walk2.addFrame(sprites.getSprite(2), defaultFrameTime);
        walk2.addFrame(sprites.getSprite(3), defaultFrameTime);
        walk2.setLoop(true);
        
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(walk);
        stateMachine.setDefaultState(walk.title);
        stateMachine.addState(walk2);
        
        stateMachine.addStateTrigger(walk.title, walk2.title, "toBreathe");
        stateMachine.addStateTrigger(walk2.title, walk.title, "toWalk");
        bowser.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Dynamic);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        rb.setGravityScale(0);
        bowser.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.32f, 0.35f));
        b2d.setOffset(new Vector2f(0.04f, -0.03f));
        bowser.addComponent(b2d);
        bowser.addComponent(new BowserAI());
        
        return bowser;
    }
    
    public static GameObject generateFireJet() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/fireJet.png");
        GameObject fire = generateSpriteObject(sprites.getSprite(0), 0.375f, 0.125f);
        fire.name = "fire jet";
        
        AnimationState idle = new AnimationState();
        idle.title = "idle";
        float defaultFrameTime = 0.08f;
        idle.addFrame(sprites.getSprite(0), defaultFrameTime);
        idle.addFrame(sprites.getSprite(1), defaultFrameTime);
        idle.setLoop(true);
        
        StateMachine stateMachine = new StateMachine();
        stateMachine.addState(idle);
        stateMachine.setDefaultState(idle.title);
        fire.addComponent(stateMachine);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(true);
        rb.setMass(0.1f);
        rb.setGravityScale(0);
        fire.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.2f, 0.1f));
        b2d.setOffset(new Vector2f(0.07f, 0));
        fire.addComponent(b2d);
        
        TransitionMachine transitionMachine = new TransitionMachine(true, false);
        TransitionState move = new TranslateTransition(new Vector2f(-10,0),10f);
        transitionMachine.addTransition(move);
        fire.addComponent(transitionMachine);
        fire.addComponent(new StageHazard());
        
        return fire;
    }
    
    public static GameObject generateAxe() {
        SpriteSheet sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
        GameObject axe = generateSpriteObject(sprites.getSprite(12), 0.25f, 0.25f);
        axe.name = "axe";
        
        AnimationState idle = new AnimationState();
        idle.title = "idle";
        float defaultFrameTime = 0.23f;
        idle.addFrame(sprites.getSprite(12), defaultFrameTime);
        idle.addFrame(sprites.getSprite(13), defaultFrameTime);
        idle.addFrame(sprites.getSprite(14), defaultFrameTime);
        idle.setLoop(true);
        
        StateMachine sm = new StateMachine();
        sm.addState(idle);
        sm.setDefaultState(idle.title);
        axe.addComponent(sm);
        
        Rigidbody2D rb = new Rigidbody2D();
        rb.setBodyType(BodyType.Static);
        rb.setFixedRotation(false);
        rb.setMass(0.1f);
        rb.setGravityScale(0);
        axe.addComponent(rb);
        
        Box2DCollider b2d = new Box2DCollider();
        b2d.setHalfSize(new Vector2f(0.1f, 0.2f));
        b2d.setOffset(new Vector2f(0, 0));
        axe.addComponent(b2d);
        axe.addComponent(new BridgeController());
        
        return axe;
    }
}
