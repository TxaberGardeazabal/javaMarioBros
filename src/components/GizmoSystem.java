/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.KeyListener;
import gameEngine.Window;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class GizmoSystem extends Component{
    private SpriteSheet gizmos;
    private int usingGizmo = 0;
    private MouseControls mc;

    public GizmoSystem(SpriteSheet gizmoSprites, MouseControls mouseControls) {
        this.gizmos = gizmoSprites;
        this.mc = mouseControls;
    }
    
    @Override
    public void start() {
        this.gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1), gizmos.getSprite(0),
        mc));
        this.gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), gizmos.getSprite(0),
        mc));
    }
    
    @Override
    public void editorUpdate(float dt) {
        switch (usingGizmo) {
            case 0:
                this.gameObject.getComponent(TranslateGizmo.class).setUsing();
                this.gameObject.getComponent(ScaleGizmo.class).setNotUsing();
                break;
            case 1:
                this.gameObject.getComponent(TranslateGizmo.class).setNotUsing();
                this.gameObject.getComponent(ScaleGizmo.class).setUsing();
        }
        
        if (KeyListener.isKeyPressed(Settings.GIZMO_TRANSLATE)) {
            usingGizmo = 0;
        } else if (KeyListener.isKeyPressed(Settings.GIZMO_SCALE)) {
            usingGizmo = 1;
        }
    }
}
