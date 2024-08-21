/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers;

import gameEngine.GameObject;
import java.util.ArrayList;
import java.util.List;
import observers.events.Event;

/**
 * Clase que lleva el control de los eventos que ocurren en la aplicacion.
 * La clase funciona como singleton y guarda todos los objetos "observer" en la escena para poder propagar eventos cuando ocurran
 * @author txaber gardeazabal
 */
public class EventSystem {
    private static List<Observer> observers = new ArrayList<>();
    
    /**
     * Añade un observer nuevo a la lista de observers
     * @param observer un objeto observer
     */
    public static void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }
    
    /**
     * Elimina un observer de la lista de observers
     * @param observer el objeto observer a eliminar
     * @return true en el caso de haber eliminado el observer, false si el observer no estaba en la lista, o no se pudo eliminar por otra razon
     */
    public static boolean removeObserver(Observer observer) {
        return observers.remove(observer);
    }
    
    /**
     * Lanza un evento para todos los observer en la escena, la interpretacion del evento depende del observador
     * @param go gameobject que lanza el evento
     * @param event evento nuevo a lanzar
     */
    public static void notify(GameObject go, Event event){
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).onNotify(go, event);
        }
    }
}
