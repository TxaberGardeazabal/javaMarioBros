/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import editor.GameViewWindow;
import java.io.File;
import java.io.IOException;
import scene.LevelEditorSceneInitializer;
import scene.Scene;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.system.MemoryUtil.NULL;
import observers.EventSystem;
import observers.Observer;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.ALC_DEFAULT_DEVICE_SPECIFIER;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcGetString;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcOpenDevice;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glViewport;
import physics2D.Physics2D;
import render.DebugDraw;
import render.FontRenderBatch;
import render.FontTest;
import render.Framebuffer;
import render.PickingTexture;
import render.Renderer;
import render.Shader;
import scene.LevelSceneInitializer;
import scene.MainMenuSceneInitializer;
import scene.SceneInitializer;
import util.AssetPool;
/**
 * window for my game
 * suposedly the game will run on one window only (except all imgui stuff) so this class is singleton
 * @author txaber
 */
public class Window implements Observer{
    private String glslVersion = null;
    
    private long glfwWindow; // window pointer
    private int width, height;
    private String title;
    private static Window window = null;
    private ImGuiLayer imGuiLayer;
    private Framebuffer framebuffer;
    private PickingTexture pickingTexture;
    
    private long audioContext;
    private long audioDevice;
    
    private GameViewWindow gameViewWindow;
    private static Scene currentScene; 
    private boolean runtimePlaying = false;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "Super mario bros";
        EventSystem.addObserver(this);
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    /*
    * changes current scene to newScene and starts it
    */
    public static void changeScene(SceneInitializer sceneInitializer, String levelFilepath) {
        if (currentScene != null) {
            currentScene.Destroy();
        }
        
        currentScene = new Scene(sceneInitializer, levelFilepath);
        
        currentScene.load();
        currentScene.init();
        currentScene.start();
    }
    
    /*
    * returns the singleton class window
    */
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }
    
    public static Scene getScene() {
        return currentScene;
    }
    
    public static Physics2D getPhysics() {
        return currentScene.getPhysics();
    }
    
    /*
    * begins the window lifecycle
    */
    public void run() {
        System.out.println("using LWJGL version "+org.lwjgl.Version.getVersion());
        
        init();
        loop();
        destroy();
    }

    /*
    * initializes everything
    */
    private void init() {
        initWindow();
        this.imGuiLayer.initImGui(glslVersion);
        
        File file = new File("assets/levels/NewLevel.txt");
        /*if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        
        Window.changeScene(new LevelEditorSceneInitializer(), file.getAbsolutePath());
        //Window.changeScene(new MainMenuSceneInitializer(), glslVersion);
    }

    /*
    * this function refreshes every frame and calls other update methods
    */
    private void loop() {
        float beginTime = (float)GLFW.glfwGetTime();
        float endTime;
        float dt = -1.0f;
        
        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");
        
        while (!GLFW.glfwWindowShouldClose(glfwWindow)) {
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents();
            
            // render pass 1. render to picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();
            glViewport(0, 0, 1920, 1080);
            glClearColor(0,0,0,0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            Renderer.bindShader(pickingShader);
            currentScene.render();
            
            pickingTexture.disableWriting();
            glEnable(GL_BLEND);
            
            // render pass 2. render actual game
            DebugDraw.beginFrame();
            
            this.framebuffer.bind();
            Vector4f clearColor = currentScene.camera().clearColor;
            glClearColor(clearColor.x,clearColor.y,clearColor.z,clearColor.w);
            glClear(GL_COLOR_BUFFER_BIT);
            
            if (dt >= 0) {
                //System.out.println((1.0f / dt) + " FPS");
                Renderer.bindShader(defaultShader);
                
                if (runtimePlaying) {
                    currentScene.update(dt);
                } else {
                    currentScene.editorUpdate(dt);
                }
                
                currentScene.render();
                DebugDraw.draw();
                
                // font render test
                //GameObject go = currentScene.getGameObjectByName("Font_test");
                //go.getComponent(FontTest.class).render();
            }
            
            this.framebuffer.unbind();
            imGuiLayer.update(dt, currentScene);
            
            GLFW.glfwSwapBuffers(glfwWindow); // swap the color buffers
            KeyListener.endFrame();
            MouseListener.endFrame();
            
            endTime = (float)GLFW.glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
        
    }
    
    /*
    * ends this window and frees up all the allocated memory from window components
    * technically also ends the game
    */
    public void destroy(){
        alcDestroyContext(audioContext);
        alcCloseDevice(audioContext);
        this.imGuiLayer.destroyImGui();
        glfwFreeCallbacks(glfwWindow);
        GLFW.glfwDestroyWindow(glfwWindow);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
    
    public static void setWidth(int newWidth) {
        get().width = newWidth;
    }
    
    public static void setHeight (int newHeight) {
        get().height = newHeight;
    }
    
    public static int getWidth(){
        return get().width;
    }
    
    public static int getHeight(){
        return get().height;
    }
    
    public static Framebuffer getFrameBuffer() {
        return get().framebuffer;
    }
    
    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }

    /*
    * initialize and configure GLFW and openGL so the windows renders propertly
    */
    private void initWindow() {
        
        // error callback
        GLFWErrorCallback.createPrint(System.err).set();
        
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glslVersion = "#version 330";
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR,3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR,3);
        // Configure GLFW
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
        
        // Create the window
        glfwWindow = GLFW.glfwCreateWindow(this.width,this.height,this.title,NULL,NULL);
        if ( glfwWindow == NULL ) {
            throw new RuntimeException("Failed to create the GLFW window");
        }
        
        // mouseListener
        GLFW.glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        GLFW.glfwSetMouseButtonCallback(glfwWindow, ImGuiLayer::imGuiMouseButtonCallback);
        GLFW.glfwSetScrollCallback(glfwWindow, ImGuiLayer::imGuiMouseScrollCallback);
        
        // keyListener
        GLFW.glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        
        // window resize
        GLFW.glfwSetWindowSizeCallback(glfwWindow, (w, newWidth, newHeight) -> {
            this.setWidth(newWidth);
            this.setHeight(newHeight);
        });
                        
        // Make the OpenGL context current
	GLFW.glfwMakeContextCurrent(glfwWindow);
	// Enable v-sync
	GLFW.glfwSwapInterval(1);
        
        // Make the window visible
        GLFW.glfwShowWindow(glfwWindow);
        
        // initialize audio device
        String defauldDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        audioDevice = alcOpenDevice(defauldDeviceName);
        
        int[] attributes = {0};
        audioContext = alcCreateContext(audioDevice, attributes);
        alcMakeContextCurrent(audioContext);
        
        ALCCapabilities alcCapabilities = ALC.createCapabilities(audioDevice);
        ALCapabilities alCapabilities = AL.createCapabilities(alcCapabilities);
        
        if (!alCapabilities.OpenAL10) {
            assert false: "Audio library not supported.";
        }
        
        // This line is critical for LWJGL's interoperation with GLFW's
	// OpenGL context, or any context that is managed externally.
	// LWJGL detects the context that is current in the current thread,
	// creates the GLCapabilities instance and makes the OpenGL
	// bindings available for use.
	GL.createCapabilities();
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        this.framebuffer = new Framebuffer(1920,1080);
        this.pickingTexture = new PickingTexture(1920, 1080);
        glViewport(0,0,1920,1080);
        
        this.imGuiLayer = new ImGuiLayer(glfwWindow);
        this.gameViewWindow = new GameViewWindow();
    }
    
    public static ImGuiLayer getImGuiLayer() {
        return get().imGuiLayer;
    }

    @Override
    public void onNotify(GameObject go, Event event) {
        
        switch(event.type) {
            case GameEngineStartPlay:
                System.out.println("starting play");
                this.runtimePlaying = true;
                currentScene.save();
                Window.changeScene(new LevelSceneInitializer(), currentScene.getLevelFilepath());
                break;
            case GameEngineStopPlay:
                System.out.println("ending play");
                this.runtimePlaying = false;
                Window.changeScene(new LevelEditorSceneInitializer(), currentScene.getLevelFilepath());
                break;
            case LoadLevel:
                Window.changeScene(new LevelEditorSceneInitializer(), currentScene.getLevelFilepath());
                break;
            case SaveLevel:
                currentScene.save();
                break;
        }
        
        /*if (event.type == EventType.GameEngineStartPlay) {
            System.out.println("starting play");
        } else if (event.type == EventType.GameEngineStopPlay) {
            System.out.println("ending play");
        }*/
    }
    
    public static PickingTexture getPickingTexture() {
        return get().pickingTexture;
    }
    
    public void changeTitle(String newTitle) {
        GLFW.glfwSetWindowTitle(glfwWindow, newTitle);
    }
}
