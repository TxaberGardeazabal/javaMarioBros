/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import UI.Digit;
import UI.Digitalizer;
import UI.FixedHUD;
import UI.UIButton;
import components.ComplexPrefabWrapper;
import components.MouseControls;
import components.StateMachine;
import components.TransitionMachine;
import components.gamecomponents.BlockCoin;
import gameEngine.GameObject;
import components.gamecomponents.BreakableBrick;
import components.gamecomponents.Coin;
import components.gamecomponents.FireRod;
import components.gamecomponents.Flag;
import components.gamecomponents.FlagPole;
import components.gamecomponents.Flower;
import components.gamecomponents.GoombaAI;
import components.gamecomponents.HoleLogic;
import components.gamecomponents.ItemBlock;
import components.gamecomponents.KoopaAI;
import components.gamecomponents.LevelController;
import components.gamecomponents.LiveMushroom;
import components.gamecomponents.MovingPlatform;
import components.gamecomponents.MushroomAI;
import components.gamecomponents.MusicController;
import components.gamecomponents.Pipe;
import components.gamecomponents.PlayerController;
import components.gamecomponents.StarAI;
import components.propertieComponents.StageHazard;
import components.propertieComponents.Ground;
import gameEngine.Direction;
import gameEngine.Prefab;
import gameEngine.PrefabSave;
import gameEngine.Window;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import java.util.List;
import javax.swing.JOptionPane;
import org.joml.Vector2f;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.PillboxCollider;
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
                        if (ImGui.collapsingHeader("Physics")) {
                            if (ImGui.menuItem("Add rigidbody")) {
                                if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
                                    activeGameObject.addComponent(new Rigidbody2D());
                                }
                            }

                            if (ImGui.menuItem("Add box collider")) {
                                activeGameObject.addComponent(new Box2DCollider());
                            }

                            if (ImGui.menuItem("Add circle collider")) {
                                activeGameObject.addComponent(new CircleCollider());
                            }
                            
                            if (ImGui.menuItem("Add pillbox collider")) {
                                activeGameObject.addComponent(new PillboxCollider());
                            }
                        }
                        
                        
                        
                        if (ImGui.collapsingHeader("Other")) {
                            if (ImGui.menuItem("Add State machine")) {
                                if (activeGameObject.getComponent(StateMachine.class) == null) {
                                    activeGameObject.addComponent(new StateMachine());
                                }
                            }
                            
                            if (ImGui.menuItem("Add Transition machine")) {
                                if (activeGameObject.getComponent(TransitionMachine.class) == null) {
                                    activeGameObject.addComponent(new TransitionMachine(false,false));
                                }
                            }
                        }
                        
                        if (ImGui.collapsingHeader("UI")) {
                            if (ImGui.menuItem("Add fixed position")) {
                                if (activeGameObject.getComponent(FixedHUD.class) == null) {
                                    activeGameObject.addComponent(new FixedHUD());
                                }
                            }
                            
                            if (ImGui.menuItem("Add digitalizer")) {
                                if (activeGameObject.getComponent(Digitalizer.class) == null) {
                                    activeGameObject.addComponent(new Digitalizer());
                                }
                            }
                            
                            if (ImGui.menuItem("Add digit")) {
                                if (activeGameObject.getComponent(Digit.class) == null) {
                                    activeGameObject.addComponent(new Digit());
                                }
                            }
                            
                            if (ImGui.menuItem("Add button")) {
                                if (activeGameObject.getComponent(UIButton.class) == null) {
                                    activeGameObject.addComponent(new UIButton());
                                }
                            }
                        }
                        
                        if (ImGui.collapsingHeader("game Logic")) {
                            if (ImGui.collapsingHeader("Player")) {
                                if (ImGui.menuItem("Add playerController")) {
                                    if (activeGameObject.getComponent(PlayerController.class) == null) {
                                        activeGameObject.addComponent(new PlayerController());
                                    }
                                }
                            }

                            if (ImGui.collapsingHeader("enemies")) {
                                if (ImGui.menuItem("Add goomba AI")) {
                                    if (activeGameObject.getComponent(GoombaAI.class) == null) {
                                        activeGameObject.addComponent(new GoombaAI());
                                    }
                                }
                                if (ImGui.menuItem("Add koopa AI")) {
                                    if (activeGameObject.getComponent(KoopaAI.class) == null) {
                                        activeGameObject.addComponent(new KoopaAI());
                                    }
                                }

                            }

                            if (ImGui.collapsingHeader("items")) {
                                if (ImGui.menuItem("Add mushroom AI")) {
                                    if (activeGameObject.getComponent(MushroomAI.class) == null) {
                                        activeGameObject.addComponent(new MushroomAI());
                                    }
                                }
                                if (ImGui.menuItem("Add fire flower AI")) {
                                    if (activeGameObject.getComponent(Flower.class) == null) {
                                        activeGameObject.addComponent(new Flower());
                                    }
                                }
                                if (ImGui.menuItem("Add star AI")) {
                                    if (activeGameObject.getComponent(StarAI.class) == null) {
                                        activeGameObject.addComponent(new StarAI());
                                    }
                                }
                                if (ImGui.menuItem("Add 1up mushroom AI")) {
                                    if (activeGameObject.getComponent(LiveMushroom.class) == null) {
                                        activeGameObject.addComponent(new LiveMushroom());
                                    }
                                }
                                if (ImGui.menuItem("Add coin")) {
                                    if (activeGameObject.getComponent(Coin.class) == null) {
                                        activeGameObject.addComponent(new Coin());
                                    }
                                }
                                if (ImGui.menuItem("Add BlockCoin")) {
                                    if (activeGameObject.getComponent(BlockCoin.class) == null) {
                                        activeGameObject.addComponent(new BlockCoin());
                                    }
                                }
                            }
                            
                            if (ImGui.collapsingHeader("Blocks")) {
                                if (ImGui.menuItem("Add breakable")) {
                                    if (activeGameObject.getComponent(BreakableBrick.class) == null) {
                                        activeGameObject.addComponent(new BreakableBrick());
                                    }
                                }

                                if (ImGui.menuItem("Add itemBlock")) {
                                    if (activeGameObject.getComponent(ItemBlock.class) == null) {
                                        activeGameObject.addComponent(new ItemBlock());
                                    }
                                }
                            }
                            
                            if (ImGui.collapsingHeader("properties")) {
                                if (ImGui.menuItem("Add ground")) {
                                    if (activeGameObject.getComponent(Ground.class) == null) {
                                        activeGameObject.addComponent(new Ground());
                                    }
                                }
                                if (ImGui.menuItem("Add obstacle")) {
                                    if (activeGameObject.getComponent(StageHazard.class) == null) {
                                        activeGameObject.addComponent(new StageHazard());
                                    }
                                }
                            }
                            
                            if (ImGui.collapsingHeader("Other")) {
                                if (ImGui.menuItem("Add pipe")) {
                                    if (activeGameObject.getComponent(Pipe.class) == null) {
                                        activeGameObject.addComponent(new Pipe(Direction.Down));
                                    }
                                }
                                
                                if (ImGui.menuItem("Add Hole logic")) {
                                    if (activeGameObject.getComponent(HoleLogic.class) == null) {
                                        activeGameObject.addComponent(new HoleLogic());
                                    }
                                }
                                
                                if (ImGui.menuItem("Add flag logic")) {
                                    if (activeGameObject.getComponent(Flag.class) == null) {
                                        activeGameObject.addComponent(new Flag());
                                    }
                                }
                                
                                if (ImGui.menuItem("Add flagpole logic")) {
                                    if (activeGameObject.getComponent(FlagPole.class) == null) {
                                        activeGameObject.addComponent(new FlagPole(false, 1));
                                    }
                                }
                                
                                if (ImGui.menuItem("Add MovingPlatform logic")) {
                                    if (activeGameObject.getComponent(MovingPlatform.class) == null) {
                                        activeGameObject.addComponent(new MovingPlatform());
                                    }
                                }
                                
                                if (ImGui.menuItem("Add fire rod logic")) {
                                    if (activeGameObject.getComponent(FireRod.class) == null) {
                                        activeGameObject.addComponent(new FireRod());
                                    }
                                }
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
                    ImBoolean test = new ImBoolean(true);
                    if (ImGui.collapsingHeader("Level controller",test)) {
                        lc.world = OImGui.inputText("world", lc.world);
                        lc.level = OImGui.inputText("level", lc.level);
                        lc.time = OImGui.dragFloat("level timer", lc.time);
                        lc.nextLevel = OImGui.inputText("next level", lc.nextLevel);

                        ImGui.separator();

                        OImGui.colorPicker4("sky color", lc.skyColor);
                        OImGui.colorPicker4("overworld color", lc.overworldColor);
                        OImGui.colorPicker4("underground color", lc.underGroundColor);
                    }
                    
                } else {
                    ImGui.text("no level controller");
                    
                    if (ImGui.button("add level controller object")) {
                        if (Window.getScene().getGameObjectWith(LevelController.class) == null) {
                            GameObject object = Prefab.generateLevelController();
                            Window.getScene().addGameObjectToScene(object);

                        }
                    }
                }
                
                GameObject mcObj = Window.getScene().getGameObjectWith(MusicController.class);
                if (mcObj != null) {
                    MusicController muc = mcObj.getComponent(MusicController.class);
                    ImBoolean test = new ImBoolean(true);
                    if (ImGui.collapsingHeader("Music controller",test)) {
                        muc.mainTheme = OImGui.inputText("main theme", muc.mainTheme);
                        muc.secondaryTheme = OImGui.inputText("main theme", muc.secondaryTheme);
                        muc.levelEndTheme = OImGui.inputText("main theme", muc.levelEndTheme);
                    }
                } else {
                    ImGui.text("no music controller");
                    
                    if (ImGui.button("add music controller object")) {
                        if (Window.getScene().getGameObjectWith(LevelController.class) == null) {
                            GameObject object = Prefab.generateLevelController();
                            Window.getScene().addGameObjectToScene(object);

                        }
                    }
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
