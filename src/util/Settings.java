/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.File;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Clase con todos los controles de la aplicacion.
 * @author txaber gardeazabal
 */
public class Settings {
    public static float GRID_WIDTH = 0.25f;
    public static float GRID_HEIGHT = 0.25f;
    
    public static float BUTTON_SIZE = 32;
    public static float BLOCK_WIDTH = 0.25F;
    public static float BLOCK_HEIGHT = 0.25F;
    
    // camera controlls
    public static int CAMERA_PINCH = GLFW_MOUSE_BUTTON_MIDDLE;
    public static int CAMERA_RESET = GLFW_KEY_KP_DECIMAL;
    
    // general controlls
    public static int MOUSE_SELECT = GLFW_MOUSE_BUTTON_LEFT;
    public static int MOUSE_BOX_SELECT = GLFW_MOUSE_BUTTON_RIGHT;
    public static int MOUSE_ACTION_WINDOW = GLFW_MOUSE_BUTTON_RIGHT;
    
    // key controlls
    public static int EDITOR_DELETE = GLFW_KEY_DELETE;
    public static int EDITOR_DROP_BLOCK = GLFW_KEY_ESCAPE;
    public static int EDITOR_PLACE_BLOCK = GLFW_MOUSE_BUTTON_LEFT;
    public static int EDITOR_PICK_BLOCK = GLFW_MOUSE_BUTTON_LEFT;
    public static int EDITOR_MOVE_BACK = GLFW_KEY_O;
    public static int EDITOR_BRING_FORWARD = GLFW_KEY_P;
    public static int GIZMO_TRANSLATE = GLFW_KEY_T;
    public static int GIZMO_ROTATE = GLFW_KEY_R;
    public static int GIZMO_SCALE = GLFW_KEY_S;
    public static int EDITOR_MOVE_UP = GLFW_KEY_UP;
    public static int EDITOR_MOVE_DOWN = GLFW_KEY_DOWN;
    public static int EDITOR_MOVE_LEFT = GLFW_KEY_LEFT;
    public static int EDITOR_MOVE_RIGHT = GLFW_KEY_RIGHT;
    public static int EDITOR_MOVE_INCREASE = GLFW_KEY_LEFT_SHIFT;
    
    // control keys
    public static int LEFT_CONTROL = GLFW_KEY_LEFT_CONTROL;
    public static int LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT;
    public static int EDITOR_DUPLICATE_SELECTION = GLFW_KEY_D;
    public static int EDITOR_NEW_LEVEL = GLFW_KEY_N;
    public static int EDITOR_OPEN_LEVEL = GLFW_KEY_O;
    public static int EDITOR_SAVE_LEVEL = GLFW_KEY_S;
    public static int EDITOR_RELOAD_LEVEL = GLFW_KEY_R;
    public static int EDITOR_DELETE_LEVEL = GLFW_KEY_D;
    
    // mario controlls
    public static int MOVELEFT = GLFW_KEY_A;
    public static int MOVERIGHT = GLFW_KEY_D;
    public static int CROUCH = GLFW_KEY_S;
    public static int JUMP = GLFW_KEY_SPACE;
    public static int ENTERPIPEDOWN = GLFW_KEY_S;
    public static int ENTERPIPELEFT = GLFW_KEY_A;
    public static int ENTERPIPEUP = GLFW_KEY_W;
    public static int ENTERPIPERIGHT = GLFW_KEY_D;
    public static int FIREBALL = GLFW_KEY_E;    
    public static int SPRINT = GLFW_KEY_LEFT_SHIFT;
    
    // menu options
    public static boolean mGrid = true;
    public static boolean mBorders = false;
    public static boolean mColisionBorders = false;
    public static boolean mPipeBorders = false;
    public static boolean mRaycasts = false;
    public static boolean mGroundRaycast = false;
    public static boolean mPitRaycast = true;
    public static boolean mOtherRaycast = false;
    public static boolean mConsole = true;
    
    // other
    public static float worldGravityMul = 0.7f;
    public static String mainMenuPath = "assets/levels/mainmenu.txt";
    public static String defaultLevel = "add filepath here when I make default level";
    public static String customPrefabPath = "assets/prefabs";
    
    /**
     * Convierte una ruta de asset absoluta en una relativa a la carpeta raiz
     * @param absPath
     * @return 
     */
    public static String getRelativePath(String absPath) {
        String rootFolder = "assets"; // could customize this?
        String[] res = absPath.split(rootFolder);
        return rootFolder.concat(res[1]);
    }
    
    /**
     * Comprueba si un archivo es de la extension deseada.
     * @param filepath ruta absoluta del fichero
     * @param desiredTypes tipo de archivo deseada
     * @return true si el archivo es de la extension esperada, false si tiene otra extension, o no existe
     */
    public static boolean isValidFile(String filepath, String desiredTypes) {
        File file = new File(filepath);
        if (!file.exists()) {
            return false;
        }
        
        String extension = "";
        int i = file.getName().lastIndexOf(".");
        if (i >= 0) {
            extension = file.getName().substring(i+1);
        }
        
        String[] types = desiredTypes.split(",");
        for (String type : types) {
            if (extension.equals(type)) {
                return true;
            }
        } 
        return false;
    }
}
