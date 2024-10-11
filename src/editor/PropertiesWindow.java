/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import UI.FixedHUD;
import components.ComplexPrefabWrapper;
import components.MouseControls;
import gameEngine.GameObject;
import components.gamecomponents.BreakableBrick;
import components.gamecomponents.LevelController;
import components.propertieComponents.Ground;
import gameEngine.PrefabSave;
import gameEngine.Window;
import imgui.internal.ImGui;
import java.util.List;
import javax.swing.JOptionPane;
import org.joml.Vector2f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody2D;
import util.Settings;

/**
 * Controlador de la ventana de propiedades del editor.
 * La ventana de editor muestra los componentes y los valores de variables del objeto seleccionado, donde se pueden alterar, añadir y borrar
 * para cambiar las funcionalidades por defecto de los gameobject.
 * @author txaber gardeazabal
 */
public class PropertiesWindow {
    private MouseControls mc;
    

    public PropertiesWindow(MouseControls mouseControls) {
        this.mc = mouseControls;
    }
    
    public void update(float dt) {
        
    }
    
    /**
     * Ejecuta codigo imgui para mostrar y actualizar la ventana
     */
    public void imGui() {
        ImGui.begin("properties");
        
        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Inspector")) {
                List<GameObject> activeGameObjects = mc.getActiveGameObjects();
                if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
                    GameObject activeGameObject = activeGameObjects.get(0);
                    activeGameObject.imGui();

                    ImGui.separator();
                    if (ImGui.button("save as prefab")) {
                        String res = JOptionPane.showInputDialog("introduce name of the prefab");
                        
                        if (res != null && !res.equals("")) {
                            // save in custom prefabs
                            PrefabSave file = new PrefabSave(Settings.customPrefabPath+"/"+res+".prefab");
                            // set position to 0
                            GameObject newObj = activeGameObject.copy();
                            newObj.transform.setPosition(new Vector2f().zero());
                            file.setPrefab(newObj);
                            file.save();
                            newObj.destroy();
                        }
                    }
                    
                    
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
                        
                        if (ImGui.menuItem("Add fixed position")) {
                            if (activeGameObject.getComponent(FixedHUD.class) == null) {
                                activeGameObject.addComponent(new FixedHUD());
                            }
                        }

                        ImGui.endPopup();
                    }
                } else if (!activeGameObjects.isEmpty()) {
                    ImGui.text(activeGameObjects.size() + " selected objects");
                    
                    ImGui.separator();
                    if (ImGui.button("save as prefab")) {
                        String res = JOptionPane.showInputDialog("introduce name of the prefab");
                        
                        if (res != null && !res.equals("")) {
                            mc.clearColor();
                            
                            // save in custom prefabs
                            GameObject root = Window.getScene().createGameObject("root");
                            ComplexPrefabWrapper cpw = new ComplexPrefabWrapper();
                            for ( GameObject go : activeGameObjects) {
                                cpw.addGameObject(go);
                            }
                            
                            root.addComponent(cpw);
                            PrefabSave file = new PrefabSave(Settings.customPrefabPath+"/"+res+".prefab");
                            file.setPrefab(root);
                            file.save();
                            file.destroy();
                        }
                    }
                }
                else {
                    ImGui.text("nothing to show");
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Level configuration")) {
                GameObject lcObj = Window.getScene().getGameObjectWith(LevelController.class);
                if (lcObj != null) {
                    LevelController lc = lcObj.getComponent(LevelController.class);
                    lc.world = OImGui.inputText("world", lc.world);
                    lc.level = OImGui.inputText("level", lc.level);
                    lc.time = OImGui.dragFloat("level timer", lc.time);
                    lc.nextLevel = OImGui.inputText("next level", lc.nextLevel);

                    OImGui.colorPicker4("sky color", lc.skyColor);
                    OImGui.colorPicker4("overworld color", lc.overworldColor);
                    OImGui.colorPicker4("underground color", lc.underGroundColor);
                } else {
                    ImGui.text("no level controller");
                }
                ImGui.endTabItem();
            }
            
            ImGui.endTabBar();
        }
        
        ImGui.end();
    }

    public MouseControls getMc() {
        return mc;
    }
}
