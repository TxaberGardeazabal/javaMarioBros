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
import static observers.events.EventType.GameEngineStartPlay;
import static observers.events.EventType.GameEngineStopPlay;
import static observers.events.EventType.LoadLevel;
import static observers.events.EventType.SaveLevel;
import util.AssetPool;
import util.Settings;

/**
 * Escena de juego
 * @author txaber gardeazabal
 */
public class LevelSceneInitializer extends SceneInitializer {
    
    public LevelSceneInitializer() {
    }
    
    @Override
    public void init(Scene scene) { 
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
    }

    @Override
    public void loadResources(Scene scene) {
        Settings.loadResources();        
        refreshTextures(scene);
    }

    @Override
    public void imGui() {}

    @Override
    public void onNotify(GameObject go, Event event) {
        switch(event.type) {
            case GameEngineStartPlay:
                ConsoleWindow.addLog("starting level: "+Window.getScene().getLevelFilepath(), ConsoleWindow.LogCategory.info);
                Window.setRuntimePlaying(true);
                Window.getScene().save();
                Window.changeScene(new LevelSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case GameEngineStopPlay:
                ConsoleWindow.addLog("ending level: "+Window.getScene().getLevelFilepath(), ConsoleWindow.LogCategory.info);
                Window.setRuntimePlaying(false);
                Window.changeScene(new LevelEditorSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer(), Window.getScene().getLevelFilepath());
                break;
            case SaveLevel:
                Window.getScene().save();
                break;
        }
    }
    
}