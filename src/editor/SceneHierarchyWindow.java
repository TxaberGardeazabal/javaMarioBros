/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import components.MouseControls;
import gameEngine.GameObject;
import components.propertieComponents.ShadowObj;
import gameEngine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import java.util.List;

/**
 * Controlador de la ventana de jerarquia del editor.
 * En la ventana de jerarquia se muestran todos los objetos dentro del nivel con las relaciones entre ellos, 
 * desde aqui se puede alterar el orden jerarquico de estos objetos arrastrandolos con el raton
 * @author txaber gardeazabal
 */
public class SceneHierarchyWindow {
    
    private static String payloadDropType = "SceneHierarchy";
    private MouseControls mc;
    
    public SceneHierarchyWindow(MouseControls mouseControls) {
        this.mc = mouseControls;
    }
    
    /**
     * Ejecuta codigo imgui para mostrar y actualizar la ventana
     */
    public void imGui() {
        ImGui.begin("Scene Hierarchy");
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, 40);
        ImGui.button("R\nO\nO\nT", 20, ImGui.getContentRegionAvailY() + ImGui.getScrollY());
        if (ImGui.beginDragDropTarget()) {
        Object payloadObj = ImGui.acceptDragDropPayload(payloadDropType);
            if (payloadObj != null) {
                if (payloadObj.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject receivedObj = (GameObject)payloadObj;

                    // delete old go reference
                    if (receivedObj.getParent() != null) {
                        receivedObj.getParent().removeChild(receivedObj);
                    }
                    receivedObj.setParent(null);

                    /*ConsoleWindow.addLog("payload receiver: root", 
                            ConsoleWindow.LogCategory.info);
                    ConsoleWindow.addLog("payload received: " + receivedObj.name, 
                            ConsoleWindow.LogCategory.info);
                    */
                }
            }
            ImGui.endDragDropTarget();
        }
        
        ImGui.nextColumn();
        
        List<GameObject> gameObjects  = Window.getScene().getGameObjects();
        int index = 0;
        for (GameObject obj : gameObjects) {
            if (!obj.doSerialization()) {
                continue;
            } else if (obj.getComponent(ShadowObj.class) != null) {
                continue;
            }
            
            if (obj.getParent() == null) {
                boolean treeNodeOpen = doTreeNode(obj, index);
                if (treeNodeOpen) {
                    ImGui.treePop();
                }
            }
            index++;
            
        }   
        
        ImGui.columns();
        ImGui.end();
    }
    
    /**
     * muestra los hijos del objeto en una estructura en arbol, hecho para llamar de forma recursiva
     * @param obj el objeto a mostrar
     * @param index el identificador unico para imgui
     * @return true si el nodo esta desplegado, false de lo contrario
     */
    private boolean doTreeNode(GameObject obj, int index) {
        ImGui.pushID(index);
        
        boolean treeNodeOpen = false;
        if (!obj.getChildGOs().isEmpty()) {
            treeNodeOpen = ImGui.treeNodeEx(
            obj.name, ImGuiTreeNodeFlags.DefaultOpen |
                    ImGuiTreeNodeFlags.FramePadding |
                    ImGuiTreeNodeFlags.OpenOnArrow |
                    ImGuiTreeNodeFlags.SpanAvailWidth, obj.name);

        } else {
            //ImGui.button(obj.name);
            ImGui.treeNodeEx(obj.name, ImGuiTreeNodeFlags.Leaf |
                    ImGuiTreeNodeFlags.FramePadding |
                    ImGuiTreeNodeFlags.SpanAvailWidth,obj.name);
            ImGui.treePop();
        }
        
        if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen()) {
            mc.setActiveGameObject(obj);
        }
        
        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload(payloadDropType ,obj);
            ImGui.text(obj.name);
            ImGui.endDragDropSource();
        }
        
        if (ImGui.beginDragDropTarget()) {
        Object payloadObj = ImGui.acceptDragDropPayload(payloadDropType);
            if (payloadObj != null) {
                if (payloadObj.getClass().isAssignableFrom(GameObject.class)) {
                    GameObject receivedObj = (GameObject)payloadObj;

                    if (!receivedObj.equals(obj) && !obj.getChildGOs().contains(receivedObj)) {
                        // delete old go reference
                        if (receivedObj.getParent() != null) {
                            receivedObj.getParent().removeChild(receivedObj);
                        }

                        obj.addChild(receivedObj);
                        receivedObj.setParent(obj);
                    }

                    /*ConsoleWindow.addLog("payload receiver: root", 
                            ConsoleWindow.LogCategory.info);
                    ConsoleWindow.addLog("payload received: " + receivedObj.name, 
                            ConsoleWindow.LogCategory.info);
                    */
                }
            }
            ImGui.endDragDropTarget();
        }
        
        if (treeNodeOpen && !obj.getChildGOs().isEmpty()) {
            for (int i = 0;i < obj.getChildGOs().size(); i++) {
                index++;
                boolean treeNodeOpen2 = doTreeNode(obj.getChildGOs().get(i), index);

                if (treeNodeOpen2) {
                    // this crashes the app, DO NOT USE
                    //ImGui.text("this is a child item");
                    ImGui.treePop();
                }

            }
        }
        ImGui.popID();
        
        return treeNodeOpen;
    }
}
