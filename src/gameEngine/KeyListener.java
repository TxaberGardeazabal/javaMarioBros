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
 * the keyListener class handles all keyboard inputs using GLFW
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
    
    public static KeyListener get(){
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    } 
    
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
    
    public static boolean isKeyPressed(int keyCode) {
        if (keyCode > 0) {
            return get().keyPressed[keyCode];
        }
        return false;
    }
    
    public static boolean keyBeginPress(int keyCode) {
        if (keyCode > 0) {
            return get().keyBeginPress[keyCode];
        }
        return false;
    }
 }
