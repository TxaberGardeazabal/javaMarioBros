/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scene;

import components.SpriteRenderer;
import components.StateMachine;
import gameEngine.GameObject;
import observers.Observer;
import util.AssetPool;

/**
 * Clase padre con la que se cargan los datos necesarios de cada escena.
 * @author txaber gardeazabal
 */
public abstract class SceneInitializer implements Observer{
    /**
     * Inicializa la escena, llamado antes del primer frame
     * @param scene referencia a la escena
     */
    public abstract void init(Scene scene);
    /**
     * Carga todos los recursos necesarios para la escena en memoria
     * @param scene referencia a la escena
     */
    public abstract void loadResources(Scene scene);
    
    /*
    * Recarga las texturas de la escena con las texturas de dentro del AssetPool
    */
    protected void refreshTextures(Scene scene) {
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
    /**
     * Ejecuta codigo imgui para la escena
     */
    public abstract void imGui();
}
