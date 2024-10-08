/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scene;

import components.GameCamera;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.StateMachine;
import gameEngine.GameObject;
import gameEngine.Window;
import observers.events.Event;
import util.AssetPool;

/**
 * Escena de menu principal
 * @author txaber gardeazabal
 */
public class MainMenuSceneInitializer extends SceneInitializer{

    public MainMenuSceneInitializer() {
    }

    @Override
    public void init(Scene scene) {
        Window.get().changeTitle("Super mario bros");   
        
        Window.getImGuiLayer().getGameViewWindow().setShowMenuBar(false);
        
        GameObject camera = scene.createGameObject("SceneCamera");
        camera.setNoSerialize();
        camera.addComponent(new GameCamera(scene.camera()));
        camera.start();
        scene.addGameObjectToScene(camera);
        
        System.out.println("starting play");
        Window.setRuntimePlaying(true);
    }

    @Override
    public void loadResources(Scene scene) {
        
        // shaders
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.getShader("assets/shaders/debugLine2D.glsl");
        
        AssetPool.getTexture("assets/images/text/mario.png");
        
        // spritesheets
        AssetPool.addSpritesheet("assets/images/text/numeros.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/text/numeros.png"),
                56,56,10,8));
        AssetPool.addSpritesheet("assets/images/gizmos.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        22, 48, 3, 2));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png"),
                16,16,25,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/coinBlocks.png"),
                16,16,15,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioSmall.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioSmall.png"),
                16,16,30,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioBig.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioBig.png"),
                16,32,54,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioSmallStar.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioSmallStar.png"),
                16,16,45,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioBigStar.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioBigStar.png"),
                16,32,54,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/marioPowerups.png"),
                16,16,36,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/blockCoin.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/blockCoin.png"),
                8,16,4,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/marioParticles.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/marioParticles.png"),
                16,16,20,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png"),
                16,32,14,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png"),
                16,16,8,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesFull.png"),
                32,32,4,0));
        AssetPool.getTexture("assets/images/spriteSheets/particles/marioFireball.png");
        
        // sounds
        AssetPool.addSound("assets/sounds/1-up.ogg", false);
        AssetPool.addSound("assets/sounds/bowserfalls.ogg", false);
        AssetPool.addSound("assets/sounds/bowserfire.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/fireball.ogg", false);
        AssetPool.addSound("assets/sounds/fireworks.ogg", false);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/jump-super.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/vine.ogg", false);
        AssetPool.addSound("assets/sounds/warning.ogg", false);
        AssetPool.addSound("assets/sounds/world_clear.ogg", false);
        
        
        for (GameObject g : scene.getGameObjects()) {
            if (g.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer spr = g.getComponent(SpriteRenderer.class);
                if (spr.getTexture() != null) {
                    spr.setTexture(AssetPool.getTexture(spr.getTexture().getFilepath()));
                }
            }
            
            if (g.getComponent(StateMachine.class) != null) {
                StateMachine stateMachine = g.getComponent(StateMachine.class);
                stateMachine.refreshTextures();
            }
        }
    }

    @Override
    public void imGui() {
    }

    @Override
    public void onNotify(GameObject go, Event event) {
        switch(event.type) {
            case PlayLevel:
                Window.changeScene(new LevelSceneInitializer(), event.getPayload("filepath"));
                break;
            case OpenInEditor:
                Window.setRuntimePlaying(false);
                Window.changeScene(new LevelEditorSceneInitializer(), event.getPayload("filepath"));
                break;
            case Exit:
                Window.get().destroy();
                break;
            
        }
    }
    
}
