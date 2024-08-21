/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers;

import gameEngine.GameObject;
import observers.events.Event;

/**
 * Funcionalidad para poder recibir llamadas de eventos y poder interpretarlos
 * @author txaber gardeazabal
 */
public interface Observer {
    /**
     * La funcion se llama cada vez que hay un evento
     * @param go gameobject que lanza el evento
     * @param event el evento lanzado
     */
    void onNotify(GameObject go, Event event);
}
