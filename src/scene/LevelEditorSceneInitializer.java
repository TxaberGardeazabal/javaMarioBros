/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scene;


import components.EditorCamera;
import components.GameCamera;
import components.GizmoSystem;
import components.GridLines;
import components.KeyControls;
import components.MouseControls;
import components.SpriteSheet;
import gameEngine.GameObject;
import components.gamecomponents.HoleLogic;
import editor.ConsoleWindow;
import gameEngine.ImGuiLayer;
import gameEngine.Window;
import observers.events.Event;
import static observers.events.EventType.LoadLevel;
import static observers.events.EventType.SaveLevel;
import util.AssetPool;
import util.Settings;


/**
 * Escena de edicion de niveles, aqui se pueden crear, editar, jugar y borrar nuevos niveles, aparte de tener todo el acceso a los assets del motor,
 * @author txaber
 */
public class LevelEditorSceneInitializer extends SceneInitializer {
    
    // gameobject to contain most basic functionalities of the level editor
    private GameObject editor; 
    
    public LevelEditorSceneInitializer() {
    }
    
    @Override
    public void init(Scene scene) {
        Window.get().changeTitle("Mario level editor: " + Window.getScene().getLevelFilepath());
        
        Window.getImGuiLayer().getGameViewWindow().setShowMenuBar(true);
        
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
        
        GameObject overworldholeDetection = scene.createGameObject("overworldGround");
        overworldholeDetection.transform.position.y += -0.5;
        overworldholeDetection.setNoSerialize();
        overworldholeDetection.addComponent(new HoleLogic());
        overworldholeDetection.start();
        scene.addGameObjectToScene(overworldholeDetection);
        
        GameObject undergroundholeDetection = scene.createGameObject("undergroundGround");
        undergroundholeDetection.transform.position.y += -5.5;
        undergroundholeDetection.setNoSerialize();
        undergroundholeDetection.addComponent(new HoleLogic());
        undergroundholeDetection.start();
        scene.addGameObjectToScene(undergroundholeDetection);
        
        // TODO: here should not be a killzone but a special check, this is a placeholder
        GameObject skyholeDetection = scene.createGameObject("skyGround");
        skyholeDetection.transform.position.y += 4.75;
        skyholeDetection.setNoSerialize();
        skyholeDetection.addComponent(new HoleLogic());
        skyholeDetection.start();
        scene.addGameObjectToScene(skyholeDetection);
        
    }

    @Override
    public void loadResources(Scene scene) {

        Settings.loadResources();        
        refreshTextures(scene);
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
                ConsoleWindow.addLog("begining play", ConsoleWindow.LogCategory.info);
                Window.getImGuiLayer().getPropertiesWindow().getMc().destroyHoldingObject();
                Window.getImGuiLayer().getPropertiesWindow().getMc().clearColor();
                Scene scene = Window.getScene();
                scene.save();
                scene.camera().resetZoom();
                
                // add game camera
                GameObject camera = scene.createGameObject("SceneCamera");
                camera.setNoSerialize();
                camera.addComponent(new GameCamera(scene.camera()));
                camera.start();
                scene.addGameObjectToScene(camera);
                
                
                // add hole killzones
                GameObject overworldholeDetection = scene.createGameObject("overworldGround");
                overworldholeDetection.transform.position.y += -0.5;
                overworldholeDetection.setNoSerialize();
                overworldholeDetection.addComponent(new HoleLogic());
                overworldholeDetection.start();
                scene.addGameObjectToScene(overworldholeDetection);

                GameObject undergroundholeDetection = scene.createGameObject("undergroundGround");
                undergroundholeDetection.transform.position.y += -5.5;
                undergroundholeDetection.setNoSerialize();
                undergroundholeDetection.addComponent(new HoleLogic());
                undergroundholeDetection.start();
                scene.addGameObjectToScene(undergroundholeDetection);
                
                // TODO: here should not be a killzone but a special check, this is a placeholder
                GameObject skyholeDetection = scene.createGameObject("skyGround");
                skyholeDetection.transform.position.y += 4.75;
                skyholeDetection.setNoSerialize();
                skyholeDetection.addComponent(new HoleLogic());
                skyholeDetection.start();
                scene.addGameObjectToScene(skyholeDetection);
                
                Window.setRuntimePlaying(true);
                break;
            case EditorStopPlay:
                ConsoleWindow.addLog("ending play", ConsoleWindow.LogCategory.info);
                Window.setRuntimePlaying(false);
                Window.getImGuiLayer().getGameViewWindow().setIsPlaying(false);
                
                AssetPool.stopAllSounds();
                
                Window.changeScene(new LevelEditorSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case SaveLevel:
                Window.getImGuiLayer().getPropertiesWindow().getMc().destroyHoldingObject();
                Window.getImGuiLayer().getPropertiesWindow().getMc().clearSelected();
                Window.getScene().save();
                break;
            case EndWindow:
                AssetPool.stopAllSounds();
                endImGui();
                break;
        }
    }
}
