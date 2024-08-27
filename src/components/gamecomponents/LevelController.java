/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.gamecomponents.PlayerController.PlayerState;
import editor.ConsoleWindow;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.joml.Vector4f;

/**
 *
 * @author txaber gardeazabal
 */
public class LevelController extends Component{
    
    private boolean hasBegun = false;
    private String saveFile = "assets/saveFile.txt";
    
    // level data
    public Vector4f skyColor = new Vector4f(92.0f, 148.0f, 252.0f, 1.0f);
    public Vector4f overworldColor = new Vector4f(92.0f, 148.0f, 252.0f, 1.0f);
    public Vector4f underGroundColor = new Vector4f(0.0f,0.0f,0.0f,0.0f);
    
    private String nextLevel = "";
    private String world;
    private String level;
    private float time;
    private transient float timeLeft = time;
    
    // player data
    private transient PlayerData pData;
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
}
