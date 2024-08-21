/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import components.propertieComponents.NonPickable;
import gameEngine.GameObject;
import gameEngine.MouseListener;
import gameEngine.Prefab;
import gameEngine.Window;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Settings;

/**
 * Clase que contiene los componentes visuales y funcionalidades graficas para los gizmos individuales
 * del sistema de gizmos.
 * @author txaber gardeazabal
 */
public class Gizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(1,0.3f,0.3f,1);
    private Vector4f xAxisColorHover = new Vector4f(1,0,0,1);
    private Vector4f yAxisColor = new Vector4f(0.3f,1,0.3f,1);
    private Vector4f yAxisColorHover = new Vector4f(0,1,0,1);
    private Vector4f xyAxisColor = new Vector4f(0.3f,0.3f,1,1);
    private Vector4f xyAxisColorHover = new Vector4f(0,0,1,1);
    
    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private GameObject xyAxisObject;
    private SpriteRenderer xAxisSprite;
    private SpriteRenderer yAxisSprite;
    private SpriteRenderer xyAxisSprite;
    protected List<GameObject> activeGameObjects = null;
    private Vector2f xAxisOffset = new Vector2f(26f / 80f,0);
    private Vector2f yAxisOffset = new Vector2f(0,26f / 80f);
    private Vector2f xyAxisOffset = new Vector2f(16f / 80f, 6f / 80f);
    
    private float gizmoWidth = 16f / 80f;
    private float gizmoHeight = 48f / 80f;
    private float squareGizmoHeight = 20f / 80f * 2;
    private float squareGizmoWidth = 20f / 80f;
    protected boolean xAxisActive;
    protected boolean yAxisActive;
    protected boolean xyAxisActive;
    private boolean using = false;
    
    private MouseControls mc;
    
    public Gizmo(Sprite arrowSprite, Sprite blockSprite, MouseControls mouseControls) {
        this.xAxisObject = Prefab.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.yAxisObject = Prefab.generateSpriteObject(arrowSprite, gizmoWidth, gizmoHeight);
        this.xyAxisObject = Prefab.generateSpriteObject(blockSprite, squareGizmoWidth, squareGizmoHeight);
        this.xAxisSprite = this.xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSprite = this.yAxisObject.getComponent(SpriteRenderer.class);
        this.xyAxisSprite = this.xyAxisObject.getComponent(SpriteRenderer.class);
        this.mc = mouseControls;
        
        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());
        this.xyAxisObject.addComponent(new NonPickable());
        
        Window.getScene().addGameObjectToScene(this.xAxisObject);
        Window.getScene().addGameObjectToScene(this.yAxisObject);
        Window.getScene().addGameObjectToScene(this.xyAxisObject);
    }
    
    @Override
    public void start() {
        // set the arrows pointing at the right direction
        this.xAxisObject.transform.rotation = 90.0f;
        this.yAxisObject.transform.rotation = 180.0f;
        this.xAxisObject.transform.zIndex = 100;
        this.yAxisObject.transform.zIndex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
        this.xAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
        this.yAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
        
        this.xyAxisObject.transform.zIndex = 100;
        this.xyAxisObject.setNoSerialize();
        this.xyAxisObject.getComponent(SpriteRenderer.class).setColor(new Vector4f(0,0,0,0));
    }
    
    @Override
    public void update(float dt) {
        if (using) {
            this.setNotUsing();
        }
    }
    
    @Override
    public void editorUpdate(float dt) {
        if (!using) return;
        
        this.activeGameObjects = this.mc.getActiveGameObjects();
        if (this.activeGameObjects != null && !this.activeGameObjects.isEmpty()) {
            this.setActive();
        }
        else {
            this.setInactive();
            return;
        }
        
        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();
        boolean xyAxisHot = checkXYHoverState();
        
        if ((xAxisHot || xAxisActive) && MouseListener.isMouseDraging() && MouseListener.MouseButtonDown(Settings.MOUSE_SELECT)) {
            xAxisActive = true;
            yAxisActive = false;
            xyAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListener.isMouseDraging() && MouseListener.MouseButtonDown(Settings.MOUSE_SELECT)) {
            xAxisActive = false;
            yAxisActive = true;
            xyAxisActive = false;
        } else if ((xyAxisHot || xyAxisActive) && MouseListener.isMouseDraging() && MouseListener.MouseButtonDown(Settings.MOUSE_SELECT)) {
            xAxisActive = false;
            yAxisActive = false;
            xyAxisActive = true;
        } else {
            xAxisActive = false;
            yAxisActive = false;
            xyAxisActive = false;
        }
        
        if (this.activeGameObjects != null && !this.activeGameObjects.isEmpty()) {
            // calc the average position
            Vector2f avg = new Vector2f().zero();
            
            for (GameObject go : this.activeGameObjects) {
                avg.add(go.transform.position);
            }
            avg.div(new Vector2f(activeGameObjects.size(),activeGameObjects.size()));
            
            this.xAxisObject.transform.position.set(avg);
            this.yAxisObject.transform.position.set(avg);
            this.xyAxisObject.transform.position.set(avg);
            this.xAxisObject.transform.position.add(xAxisOffset);
            this.yAxisObject.transform.position.add(yAxisOffset);
            this.xyAxisObject.transform.position.add(xyAxisOffset);
        }
    }
    
    /**
     * muestra el gizmo en pantalla y activa sus funciones
     */
    public void setActive() {
        this.xAxisSprite.setColor(xAxisColor);
        this.yAxisSprite.setColor(yAxisColor);
        this.xyAxisSprite.setColor(xyAxisColor);
    }
    
    /**
     * oculta el gizmo de la pantalla
     */
    public void setInactive() {
        this.activeGameObjects = null;
        this.xAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.yAxisSprite.setColor(new Vector4f(0,0,0,0));
        this.xyAxisSprite.setColor(new Vector4f(0,0,0,0));
    }
    
    /**
     * Busca si el cursor esta colocado sobre la flecha X de gizmo
     * @return true si el cursor esta sobre la flecha X, false si no lo esta.
     */
    private boolean checkXHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= xAxisObject.transform.position.x + (gizmoHeight / 2.0f)
                && mousePos.x >= xAxisObject.transform.position.x - (gizmoWidth / 2.0f)
                && mousePos.y >= xAxisObject.transform.position.y - (gizmoHeight / 2.0f)
                && mousePos.y <= xAxisObject.transform.position.y + (gizmoWidth / 2.0f)) {
            xAxisSprite.setColor(xAxisColorHover);
            return true;
        }
        
        xAxisSprite.setColor(xAxisColor);
        return false;
    }
    
    /**
     * Busca si el cursor esta colocado sobre la flecha Y de gizmo
     * @return true si el cursor esta sobre la flecha Y, false si no lo esta.
     */
    private boolean checkYHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= yAxisObject.transform.position.x + (gizmoWidth / 2.0f)
                && mousePos.x >= yAxisObject.transform.position.x - (gizmoWidth / 2.0f)
                && mousePos.y <= yAxisObject.transform.position.y + (gizmoHeight / 2.0f)
                && mousePos.y >= yAxisObject.transform.position.y - (gizmoHeight / 2.0f)) {
            yAxisSprite.setColor(yAxisColorHover);
            return true;
        }
        
        yAxisSprite.setColor(yAxisColor);
        return false;
    }
    
    /**
     * Busca si el cursor esta colocado sobre la flecha XY de gizmo
     * @return true si el cursor esta sobre la flecha XY, false si no lo esta.
     */
    private boolean checkXYHoverState() {
        Vector2f mousePos = MouseListener.getWorld();
        if (mousePos.x <= xyAxisObject.transform.position.x + (squareGizmoWidth / 2.0f)
                && mousePos.x >= xyAxisObject.transform.position.x - (squareGizmoWidth / 2.0f)
                && mousePos.y <= xyAxisObject.transform.position.y + (squareGizmoHeight / 2.0f)
                && mousePos.y >= xyAxisObject.transform.position.y - (squareGizmoHeight / 2.0f)) {
            xyAxisSprite.setColor(xyAxisColorHover);
            return true;
        }
        
        xyAxisSprite.setColor(xyAxisColor);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }
    
    public void setNotUsing() {
        this.using = false;
        setInactive();
    }
}
