/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import components.MouseControls;
import gameEngine.GameObject;
import components.SpriteRenderer;
import components.gamecomponents.BreakableBrick;
import components.propertieComponents.Ground;
import imgui.internal.ImGui;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector4f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody2D;
import render.PickingTexture;
import scene.Scene;

/**
 *
 * @author txaber gardeazabal
 */
public class PropertiesWindow {
    private MouseControls mc;

    public PropertiesWindow(MouseControls mouseControls) {
        this.mc = mouseControls;
    }
    
    public void update(float dt) {
        
    }
    
    public void imGui() {
        ImGui.begin("Inspector");
        List<GameObject> activeGameObjects = mc.getActiveGameObjects();
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            GameObject activeGameObject = activeGameObjects.get(0);
            activeGameObject.imGui();
            
            // TODO: add more components here (and maybe make it better)
            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                if (ImGui.menuItem("Add rigidbody")) {
                    if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }
                
                if (ImGui.menuItem("Add box collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }
                
                if (ImGui.menuItem("Add circle collider")) {
                    if (activeGameObject.getComponent(CircleCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }
                
                if (ImGui.menuItem("Add ground")) {
                    if (activeGameObject.getComponent(Ground.class) == null) {
                        activeGameObject.addComponent(new Ground());
                    }
                }
                
                if (ImGui.menuItem("Add breakable")) {
                    if (activeGameObject.getComponent(BreakableBrick.class) == null) {
                        activeGameObject.addComponent(new BreakableBrick());
                    }
                }
                
                ImGui.endPopup();
            }
        } else if (!activeGameObjects.isEmpty()) {
            ImGui.text(activeGameObjects.size() + " selected objects");
        }
        else {
            ImGui.text("nothing to show");
        }
        ImGui.end();
    }

    public MouseControls getMc() {
        return mc;
    }
}
