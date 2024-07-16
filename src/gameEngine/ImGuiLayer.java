/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import components.MouseControls;
import editor.AssetWindow;
import editor.GameViewWindow;
import editor.MenuBar;
import editor.PropertiesWindow;
import editor.SceneHierarchyWindow;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import render.PickingTexture;
import scene.Scene;

/**
 *
 * @author txaber
 */

public class ImGuiLayer {
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    
    private long glfwWindow;
    private static GameViewWindow gameViewWindow;
    private static PropertiesWindow propertiesWindow;
    private static MenuBar menuBar;
    private static SceneHierarchyWindow sceneHierarchy;
    private static AssetWindow assetWindow;
    
    public ImGuiLayer(long glfwWindow) {
        this.glfwWindow = glfwWindow;
        // siempre inicializa la vvista de juego
        gameViewWindow = new GameViewWindow();
    }
    
    /*
    * initialize imGui context
    */
    public void initImGui(String glslVersion) {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        //io.addConfigFlags(ImGuiWindowFlags.MenuBar);
        // mess with fonts
        //io.setFontDefault();
        
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init(glslVersion);
    }
    
    public void destroyImGui() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
    
    public void update(float dt, Scene currentScene){
        startFrame();
        
        setupDockSpace();
        currentScene.imGui();
        // remove this
        //ImGui.showDemoWindow();
        if (gameViewWindow != null) {
            gameViewWindow.imGui();
        }
        if (propertiesWindow != null) {
            propertiesWindow.update(dt);
            propertiesWindow.imGui();
        }
        if (sceneHierarchy != null) {
            sceneHierarchy.imGui();
        }
        if (assetWindow != null) {
            assetWindow.imGui();
        }
        if (menuBar != null) {
            menuBar.imGui();
        }
        endFrame();
    }
    
    private void startFrame() {
         imGuiGlfw.newFrame();
         ImGui.newFrame();
    }
    
    private void endFrame() {
        glBindFramebuffer(GL_FRAMEBUFFER,0);
        glViewport(0, 0, Window.getWidth(), Window.getHeight());
        glClearColor(0,0,0,1);
        glClear(GL_COLOR_BUFFER_BIT);
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }
    
    public static void imGuiMouseButtonCallback(long window, int button, int action, int mods) {
        // makes it so the gameViewWindow doesn't get button events when the cursor its outside the viewport
        if (!ImGui.getIO().getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
            MouseListener.mouseButtonCallback(window, button, action, mods);
        }
    }
    
    public static void imGuiMouseScrollCallback(long window, double xOffset, double yOffset) {
        // makes it so the gameViewWindow doesn't get button events when the cursor its outside the viewport
        if (!ImGui.getIO().getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
            MouseListener.mouseScrollCallback(window, xOffset, yOffset);
        } else {
            MouseListener.clear();
        }
    }
    
    private void setupDockSpace() {
        //int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
        
        ImGuiViewport mainViewPort = ImGui.getMainViewport();
//        ImGui.setNextWindowPos(mainViewPort.getWorkPosX(), mainViewPort.getWorkPosY());
//        ImGui.setNextWindowSize(mainViewPort.getWorkSizeX(), mainViewPort.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewPort.getID());
        
        ImGui.setNextWindowPos(0.0f, 0.0f);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking 
                | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse 
                | ImGuiWindowFlags.NoResize |ImGuiWindowFlags.NoMove
                | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        
        ImGui.begin("dockspace demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);
        
        // dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));
        ImGui.end();
    }

    public void startAssetWindow(MouseControls mc) {
        assetWindow = new AssetWindow(mc);
    }
    
    public void startPropertiesWindow(MouseControls mc) {
        propertiesWindow = new PropertiesWindow(mc);
    }
    
    public void startSceneHierarchyWindow(MouseControls mc) {
        sceneHierarchy = new SceneHierarchyWindow(mc);
    }
    
    public void startMenuBar() {
        menuBar = new MenuBar();
    }
    
    public void endAssetWindow() {
        assetWindow = null;
    }
    
    public void endPropertiesWindow() {
        propertiesWindow = null;
    }
    
    public void endSceneHierarchyWindow() {
        sceneHierarchy = null;
    }
    
    public void endMenuBar() {
        menuBar = null;
    }
    
    public PropertiesWindow getPropertiesWindow() {
        return propertiesWindow;
    }
    
    public AssetWindow getAssetWindow() {
        return assetWindow;
    }
    
    
    public GameViewWindow getGameViewWindow() {
        return gameViewWindow;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public SceneHierarchyWindow getSceneHierarchy() {
        return sceneHierarchy;
    }
    
    
}
