/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import editor.ConsoleWindow;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Clase controladora de animaciones basadas en sprites. El componente statemachine controla las diferentes
 * animaciones de un objeto grafico asi como los cambios de estado entre una animacion y otra.
 * @author txaber gardeazabal
 */
public class StateMachine extends Component{
    /**
     * Una clase para poder notificar los cambios de animacion es necesaria
     */
    private class StateTrigger {
        public String state;
        public String trigger;
        
        public StateTrigger() {}

        public StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }
        @Override
        public boolean equals(Object o) {
            if (o.getClass() != StateTrigger.class) return false;
            StateTrigger t2 = (StateTrigger)o;
            return t2.trigger.equals(this.trigger) && t2.state.equals(this.state);
        }
        @Override
        public int hashCode() {
            return Objects.hash(trigger,state);
        } 
    }
    
    public HashMap<StateTrigger, String> stateTransfers = new HashMap<>();
    private List<AnimationState> states = new ArrayList();
    private transient AnimationState currentState = null;
    private String defaultStateTitle = "";
    
    /**
     * Refresca las texturas de todos los frames de animacion
     */
    public void refreshTextures() {
        for (AnimationState state : states) {
            state.refreshTexture();
        }
    }
    
    /**
     * define un cambio de una animacion a otra mediante una palabra "trigger"
     * @param from el identificador de la animacion origen
     * @param to el identificador de la animacion destino
     * @param onTrigger palabra clave para notificar el cambio
     */
    public void addStateTrigger(String from, String to, String onTrigger) {
        this.stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }
    
    /**
     * Incuye una animacion
     * @param state la animacion a guardar
     */
    public void addState(AnimationState state) {
        this.states.add(state);
    }
    
    /**
     * Define una animacion como el primer estado
     * @param animTitle identificador de la animacion por defecto
     */
    public void setDefaultState(String animTitle) {
        for (AnimationState state: states) {
            if (state.title.equals(animTitle)) {
                defaultStateTitle = animTitle;
                if (currentState == null) {
                    currentState = state;
                    return;
                }
            }
        }
        ConsoleWindow.addLog("Unable to find state '"+animTitle+"' in set default state", ConsoleWindow.LogCategory.warning);
    }
    
    /**
     * Notifica un cambio de animacion.
     * @param trigger palabra clave del cambio
     */
    public void trigger(String trigger) {
        for (StateTrigger state : stateTransfers.keySet()) {
            if (state.state.equals(currentState.title) && state.trigger.equals(trigger)) {
                if (stateTransfers.get(state) != null) {
                    int newStateIndex = -1;
                    int index = 0;
                    for (AnimationState s : states) {
                        if (s.title.equals(stateTransfers.get(state))) {
                            newStateIndex = index;
                            break;
                        }
                        index++;
                    }
                    
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }
        //ConsoleWindow.addLog("unable to find trigger " + trigger, ConsoleWindow.LogCategory.info);
    }
    
    /**
     * Altera todos los sprites de todas las animaciones con los de un spritesheet diferente.
     * el orden numerico de los sprites cambiados seran siendo los mismos, por ello
     * es importante que el nuevo spritesheet respete el orden original de los sprites,
     * se puede usar mientras corre el juego sin problemas
     * 
     * @param spriteSheet el spritesheet nuevo
     * @param offset distancia numerica relativa de los sprites originales hacia el nuevo spritesheet
     */
    public void changeSpriteSheetSkin(SpriteSheet spriteSheet, int offset) {
        for (AnimationState state : states) {
            state.changeUsingSpriteSheet(spriteSheet, offset);
            state.refreshTexture();
        }
    }
    
    @Override
    public void start() {
        for (AnimationState state : states) {
            if (state.title.equals(defaultStateTitle)) {
                currentState = state;
                break;
            }
        }
    }
    
    @Override
    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.setSprite(currentState.getCurrentSprite());
            }
        }
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.setSprite(currentState.getCurrentSprite());
            }
        }
    }
    
    @Override
    public void imGui() {
        int index = 0;
        for (AnimationState state : states) {
            ImString title = new ImString(state.title);
            ImGui.inputText("State: ", title);
            state.title = title.get();
            
            ImBoolean doesLoop = new ImBoolean(state.doesLoop());
            ImGui.checkbox("doesLoop" , doesLoop);
            state.setLoop(doesLoop.get());
            for (Frame frame : state.animationFrames) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame("+index+") Time: ", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }
}
