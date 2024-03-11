/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package scene;

import gameEngine.Window;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

/**
 *
 * @author txaber gardeazabal
 */
public class MainMenuSceneInitializer extends SceneInitializer{

    public MainMenuSceneInitializer() {
    }

    @Override
    public void init(Scene scene) {
        Window.get().changeTitle("Super mario bros");
        
    }

    @Override
    public void loadResources(Scene scene) {
    }

    @Override
    public void imGui() {
        ImGui.begin("Main menu", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoBackground);
        ImGui.button("Play");
        ImGui.button("Level select");
        ImGui.button("Level editor");
        ImGui.button("Exit");
        ImGui.end();
    }
    
}
