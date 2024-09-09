/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import UI.FixedHUD;
import components.MouseControls;
import gameEngine.GameObject;
import components.gamecomponents.BreakableBrick;
import components.gamecomponents.LevelController;
import components.propertieComponents.Ground;
import gameEngine.Window;
import imgui.internal.ImGui;
import java.util.List;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.Rigidbody2D;

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
                }
                else {
                    ImGui.text("nothing to show");
                }
                ImGui.endTabItem();
            }
            if (ImGui.beginTabItem("Level configuration")) {
                LevelController lc = Window.getScene().getGameObjectWith(LevelController.class).getComponent(LevelController.class);
                if (lc != null) {
                    lc.world = OImGui.inputText("mundo", lc.world);
                    lc.level = OImGui.inputText("nivel", lc.level);
                    lc.time = OImGui.dragFloat("tiempo maximo", lc.time);
                    lc.nextLevel = OImGui.inputText("nivel siguiente", lc.nextLevel);

                    OImGui.colorPicker4("color de cielo", lc.skyColor);
                    OImGui.colorPicker4("color de superficie", lc.overworldColor);
                    OImGui.colorPicker4("color de subterraneo", lc.underGroundColor);
                } else {
                    ImGui.text("No hay nivel");
                    ConsoleWindow.addLog("Properties window: level controller not found",
                            ConsoleWindow.LogCategory.warning);
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
