/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gameEngine;

import UI.ButtonBehavior;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.ButtonBehaviorDeserializer;
import components.ComplexPrefabWrapper;
import components.Component;
import components.ComponentDeserializer;
import components.SpriteRenderer;
import components.StateMachine;
import editor.ConsoleWindow;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author txaber gardeazabal
 */
public class PrefabSave {
    
    private String filepath;
    private GameObject prefab;
    private ComplexPrefabWrapper cpw;

    public PrefabSave(String filepath) {
        this.filepath = filepath;
    }

    public GameObject getPrefab() {
        return prefab;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setPrefab(GameObject prefab) {
        this.prefab = prefab;
        if (prefab.getComponent(ComplexPrefabWrapper.class) != null) {
            this.cpw = prefab.getComponent(ComplexPrefabWrapper.class);
        }
    }
    
    public void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(ButtonBehavior.class, new ButtonBehaviorDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        
        try {
            
            FileWriter fe = new FileWriter(filepath);
            
            fe.write(gson.toJson(prefab));
            fe.close();
        } catch(IOException e) {
            ConsoleWindow.addLog("Error al guardar: no se pudo guardar el archivo "+filepath, 
                    ConsoleWindow.LogCategory.error);
        }
    }
    
    public GameObject load() {
        Gson gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .registerTypeAdapter(ButtonBehavior.class, new ButtonBehaviorDeserializer())
                .enableComplexMapKeySerialization()
                .create();
        
        String inFile = "";
        try {
            inFile = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException ex ) {
            ConsoleWindow.addLog("Error al cargar: No se pudo acceder al archivo: "+filepath+" ,comprueba que el archivo exista y esta aplicacion tenga permisos de edicion",
                    ConsoleWindow.LogCategory.error);
        }
        
        if (!inFile.equals("")) {
            prefab = gson.fromJson(inFile, GameObject.class);
            
            if (prefab.getComponent(ComplexPrefabWrapper.class) != null) {
                this.cpw = prefab.getComponent(ComplexPrefabWrapper.class);
            }
            
            if (this.cpw == null) {
                this.prefab.refreshTextures();
            } else {
                for (GameObject go : this.cpw.getGameObjects()) {
                    go.refreshTextures();
                }
            }
            
            return prefab;
        } else {
            return null;
        }
    }    
    
    public void destroy() {
        for (GameObject go : this.cpw.getGameObjects()) {
            go.destroy();
        }
        
        this.prefab.destroy();
    }
    
    
}
