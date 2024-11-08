/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import UI.Digitalizer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import editor.ConsoleWindow;
import gameEngine.GameObject;
import gameEngine.Window;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
import org.joml.Vector4f;
import scene.LevelEditorSceneInitializer;
import scene.LevelSceneInitializer;
import scene.MainMenuSceneInitializer;
import util.AssetPool;

/**
 * 
 * @author txaber gardeazabal
 */
public class LevelController extends Component implements Observer{
    
    private boolean hasBegun = false;
    private String saveFile = "assets/saveFile.txt";
    private transient GameObject hud;
    
    // level data
    public Vector4f skyColor = new Vector4f(92.0f, 148.0f, 252.0f, 1.0f);
    public Vector4f overworldColor = new Vector4f(92.0f, 148.0f, 252.0f, 1.0f);
    public Vector4f underGroundColor = new Vector4f(0.0f,0.0f,0.0f,0.0f);
    
    public String nextLevel = "";
    public String world;
    public String level;
    public float time;
    private transient float timeLeft = time;
    private transient float timeBeforeLevelSwitch = 5;
    private transient boolean levelDone;
    
    // player data
    private transient PlayerData pData;
    private transient PlayerController player;
    
    // hud
    Digitalizer scoreD;
    Digitalizer coinsD;
    Digitalizer worldD;
    Digitalizer levelD;
    Digitalizer timeD;
    
    
    @Override
    public void start() {
        // load player data
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .create();
        
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(saveFile)));
        } catch (IOException ex ) {
            ConsoleWindow.addLog("Error al cargar: No se pudo acceder al archivo: "+saveFile+" ,comprueba que el archivo exista y esta aplicacion tenga permisos de edicion",
                    ConsoleWindow.LogCategory.error);
        }
        
        if (!inFile.equals("")) {
            pData = gson.fromJson(inFile, PlayerData.class);
        }
        
        GameObject tmp = Window.getScene().getGameObjectWith(PlayerController.class);
        if (tmp == null) {
            ConsoleWindow.addLog("LevelController: player not found",
                    ConsoleWindow.LogCategory.warning);
        } else {
            player = tmp.getComponent(PlayerController.class);
        }
        
        hud = Window.getScene().getGameObjectByName("HUD");
        if (hud == null) {
            ConsoleWindow.addLog("LevelController: hud reference not found",
                    ConsoleWindow.LogCategory.warning);
        } else {
            hud.transform.translate(new Vector2f(0,0));
        }
        
        timeLeft = time;
        levelDone = false;
        
        EventSystem.addObserver(this);
    }
    
    @Override
    public void update(float dt) {
        /*if (!hasBegun) {
            return;
        }*/
        if (player == null) {
            return;
        }
        
        if (hud != null) {
            scoreD = hud.getChildByName("puntos").getComponent(Digitalizer.class);
            coinsD = hud.getChildByName("monedas").getComponent(Digitalizer.class);
            worldD = hud.getChildByName("mundo").getComponent(Digitalizer.class);
            levelD = hud.getChildByName("nivel").getComponent(Digitalizer.class);
            timeD = hud.getChildByName("tiempo").getComponent(Digitalizer.class);

            scoreD.setValue(pData.score);
            coinsD.setValue(pData.coins);
            worldD.setValue(Integer.parseInt(world));
            levelD.setValue(Integer.parseInt(level));
            timeD.setValue((int) Math.floor(timeLeft));
        }
        
        
        if (levelDone) {
            timeBeforeLevelSwitch -= dt;
            if (timeLeft <= 0 && timeBeforeLevelSwitch <= 0) {
                // end level
                if (Window.getScene().getInitializer() instanceof LevelSceneInitializer) {
                    
                    pData.playerState = player.getPlayerState();
                    saveProgress();
                    // move to next level
                    Window.changeScene(new LevelSceneInitializer(), nextLevel);
                } else if (Window.getScene().getInitializer() instanceof LevelEditorSceneInitializer) {
                    // stop the editor runtime
                    EventSystem.notify(gameObject, new Event(EventType.EditorStopPlay));
                } else if (Window.getScene().getInitializer() instanceof MainMenuSceneInitializer) {
                    // do nothing
                }
            } else if (timeLeft >= 0) {
                timeLeft--;
                pData.score += 100;
                AssetPool.getSound("assets/sounds/coin.ogg").play();
            } 
        } else {
            if (timeLeft <= 0 || player.hasWon()) {
                return;
            }
            timeLeft -= dt;
        
            if (timeLeft <= 0 && !player.isDead()) {
                player.die();
            }
        }
        
    }
    
    /**
     * guarda las variables actuales del jugador en el archivo de save.
     */
    public void saveProgress() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .create();
        
        try {            
            FileWriter fe = new FileWriter(saveFile);
            
            fe.write(gson.toJson(pData));
            fe.close();
        } catch(IOException e) {
            ConsoleWindow.addLog("Error al guardar el archivo de save: no se pudo guardar el archivo "+saveFile, 
                    ConsoleWindow.LogCategory.error);
        }
    }

    @Override
    public void onNotify(GameObject go, Event event) {
        switch(event.type) {
            case CoinGet:
                if (pData.coins == 99) {
                    pData.lives++;
                    AssetPool.getSound("assets/sounds/1-up.ogg").play();
                    pData.coins = 0;
                } else {
                    pData.coins++;
                }
                return;
            case MarioWin:
                levelDone = true;

                return;
            case MarioDie:
                if (Window.getScene().getInitializer() instanceof MainMenuSceneInitializer) {
                    // do nothing
                    return;
                }
                
                pData.lives--;
                if (pData.lives <= 0) {
                    // game over
                    if (pData.topScore < pData.score) {
                        pData.topScore = pData.score;
                    }
                    saveProgress();
                    // TODO: level logic
                    ConsoleWindow.addLog("game over",
                        ConsoleWindow.LogCategory.info);
                }
                
                return;
            case ScoreUpdate:
                ConsoleWindow.addLog("score + "+event.getPayload("points"),
                    ConsoleWindow.LogCategory.info);
                pData.score += Integer.parseInt(event.getPayload("points"));

                return;
            case oneUp:
                AssetPool.getSound("assets/sounds/1-up.ogg").play();
                if (pData.lives < 99) {
                    pData.lives++;
                }
        }
    }
    
    @Override
    public void destroy() {
        EventSystem.removeObserver(this);
    }
    
    
}
