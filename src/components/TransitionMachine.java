/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import editor.ConsoleWindow;
import java.util.ArrayList;

/**
 *
 * @author txaber gardeazabal
 */
public class TransitionMachine extends Component {
    
    private ArrayList<TransitionState> states = new ArrayList();
    private transient TransitionState currentState;
    private transient int stateIndex = 0;
    private boolean loops;
    private boolean isPlaying = false;

    public TransitionMachine(boolean loops) {
        this.loops = loops;
    }
    
    @Override
    public void start() {
        for (TransitionState state : states) {
            state.gameObject = this.gameObject;
        }
    }
    
    @Override
    public void update(float dt) {
        if (isPlaying) {
            currentState.step(dt);
            
            if (currentState.hasEnded()) {
                stateIndex++;
                if (stateIndex >= states.size()) {
                    if (!loops) {
                        end();
                    } else {
                        stateIndex = 0;
                        currentState = states.get(stateIndex);
                        currentState.start();
                    }
                } else {
                    currentState = states.get(stateIndex);
                    currentState.start();
                }
            }
        }
        
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void begin() {
        //ConsoleWindow.addLog("transition begin", ConsoleWindow.LogCategory.info);
        stateIndex = 0;
        currentState = states.get(stateIndex);
        currentState.start();
        this.isPlaying = true;
    }
    
    public void stop() {
        this.isPlaying = false;
    }
    
    public void end() {
        //ConsoleWindow.addLog("transition end", ConsoleWindow.LogCategory.info);
        stop();
        stateIndex = 0;
    }
    
    public void addTransition(TransitionState state) {
        this.states.add(state);
    }
}
