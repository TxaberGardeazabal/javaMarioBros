/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import editor.ConsoleWindow;
import gameEngine.GameObject;
import gameEngine.Sound;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class MusicController extends Component implements Observer{
    
    public transient Sound currentTrack;
    public transient boolean mainFlag = true;
    
    // set sounds
    private transient Sound invincible;
    private transient Sound warning;

    // level tracks
    public String mainTheme = "";
    public String secondaryTheme = "";
    public String levelEndTheme = "";
    private transient Sound mainTrack;
    private transient Sound secondaryTrack;
    private transient Sound levelEndTrack;
    
    @Override
    public void start() {
        mainTrack = AssetPool.getSound(mainTheme);
        secondaryTrack = AssetPool.getSound(secondaryTheme);
        levelEndTrack = AssetPool.getSound(levelEndTheme);
        invincible = AssetPool.getSound("assets/sounds/invincible.ogg");
        warning = AssetPool.getSound("assets/sounds/warning.ogg");
        
        if (mainTrack == null) {
            ConsoleWindow.addLog("MusicController: main theme not set",
                    ConsoleWindow.LogCategory.warning);
        } else {
            currentTrack = mainTrack;
            currentTrack.play();
        }
        
        if (secondaryTrack == null) {
            ConsoleWindow.addLog("MusicController: secondary theme not set",
                    ConsoleWindow.LogCategory.warning);
        }
        
        if (levelEndTrack == null) {
            ConsoleWindow.addLog("MusicController: end theme not set",
                    ConsoleWindow.LogCategory.warning);
        }
        
        EventSystem.addObserver(this);
    }
    
    @Override
    public void update(float dt) {
        if (currentTrack.equals(warning) && !currentTrack.isPlaying()) {
            if (mainFlag) {
                currentTrack = mainTrack;
            } else {
                currentTrack = secondaryTrack;
            }
            currentTrack.play();
        }
    }

    @Override
    public void onNotify(GameObject go, Event event) {
        switch(event.type) {
            case SwapTheme:
                currentTrack.stop();
                if (mainFlag) {
                    currentTrack = secondaryTrack;
                    mainFlag = false;
                } else {
                    currentTrack = mainTrack;
                    mainFlag = true;
                }
                currentTrack.play();
                break;
            case PlayWarning:
                currentTrack.stop();
                currentTrack = warning;
                currentTrack.play();
                break;
            case PlayInvincible:
                currentTrack.stop();
                currentTrack = invincible;
                currentTrack.play();
                break;
            case PlayEndTrack:
                currentTrack.stop();
                currentTrack = levelEndTrack;
                currentTrack.play();
                break;
            case StopAllTracks:
                currentTrack.stop();
                break;
            case ResumeMainTrack:
                if (mainFlag) {
                    currentTrack = mainTrack;
                } else {
                    currentTrack = secondaryTrack;
                }
                currentTrack.play();
                break;
        }
    }
    
    @Override
    public void destroy() {
        EventSystem.removeObserver(this);
    }
}
