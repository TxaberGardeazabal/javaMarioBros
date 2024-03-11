/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scene;

import components.EditorCamera;
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
import org.joml.Vector2f;
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

        // de donde saque la idea de iniciar el imGuiLayer desde aqui?
        ImGuiLayer imGuiLayer = Window.getImGuiLayer();
        imGuiLayer.setMenuBar(new MenuBar());
        imGuiLayer.setAssetWindow(new AssetWindow(mc));
        imGuiLayer.setPropertiesWindow(new PropertiesWindow(mc));
        imGuiLayer.setSceneHierarchy(new SceneHierarchyWindow(mc));
    }

    @Override
    public void loadResources(Scene scene) {
        // TODO: puede que mover esta linea
        // default texture
        //AssetPool.getTexture("assets/alfa_rojo.png");
        
        // shaders
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.getShader("assets/shaders/debugLine2D.glsl");
        
        AssetPool.addSpritesheet("assets/images/noSpriteSheet.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/noSpriteSheet.png"),
                                16, 16, 16,0));
        AssetPool.getTexture("assets/images/noTexture.png");
        AssetPool.addSpritesheet("assets/images/gizmos.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                        22, 48, 3, 2));
        
        // spritesheets
        
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
        //System.out.println("X: " + MouseListener.getScreenX());
        //System.out.println("Y: " + MouseListener.getScreenY());
        // remove this in builds
        /*ImGui.begin("(Debug) level editor stuff");
        editor.imGui();
        ImGui.end();*/
        
        
    }
}
