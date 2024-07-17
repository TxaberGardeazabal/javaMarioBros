/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scene;

import UI.Digitalizer;
import components.EditorCamera;
import components.FontRenderer;
import components.GameCamera;
import components.GizmoSystem;
import components.GridLines;
import components.KeyControls;
import components.MouseControls;
import components.Sprite;
import components.SpriteRenderer;
import components.SpriteSheet;
import gameEngine.GameObject;
import gameEngine.Prefab;
import components.StateMachine;
import editor.AssetWindow;
import editor.MenuBar;
import editor.PropertiesWindow;
import editor.SceneHierarchyWindow;
import gameEngine.Direction;
import gameEngine.ImGuiLayer;
import gameEngine.Sound;
import gameEngine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import java.io.File;
import java.util.Collection;
import observers.Observer;
import observers.events.Event;
import static observers.events.EventType.GameEngineStartPlay;
import static observers.events.EventType.GameEngineStopPlay;
import static observers.events.EventType.LoadLevel;
import static observers.events.EventType.SaveLevel;
import org.joml.Vector2f;
import render.FontTest;
import util.AssetPool;
import util.Settings;


/**
 * Level editor scene for the editing of levels
 * @author txaber
 */
public class LevelEditorSceneInitializer extends SceneInitializer {
            
    //private GameObject obj1;
    //private SpriteSheet sprites;
    
    // gameobject to contain most basic functionalities of the level editor
    private GameObject editor; 
    
    public LevelEditorSceneInitializer() {
    }
    
    @Override
    public void init(Scene scene) {
        Window.get().changeTitle("Mario level editor: " + Window.getScene().getLevelFilepath());
        
        SpriteSheet gizmos = AssetPool.getSpritesheet("assets/images/gizmos.png");
        
        editor = scene.createGameObject("LevelEditorStuff");
        editor.setNoSerialize();
        MouseControls mc = new MouseControls(Window.getPickingTexture());
        editor.addComponent(mc);
        editor.addComponent(new KeyControls(mc));
        editor.addComponent(new GridLines());
        editor.addComponent(new EditorCamera(scene.camera()));
        editor.addComponent(new GizmoSystem(gizmos,mc));
        scene.addGameObjectToScene(editor);

        ImGuiLayer imGuiLayer = Window.getImGuiLayer();
        imGuiLayer.startMenuBar();
        imGuiLayer.startSceneHierarchyWindow(mc);
        imGuiLayer.startAssetWindow(mc);
        imGuiLayer.startPropertiesWindow(mc);
        imGuiLayer.startConsoleWindow();
        
        /*GameObject go = Window.getScene().createGameObject("Font_test");
        go.transform.position.x = 2.875f;
        go.transform.position.y = 1.625f;
        go.transform.scale.x = 0.25f;
        go.transform.scale.y = 0.25f;
        go.addComponent(new FontTest("assets/fonts/super-mario-bros-nes.ttf", 64));
        Window.getScene().addGameObjectToScene(go);
        */
        
        
    }

    @Override
    public void loadResources(Scene scene) {
        
        // shaders
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.getShader("assets/shaders/debugLine2D.glsl");
        
        // def
        AssetPool.addSpritesheet("assets/images/noSpriteSheet.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/noSpriteSheet.png"),
                16, 16, 16,0));
        AssetPool.getTexture("assets/images/noTexture.png");
        AssetPool.addSpritesheet("assets/images/gizmos.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                22, 48, 3, 2));
        
        // textures
        AssetPool.getTexture("assets/images/spriteSheets/particles/marioFireball.png");
        
        // text textures
        AssetPool.getTexture("assets/images/text/mario.png");
        AssetPool.getTexture("assets/images/text/mundo.png");
        AssetPool.getTexture("assets/images/text/tiempo.png");
        AssetPool.getTexture("assets/images/text/empezar.png");
        AssetPool.getTexture("assets/images/text/editor.png");
        AssetPool.getTexture("assets/images/text/salir.png");
        AssetPool.getTexture("assets/images/text/seleccionar_nivel.png");
        AssetPool.getTexture("assets/images/text/Super_Mario_Bros._Logo.png");
        AssetPool.getTexture("assets/images/text/0.png");
        AssetPool.getTexture("assets/images/text/1.png");
        AssetPool.getTexture("assets/images/text/2.png");
        AssetPool.getTexture("assets/images/text/3.png");
        AssetPool.getTexture("assets/images/text/4.png");
        AssetPool.getTexture("assets/images/text/5.png");
        AssetPool.getTexture("assets/images/text/6.png");
        AssetPool.getTexture("assets/images/text/7.png");
        AssetPool.getTexture("assets/images/text/8.png");
        AssetPool.getTexture("assets/images/text/9.png");
        AssetPool.getTexture("assets/images/text/-.png");
        AssetPool.getTexture("assets/images/text/x.png");
        
        
        // spritesheets
        AssetPool.addSpritesheet("assets/images/text/numeros.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/text/numeros.png"),
                56,56,10,8));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderground.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundUnderground.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderwater.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundUnderwater.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundCastle.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundCastle.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryUnderground.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryUnderground.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryCastle.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryCastle.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndScenerySnow.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndScenerySnow.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesFull.png"),
                32,32,4,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/skyOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/skyOverworld.png"),
                16,16,10,0));
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
                16,24,14,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png"),
                16,16,8,0));

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
        
        refreshTextures(scene);
    }
    
    private void refreshTextures(Scene scene) {
        // load into scene objects
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
    
    public void endImGui() {
        ImGuiLayer imGuiLayer = Window.getImGuiLayer();
        imGuiLayer.endMenuBar();
        imGuiLayer.endSceneHierarchyWindow();
        imGuiLayer.endAssetWindow();
        imGuiLayer.endPropertiesWindow();
        imGuiLayer.endConsoleWindow();
    }

    @Override
    public void onNotify(GameObject go, Event event) {
        switch(event.type) {
            case EditorStartPlay:
                System.out.println("starting play");
                Window.setRuntimePlaying(true);
                Window.getScene().save();
                
                // add game camera
                GameObject camera = Window.getScene().createGameObject("SceneCamera");
                camera.setNoSerialize();
                camera.addComponent(new GameCamera(Window.getScene().camera()));
                camera.start();
                Window.getScene().addGameObjectToScene(camera);
                
                Window.getImGuiLayer().getPropertiesWindow().getMc().destroyHoldingObject();
                
                //Window.changeScene(new LevelSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case EditorStopPlay:
                System.out.println("ending play");
                Window.setRuntimePlaying(false);
                
                Window.changeScene(new LevelEditorSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case SaveLevel:
                Window.getScene().save();
                break;
            case EndWindow:
                endImGui();
                break;
        }
    }
}
