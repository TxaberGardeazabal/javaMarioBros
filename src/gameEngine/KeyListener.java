/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import java.util.Arrays;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * Escucha los eventos de entrada del teclado con GLFW.
 * Esta clase es singleton y permite saber en tiempo real cuales botones del teclado estan siendo pulsados
 * @author txaber
 */
public class KeyListener {
    private static KeyListener instance;
    private boolean[] keyPressed = new boolean[GLFW_KEY_LAST];
    private boolean[] keyBeginPress = new boolean[GLFW_KEY_LAST];

    private KeyListener() {
    }
    
    public static void endFrame() {
        Arrays.fill(get().keyBeginPress, false);
    }
    
    /**
     * Obtiene la instancia de la clase
     * @return una referencia a esta instancia
     */
    public static KeyListener get(){
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    } 
    
    /**
     * Callback para imgui
     * @param window
     * @param key
     * @param scancode
     * @param action
     * @param mods 
     */
    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        if (key > 0) {
            if (action == GLFW_PRESS) {
                get().keyPressed[key] = true;
                get().keyBeginPress[key] = true;
            } else if (action == GLFW_RELEASE) {
                get().keyPressed[key] = false;
                get().keyBeginPress[key] = false;
            }
        }
        
    }
    
    /**
     * Comprueba si un boton del teclado esta siendo pulsado
     * @param keyCode codigo GLFW del boton
     * @return true si el boton con el codigo pasado esta siendo pulsado, false de lo contrario
     */
    public static boolean isKeyPressed(int keyCode) {
        if (keyCode > 0) {
            return get().keyPressed[keyCode];
        }
        return false;
    }
    
    /**
     * Comprueba si un boton del teclado es pulsado en el primer frame 
     * @param keyCode codigo GLFW del boton
     * @return true si el boton con el codigo pasado esta siendo pulsado, false de lo contrario
     */
    public static boolean keyBeginPress(int keyCode) {
        if (keyCode > 0) {
            return get().keyBeginPress[keyCode];
        }
        return false;
    }
 }
