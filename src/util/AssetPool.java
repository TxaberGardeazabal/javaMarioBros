/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import components.SpriteSheet;
import editor.ConsoleWindow;
import gameEngine.Sound;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import render.Shader;
import render.Texture;

/**
 * Clase que engloba la base de datos de assets disponibles.
 * La clase assetPool contiene las referencias a todos los assets que tiene el motor, estos incluyen
 * texturas, shaders, spritesheets y sonidos, estos se cargan al iniciar el nivel y pueden ser accedidos con
 * llamadas estaticas a esta clase, usando la ruta relativa como referencia al asset a buscar
 * @author txaber
 */
public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();
    
    /**
     * Busca un shader dentro de la base de datos.
     * @param name string clave (ruta relativa) del asset
     * @return el shader relacionado con la clave, o null si no existe.
     */
    public static Shader getShader(String name) {
        try {
            File file = new File(name);
            if (!file.canRead() || file.isDirectory()) {
                throw new IOException();
            }
            
            if (AssetPool.shaders.containsKey(file.getAbsolutePath())) {
                return AssetPool.shaders.get(file.getAbsolutePath());
            }
            else {
                Shader shader = new Shader(name);
                shader.compile();
                AssetPool.shaders.put(file.getAbsolutePath(), shader);
                return shader;
            }
        } catch (IOException e) {
            ConsoleWindow.addLog("error: could not find texture "+name+"\n"+e.getMessage(), ConsoleWindow.LogCategory.warning);
        }
        return null;
    }
    
    /**
     * Busca una textura dentro de la base de datos.
     * @param name string clave (ruta relativa) del asset
     * @return la textura relacionado con la clave, o null si no existe.
     */
    public static Texture getTexture(String name) {
        try {
            File file = new File(name);
            if (!file.canRead() || file.isDirectory()) {
                throw new IOException();
            }
            
            if (AssetPool.textures.containsKey(file.getAbsolutePath())) {
                return AssetPool.textures.get(file.getAbsolutePath());
            }
            else {
                Texture texture = new Texture();
                texture.init(name);
                AssetPool.textures.put(file.getAbsolutePath(), texture);
                return texture;
            }
        } catch (IOException e) {
            ConsoleWindow.addLog("error: could not find texture "+name+"\n"+e.getMessage(), ConsoleWindow.LogCategory.warning);
        }
        return null;
    }
    
    /**
     * Añade un spritesheet a la base de datos de assets.
     * @param name string clave (ruta relativa) del asset
     * @param spriteSheet el spritesheet
     * @return el spritesheet si ha sido añadido, si no fue añadido correctamente devuelve null.
     */
    public static SpriteSheet addSpritesheet(String name, SpriteSheet spriteSheet) {
        try {
            File file = new File(name);
            if (!file.canRead() || file.isDirectory()) {
                throw new IOException();
            }
            
            if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
                AssetPool.spriteSheets.put(file.getAbsolutePath(), spriteSheet);
            }
            return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
        } catch (IOException e) {
            ConsoleWindow.addLog("error: could not find spriteSheet "+name+"\n"+e.getMessage(), ConsoleWindow.LogCategory.warning);
        }
        return null;
    }
    
    /**
     * Busca un spritesheet dentro de la base de datos.
     * @param name string clave (ruta relativa) del asset
     * @return el spritesheet relacionado con la clave, o null si no existe.
     */
    public static SpriteSheet getSpritesheet(String name) {
        File file = new File(name);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            ConsoleWindow.addLog("ERROR: (assetpool) tried to access spritesheet "+name+" and it has not been added yet", ConsoleWindow.LogCategory.warning);
        }
        
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
    
    /**
     * Añade un audio a la base de datos de assets.
     * @param name string clave (ruta relativa) del asset
     * @param loops si el audio tiene bucle
     * @return el audio si ha sido añadido, si no fue añadido correctamente devuelve null.
     */
    public static Sound addSound(String name, boolean loops) {
        try {
            File file = new File(name);
            if (!file.canRead() || file.isDirectory()) {
                throw new IOException();
            }
            
            if (!AssetPool.sounds.containsKey(file.getAbsolutePath())) {
                Sound sound = new Sound(file.getAbsolutePath(), loops);
                AssetPool.sounds.put(file.getAbsolutePath(), sound);
            }
            return AssetPool.sounds.get(file.getAbsolutePath());
        } catch (IOException e) {
            ConsoleWindow.addLog("error: could not find sound "+name+"\n"+e.getMessage(), ConsoleWindow.LogCategory.warning);
        }
        return null;
    }
    
    /**
     * Busca un audio dentro de la base de datos.
     * @param name string clave (ruta relativa) del asset
     * @return el audio relacionado con la clave, o null si no existe.
     */
    public static Sound getSound(String name) {
        File file = new File(name); 
        if (!AssetPool.sounds.containsKey(file.getAbsolutePath())) {
            ConsoleWindow.addLog("ERROR: (assetpool) tried to access sound file "+name+" and it has not been added yet", ConsoleWindow.LogCategory.warning);
        }
        return AssetPool.sounds.get(file.getAbsolutePath());
    }
    
    public static Collection<Sound> getAllSounds() {
        return AssetPool.sounds.values();
    }
    
    public static void stopAllSounds() {
        Collection<Sound> c = sounds.values();
        for (Sound s : c) {
            s.stop();
        }
    }
}
