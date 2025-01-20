/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scene;

import components.GameCamera;
import components.SpriteRenderer;
import components.SpriteSheet;
import components.StateMachine;
import components.gamecomponents.HoleLogic;
import editor.ConsoleWindow;
import gameEngine.GameObject;
import gameEngine.Window;
import observers.events.Event;
import util.AssetPool;
import util.Settings;

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
        
        ConsoleWindow.addLog("starting main menu level: "+Window.getScene().getLevelFilepath(), ConsoleWindow.LogCategory.info);
        Window.setRuntimePlaying(true);
    }

    @Override
    public void loadResources(Scene scene) {
        
        Settings.loadResources();        
        refreshTextures(scene);
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
                Window.get().close();
                break;
            
        }
    }
    
}
