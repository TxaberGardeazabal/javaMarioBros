/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scene;

import observers.Observer;

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
    /**
     * Ejecuta codigo imgui para la escena
     */
    public abstract void imGui();
}
