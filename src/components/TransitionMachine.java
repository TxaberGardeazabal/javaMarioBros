/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import java.util.ArrayList;

/**
 *
 * @author txaber gardeazabal
 */
public class TransitionMachine extends Component {
    
    private ArrayList<TransitionState> states = new ArrayList();
    private transient TransitionState currentState;
    private transient int stateIndex = 0;
    private boolean linear = true;
    private boolean loops;
    private boolean isPlaying = false;

    public TransitionMachine(boolean isLinear, boolean loops) {
        this.linear = isLinear;
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
                if (!linear) {
                    if (loops) {
                        currentState.start();
                    } else {
                        stop();
                    }
                    
                } else {
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
    
    public void startTransition(int index) {
        if (index > 0 && index < states.size()) {
            currentState = states.get(index);
            currentState.start();
            isPlaying = true;
        }
    }
}
