/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.util.ArrayList;
import java.util.List;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class AnimationState {
    public String title;
    public List<Frame> animationFrames = new ArrayList<>();
    private boolean doesLoop = false;
    
    private static Sprite defaultSprite = new Sprite();
    private transient float timeTracker = 0.0f;
    private transient int currentSprite = 0;
    
    public void refreshTexture() {
        for (Frame frame : animationFrames) {
            frame.sprite.setTexture(AssetPool.getTexture(frame.sprite.getTexture().getFilepath()));
        }
    }
    
    public void addFrame(Sprite sprite, float frameTime) {
        animationFrames.add(new Frame(sprite, frameTime));
    }
    
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
        //System.out.println(this.title);
        //System.out.println(animationFrames.get(currentSprite).spriteNumber);
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
    
    public Sprite getCurrentSprite() {
        if (currentSprite < animationFrames.size()) {
            return animationFrames.get(currentSprite).sprite;
        }
        
        return defaultSprite;
    }
    
    public void changeUsingSpriteSheet(SpriteSheet spriteSheet, int offset) {
        for (Frame frame : animationFrames) {
            frame.sprite = spriteSheet.getSprite(frame.spriteNumber + offset);
        }
    }
}
