/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.TransitionState;
import java.util.ArrayList;

/**
 *
 * @author txaber gardeazabal
 */
public class PlayerControlTransition extends TransitionState {

    private ArrayList<SubStep> movement = new ArrayList();
    private int index;
    private PlayerController player;

    public PlayerControlTransition() {}
    
    @Override
    public void start() {
        index = 0;
        player = this.gameObject.getComponent(PlayerController.class);
        for (SubStep s : movement) {
            s.setPlayer(player);
            s.reset();
        }
    }

    @Override
    public void step(float dt) {
        
        if (!movement.isEmpty()) {
            movement.get(index).step(dt);
            if (movement.get(index).isFinished()) {
                index++;
                if (index == movement.size()) {
                    finished = true;
                }
            }
        }
    }
    
    public enum PlayerControl {
        MoveLeft,
        MoveRight,
        SprintLeft,
        SprintRight,
        Jump,
        Fireball,
        Wait
    }
    
    private class SubStep {
        private PlayerControl action;
        private float duration;
        private float timeTracker;
        private PlayerController player;
        private boolean finished = false;

        public SubStep(PlayerControl action, float duration) {
            this.action = action;
            this.duration = duration;
            this.timeTracker = 0;
        }

        public boolean isFinished() {
            return finished;
        }

        public void setPlayer(PlayerController player) {
            this.player = player;
        }
        
        public void reset() {
            this.timeTracker = 0;
            this.finished = false;
        }
        
        public void step(float dt) {
            switch(action) {
                case MoveLeft:
                    player.move(dt, false);
                    break;
                case MoveRight:
                    player.move(dt, true);
                    break;
                case SprintLeft:
                    player.sprint(dt, false);
                    break;
                case SprintRight:
                    player.sprint(dt, true);
                    break;
                case Jump:
                    player.jump(dt);
                    break;
                case Fireball:
                    player.fireball();
                    // fireball needs a single frame
                    finished = true;
                    break;
                case Wait:
                    // do nothing
                    break;
            }
            
            timeTracker += dt;

            if (timeTracker >= duration) {
                finished = true;
            }
        }
    }
    
    public void addSubStep(PlayerControl control, float duration) {
        movement.add(new SubStep(control, duration));
    }
}
