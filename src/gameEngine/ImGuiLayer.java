/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import components.MouseControls;
import editor.AssetWindow;
import editor.ConsoleWindow;
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
import scene.Scene;

/**
 * Controlador principal de las ventanas de imgui.
 * Esta clase mantiene referencias a todas las ventanas de imgui que usa la aplicacion, desde aqui se pueden llamar a crear las ventanas necesarias desde funciones individuales
 * para poder crear el ui necesario para cada momento, el dockspace que tiene imgui tambien deja anclar las ventanas donde necesites.
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
    private static ConsoleWindow consoleWindow;
    
    public ImGuiLayer(long glfwWindow) {
        this.glfwWindow = glfwWindow;
        // siempre inicializa la vvista de juego
        gameViewWindow = new GameViewWindow();
    }
    
    /*
    * Inicializa el contexto imgui
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
    
    /**
     * Destruye la instancia de imgui
     */
    public void destroyImGui() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }
    
    /**
     * Funcion que se ejecuta cada frame
     * @param dt tiempo en segundos desde el anterior frame
     * @param currentScene la escena actual
     */
    public void update(float dt, Scene currentScene){
        startFrame();
        
        setupDockSpace();
        currentScene.imGui();
        // remove this in test builds
        // ImGui.showDemoWindow();
        if (gameViewWindow != null) {
            gameViewWindow.imGui();
        }
        if (propertiesWindow != null) {
            propertiesWindow.imGui();
        }
        if (sceneHierarchy != null) {
            sceneHierarchy.imGui();
        }
        if (assetWindow != null) {
            assetWindow.imGui();
        }
        if (consoleWindow != null) {
            consoleWindow.imGui();
        }
        if (menuBar != null) {
            menuBar.imGui();
        }
        endFrame();
    }
    
    /**
     * Llama al inicio de frame de imgui
     */
    private void startFrame() {
         imGuiGlfw.newFrame();
         ImGui.newFrame();
    }
    
    /**
     * Llama al fin de frame de imgui
     */
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
    
    /**
     * Callback para que el mouselistener pueda recibir eventos de raton de imgui
     * @param window
     * @param button
     * @param action
     * @param mods 
     */
    public static void imGuiMouseButtonCallback(long window, int button, int action, int mods) {
        // makes it so the gameViewWindow doesn't get button events when the cursor its outside the viewport
        if (!ImGui.getIO().getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
            MouseListener.mouseButtonCallback(window, button, action, mods);
        }
    }
    
    /**
     * Callback para que el keylistener pueda recibir eventos de raton de imgui
     * @param window
     * @param xOffset
     * @param yOffset
     */
    public static void imGuiMouseScrollCallback(long window, double xOffset, double yOffset) {
        // makes it so the gameViewWindow doesn't get button events when the cursor its outside the viewport
        if (!ImGui.getIO().getWantCaptureMouse() || gameViewWindow.getWantCaptureMouse()) {
            MouseListener.mouseScrollCallback(window, xOffset, yOffset);
        } else {
            MouseListener.clear();
        }
    }
    
    /**
     * Inicia el dockspace de imgui
     */
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

    /**
     * Inicia y abre la ventana de assets
     * @param mc mousecontrols
     */
    public void startAssetWindow(MouseControls mc) {
        assetWindow = new AssetWindow(mc);
    }
    
    /**
     * Inicia y abre la ventana de propiedades
     * @param mc mousecontrols
     */
    public void startPropertiesWindow(MouseControls mc) {
        propertiesWindow = new PropertiesWindow(mc);
    }
    
    /**
     * Inicia y abre la ventana de jerarquia
     * @param mc mousecontrols
     */
    public void startSceneHierarchyWindow(MouseControls mc) {
        sceneHierarchy = new SceneHierarchyWindow(mc);
    }
    
    /**
     * Inicia y abre la ventana de consola
     */
    public void startConsoleWindow() {
        consoleWindow = new ConsoleWindow();
    }
    
    /**
     * Inicia y abre la barra de menu
     */
    public void startMenuBar() {
        menuBar = new MenuBar();
    }
    
    /**
     * Cierra la ventana de assets
     */
    public void endAssetWindow() {
        assetWindow = null;
    }
    
    /**
     * Cierra la ventana de propiedades
     */
    public void endPropertiesWindow() {
        propertiesWindow = null;
    }
    
    /**
     * Cierra la ventana de jerarquia
     */
    public void endSceneHierarchyWindow() {
        sceneHierarchy = null;
    }
    
    /**
     * Cierra la ventana de consola
     */
    public void endConsoleWindow() {
        consoleWindow = null;
    }
    
    /**
     * Cierra la barra de menu
     */
    public void endMenuBar() {
        menuBar = null;
    }
    
    /**
     * Devuelve la ventana de propiedades
     * @return una referencia de la ventana
     */
    public PropertiesWindow getPropertiesWindow() {
        return propertiesWindow;
    }
    
    /**
     * Devuelve la ventana de assets
     * @return una referencia de la ventana
     */
    public AssetWindow getAssetWindow() {
        return assetWindow;
    }
    
    /**
     * Devuelve la ventana principal de juego
     * @return una referencia de la ventana
     */
    public GameViewWindow getGameViewWindow() {
        return gameViewWindow;
    }

    /**
     * Devuelve la barra de menu
     * @return una referencia de la ventana
     */
    public MenuBar getMenuBar() {
        return menuBar;
    }

    /**
     * Devuelve la ventana de jerarquia
     * @return una referencia de la ventana
     */
    public SceneHierarchyWindow getSceneHierarchy() {
        return sceneHierarchy;
    }
    
    /**
     * Devuelve la ventana de consola
     * @return una referencia de la ventana
     */
    public ConsoleWindow getConsoleWindow() {
        return consoleWindow;
    }
}
