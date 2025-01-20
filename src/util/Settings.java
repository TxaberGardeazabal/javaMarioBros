/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import components.SpriteSheet;
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
    public static String mainMenuLevel = "assets/levels/game/mainMenu.txt";
    public static String defaultLevel = "assets/levels/NewLevel.txt";
    public static String customPrefabPath = "assets/prefabs";
    public static String rootFolder = "assets";
    
    /**
     * Convierte una ruta de asset absoluta en una relativa a la carpeta raiz
     * @param absPath
     * @return 
     */
    public static String getRelativePath(String absPath) {
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
    
    /**
     * Carga todos los assets estaticos del juego y los carga en el diccionario para su uso.
     * De manera ideal las referencias a estas assets estarian guardados en un archivo de datos para cada
     * escena, de esa manera cada escena guarda en memoria solo los assets que tenga que utilizar en vez de gastar memoria
     */
    public static void loadResources() {
        // shaders
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.getShader("assets/shaders/debugLine2D.glsl");
        AssetPool.getShader("assets/shaders/fontShader.glsl");
        AssetPool.getShader("assets/shaders/pickingShader.glsl");
        
        // textures
        AssetPool.getTexture("assets/images/spriteSheets/particles/marioFireball.png");
        AssetPool.addSpritesheet("assets/images/gizmos.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/gizmos.png"),
                22, 48, 3, 2));
        
        // text textures
        AssetPool.getTexture("assets/images/text/mario.png");
        AssetPool.getTexture("assets/images/text/mundo.png");
        AssetPool.getTexture("assets/images/text/tiempo.png");
        AssetPool.getTexture("assets/images/text/empezar.png");
        AssetPool.getTexture("assets/images/text/editor.png");
        AssetPool.getTexture("assets/images/text/salir.png");
        AssetPool.getTexture("assets/images/text/seleccionar_nivel.png");
        AssetPool.getTexture("assets/images/text/super_mario_bros_title.png");
        
        // spritesheets
        AssetPool.addSpritesheet("assets/images/text/numeros.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/text/numeros.png"),
                56,56,10,8));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderground.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundUnderground.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderwater.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundUnderwater.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundCastle.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/groundCastle.png"),
                16,16,28,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryUnderground.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryUnderground.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryCastle.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryCastle.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndScenerySnow.png",
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesAndScenerySnow.png"),
                16,16,33,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/pipesFull.png"),
                32,32,4,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/skyOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/skyOverworld.png"),
                16,16,10,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/blocksAndScenery/skyCastle.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/blocksAndScenery/skyCastle.png"),
                16,16,10,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/coinBlocksOverworld.png"),
                16,16,15,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/coinBlocksUnderground.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/coinBlocksUnderground.png"),
                16,16,15,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/coinBlocksCastle.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/coinBlocksCastle.png"),
                16,16,15,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/coinBlocksUnderwater.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/coinBlocksUnderwater.png"),
                16,16,15,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioSmall.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioSmall.png"),
                16,16,30,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioBig.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioBig.png"),
                16,32,54,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioSmallStar.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioSmallStar.png"),
                16,16,45,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/marioBigStar.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/marioBigStar.png"),
                16,32,54,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/marioPowerups.png"),
                16,16,36,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/blockCoin.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/blockCoin.png"),
                8,16,4,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/marioParticles.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/marioParticles.png"),
                16,16,20,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png"),
                16,24,14,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenUnderground.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGreenUnderground.png"),
                16,24,14,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenUnderwater.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGreenUnderwater.png"),
                16,24,14,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesRed.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesRed.png"),
                16,24,15,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png"),
                16,16,8,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundUnderground.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGroundUnderground.png"),
                16,16,8,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundCastle.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGroundCastle.png"),
                16,16,8,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundUnderwater.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/enemiesGroundUnderwater.png"),
                16,16,8,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/enemies/bowser.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/enemies/bowser.png"),
                32,32,4,0));
        AssetPool.addSpritesheet("assets/images/text/fontFace.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/text/fontFace.png"),
                56,56,38,8));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/UIExtra.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/UIExtra.png"),
                16,16,4,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/platformsTileable.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/platformsTileable.png"),
                8,8,4,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/platformsTileable2.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/platformsTileable2.png"),
                16,8,2,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/particles/fireJet.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/particles/fireJet.png"),
                24,8,2,0));
        AssetPool.addSpritesheet("assets/images/spriteSheets/mario/toadAndPeach.png", 
                new SpriteSheet(AssetPool.getTexture("assets/images/spriteSheets/mario/toadAndPeach.png"),
                16,24,2,0));
        
        
        // sounds
        AssetPool.addSound("assets/sounds/1-up.ogg", false);
        AssetPool.addSound("assets/sounds/bowserfalls.ogg", false);
        AssetPool.addSound("assets/sounds/bowserfire.ogg", false);
        AssetPool.addSound("assets/sounds/bowser's-castle.ogg", false);
        AssetPool.addSound("assets/sounds/break_block.ogg", false);
        AssetPool.addSound("assets/sounds/bump.ogg", false);
        AssetPool.addSound("assets/sounds/coin.ogg", false);
        AssetPool.addSound("assets/sounds/fireball.ogg", false);
        AssetPool.addSound("assets/sounds/fireworks.ogg", false);
        AssetPool.addSound("assets/sounds/flagpole.ogg", false);
        AssetPool.addSound("assets/sounds/gameover.ogg", false);
        AssetPool.addSound("assets/sounds/invincible.ogg", false);
        AssetPool.addSound("assets/sounds/jump-small.ogg", false);
        AssetPool.addSound("assets/sounds/jump-super.ogg", false);
        AssetPool.addSound("assets/sounds/kick.ogg", false);
        AssetPool.addSound("assets/sounds/main-theme-overworld.ogg", false);
        AssetPool.addSound("assets/sounds/mario_die.ogg", false);
        AssetPool.addSound("assets/sounds/pipe.ogg", false);
        AssetPool.addSound("assets/sounds/powerup.ogg", false);
        AssetPool.addSound("assets/sounds/powerup_appears.ogg", false);
        AssetPool.addSound("assets/sounds/stage_clear.ogg", false);
        AssetPool.addSound("assets/sounds/stomp.ogg", false);
        AssetPool.addSound("assets/sounds/underground.ogg", false);
        AssetPool.addSound("assets/sounds/vine.ogg", false);
        AssetPool.addSound("assets/sounds/warning.ogg", false);
        AssetPool.addSound("assets/sounds/world_clear.ogg", false);
    }
}
