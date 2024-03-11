/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import components.SpriteSheet;
import gameEngine.Sound;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import render.Shader;
import render.Texture;

/**
 *
 * @author txaber
 */
public class AssetPool {
    private static Map<String, Shader> shaders = new HashMap<>();
    private static Map<String, Texture> textures = new HashMap<>();
    private static Map<String, SpriteSheet> spriteSheets = new HashMap<>();
    private static Map<String, Sound> sounds = new HashMap<>();
    
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
            System.out.println("error: could not find texture "+name);
        }
        return null;
    }
    
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
            System.out.println("error: could not find texture "+name+" "+e.getMessage());
        }
        return null;
    }
    
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
            System.out.println("error: could not find texture "+name+" "+e.getMessage());
        }
        return null;
    }
    
    public static SpriteSheet getSpritesheet(String name) {
        File file = new File(name);
        if (!AssetPool.spriteSheets.containsKey(file.getAbsolutePath())) {
            assert false : "ERROR: (assetpool) tried to access spritesheet "+name+" and it has not been added yet";
        }
        
        return AssetPool.spriteSheets.getOrDefault(file.getAbsolutePath(), null);
    }
    
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
            System.out.println("error: could not find texture "+name);
        }
        return null;
    }
    
    public static Sound getSound(String name) {
        File file = new File(name); 
        if (!AssetPool.sounds.containsKey(file.getAbsolutePath())) {
            assert false : "ERROR: (assetpool) tried to access sound file "+name+" and it has not been added yet";
        }
        return AssetPool.sounds.get(file.getAbsolutePath());
    }
    
    public static Collection<Sound> getAllSounds() {
        return AssetPool.sounds.values();
    }
    
    
}
