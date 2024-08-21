/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.util.ArrayList;
import java.util.List;
import util.AssetPool;

/**
 * Cada estado individual de la animacion, la animacion basada en sprites funciona cambiando los
 * sprites que se muestran en pantalla con sprites de la lista de animacion, esto se logra
 * guardando cada uno de los frames de la animacion y cambiandolos cada cierta cantidad de tiempo definido
 * @author txaber gardeazabal
 */
public class AnimationState {
    public String title;
    public List<Frame> animationFrames = new ArrayList<>();
    
    private boolean doesLoop = false;
    private static Sprite defaultSprite = new Sprite();
    private transient float timeTracker = 0.0f;
    private transient int currentSprite = 0;
    
    /**
     * Recarga las texturas en el GPU
     */
    public void refreshTexture() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }
    
    /**
     * Añade un nuevo frame a la animacion.
     * 
     * @param sprite el sprite a ser mostrado durante el frame
     * @param frameTime tiempo en el que se va a mostrar el sprite antes de cambiar al siguiente
     */
    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }
    
    /**
     * Añade un nuevo frame a la animacion.
     * 
     * @param sprite el sprite a ser mostrado durante el frame
     * @param frameTime tiempo en el que se va a mostrar el sprite antes de cambiar al siguiente
     * @param spriteNumber numero del sprite en la secuencia del spritesheet original
     */
    public void addFrame(Sprite sprite, float frameTime, int spriteNumber) {
        animationFrames.add(new Frame(sprite, frameTime, spriteNumber));
    }
    
    public void setLoop(boolean doesLoop) {
        this.doesLoop = doesLoop;
    }
    
    public boolean doesLoop() {
        return this.doesLoop;
    }
    
    public void update(float dt) {
        if (currentSprite < animationFrames.size()) {
            timeTracker -= dt;
            if (timeTracker <= 0) {
                if (!(currentSprite == animationFrames.size() -1 && !doesLoop)) {
                    currentSprite = (currentSprite + 1) % animationFrames.size();
                }
                timeTracker = animationFrames.get(currentSprite).frameTime;
            }
        }
    }
    
    /**
    * Devuelve el sprite actual.
    * @return el sprite siendo mostrado en pantalla
    */
    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }
        
        return defaultSprite;
    }
    
    /**
     * Altera todos los sprites de la animacion con los de un spritesheet diferente.
     * el orden numerico de los sprites cambiados seran siendo los mismos, por ello
     * es importante que el nuevo spritesheet respete el orden original de los sprites,
     * se puede usar mientras corre el juego sin problemas
     * 
     * @param spriteSheet el spritesheet nuevo
     * @param offset distancia numerica relativa de los sprites originales hacia el nuevo spritesheet
     */
    public void changeUsingSpriteSheet(SpriteSheet spriteSheet, int offset) {
        for (Frame frame : animationFrames) {
            if (frame.spriteNumber + offset < spriteSheet.size()) {
                frame.sprite = spriteSheet.getSprite(frame.spriteNumber + offset);
            }
        }
    }
}
