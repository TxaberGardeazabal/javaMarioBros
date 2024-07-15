/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scene;

import observers.Observer;

/**
 *
 * @author txaber gardeazabal
 */
public abstract class SceneInitializer implements Observer{
    public abstract void init(Scene scene);
    public abstract void loadResources(Scene scene);
    public abstract void imGui();
}
