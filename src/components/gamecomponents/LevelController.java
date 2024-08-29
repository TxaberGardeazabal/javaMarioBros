/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

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
    
    // level data
    public Vector4f skyColor = new Vector4f(92.0f, 148.0f, 252.0f, 1.0f);
    public Vector4f overworldColor = new Vector4f(92.0f, 148.0f, 252.0f, 1.0f);
    public Vector4f underGroundColor = new Vector4f(0.0f,0.0f,0.0f,0.0f);
    
    public String nextLevel = "";
    public String world;
    public String level;
    public float time;
    private transient float timeLeft = time;
    
    // player data
    private transient PlayerData pData;
    private transient PlayerController player;
    /*private transient int coins;
    private transient int lives;
    private transient int score;
    private transient PlayerController.PlayerState playerState;
    private transient int topScore;*/
    
    
    @Override
    public void start() {
        // load player data
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .create();
        
        String inFile = "";
        try {
            String[] temp = saveFile.split("/");
            String levelName = temp[temp.length-1];
            
            inFile = new String(Files.readAllBytes(Paths.get(levelName)));
        } catch (IOException ex ) {
            ConsoleWindow.addLog("Error al cargar: No se pudo acceder al archivo: "+saveFile+" ,comprueba que el archivo exista y esta aplicacion tenga permisos de edicion",
                    ConsoleWindow.LogCategory.error);
        }
        
        if (!inFile.equals("")) {
            pData = gson.fromJson(inFile, PlayerData.class);
        }
        
        player = Window.getScene().getGameObjectWith(PlayerController.class).getComponent(PlayerController.class);
        if (player == null) {
            ConsoleWindow.addLog("LevelController: player not found",
                    ConsoleWindow.LogCategory.warning);
        }
        
        EventSystem.addObserver(this);
    }
    
    @Override
    public void update(float dt) {
        if (!hasBegun) {
            return;
        }
        
        
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (hasBegun) {
            ConsoleWindow.addLog(pData.coins+" "+pData.score+" "+pData.topScore, ConsoleWindow.LogCategory.info);
            /*pData = new PlayerData();
            pData.coins = 10;
            pData.coins = 10;
            pData.score = 100;
            pData.playerState = PlayerState.Small;
            pData.topScore = 0;
            saveProgress();*/
        }
    }
    
    public void imgui() {
        
    }
    
    public void saveProgress() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .enableComplexMapKeySerialization()
                .create();
        
        try {
            String[] temp = saveFile.split("/");
            String levelName = temp[temp.length-1];
            
            FileWriter fe = new FileWriter(levelName);
            
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
                
                pData.playerState = player.getPlayerState();
                // convert time to score
                // TODO: move this call to level scene only
                saveProgress();
                
                if (Window.getScene().getInitializer() instanceof LevelSceneInitializer) {
                    // move to next level
                    Window.changeScene(new LevelSceneInitializer(), nextLevel);
                } else if (Window.getScene().getInitializer() instanceof LevelEditorSceneInitializer) {
                    // stop the editor runtime
                    EventSystem.notify(gameObject, new Event(EventType.EditorStopPlay));
                } else if (Window.getScene().getInitializer() instanceof MainMenuSceneInitializer) {
                    // do nothing
                }

                return;
            case MarioDie:
                
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
