/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameEngine;

import editor.GameViewWindow;
import imgui.ImGui;
import java.util.Arrays;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

/**
 * the mouseListener class handles all mouse inputs using GLFW
 * @author txaber
 */
public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastWorldX, lastWorldY;
    private boolean[] mouseButtonPressed = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private boolean mouseDraging;
    
    private int mouseButtonsDown = 0;
    
    private static Vector2f gameViewportPos = new Vector2f();
    private static Vector2f gameViewportSize = new Vector2f();

    public MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
    }
    
    public static void clear() {
        get().scrollX = 0.0;
        get().scrollY = 0.0;
        get().xPos = 0.0;
        get().yPos = 0.0;
        get().mouseButtonsDown = 0;
        get().mouseDraging = false;
        Arrays.fill(get().mouseButtonPressed, false);
    }
    
    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }
    
    public static void mousePosCallback(long window, double xPos, double yPos) {
        if (!Window.getImGuiLayer().getGameViewWindow().getWantCaptureMouse()) {
            clear();
        }
        if (get().mouseButtonsDown > 0) {
            get().mouseDraging = true;
        }
        
        get().xPos = xPos;
        get().yPos = yPos;
    }
    
    public static void mouseButtonCallback(long window, int button, int action, int mods) { 
        if (action == GLFW_PRESS) {
            get().mouseButtonsDown++;
            if (button < GLFW_MOUSE_BUTTON_LAST) {
                get().mouseButtonPressed[button] = true;
            }

        }
        else if (action == GLFW_RELEASE) {
            get().mouseButtonsDown--;
            if (button < GLFW_MOUSE_BUTTON_LAST) {

                get().mouseButtonPressed[button] = false;
                get().mouseDraging = false;
            }
        }

        
    }
    
    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }
    
    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastWorldX = getWorldX();
        get().lastWorldY = getWorldY();
    }

    public static float getX() {
        return (float)get().xPos;
    }

    public static float getY() {
        return (float)get().yPos;
    }

    public static float getScrollX() {
        return (float)get().scrollX;
    }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static boolean isMouseDraging() {
        return get().mouseDraging;
    }
    
    public static boolean MouseButtonDown(int button) {
        if (button < GLFW_MOUSE_BUTTON_LAST) {
            return get().mouseButtonPressed[button];
        }
        return false;
    }
    
    public static Vector2f screenToWorld(Vector2f screenCoords) {
        Vector2f normalizedScreenCoords = new Vector2f(
            screenCoords.x / Window.getWidth(),
            screenCoords.y / Window.getHeight()
        );
        normalizedScreenCoords.mul(2.0f).sub(new Vector2f(1.0f,1.0f));
        Camera camera = Window.getScene().camera();
        Vector4f tmp = new Vector4f(normalizedScreenCoords.x,normalizedScreenCoords.y,0,1);
        Matrix4f inverseView = new Matrix4f(camera.getInverseView());
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        return new Vector2f(tmp.x, tmp.y);
    }
    
    public static Vector2f worldToScreen(Vector2f worldCoords) {
        Camera camera = Window.getScene().camera();
        Vector4f ndcSpacePos = new Vector4f(worldCoords.x, worldCoords.y, 0, 1);
        Matrix4f view = new Matrix4f(camera.getViewMatrix());
        Matrix4f projection = new Matrix4f(camera.getProjectionMatrix());
        ndcSpacePos.mul(projection.mul(view));
        Vector2f windowSpace = new Vector2f(ndcSpacePos.x, ndcSpacePos.y).mul(1.0f / ndcSpacePos.w);
        windowSpace.add(new Vector2f(1.0f,1.0f)).mul(0.5f);
        windowSpace.mul(new Vector2f(Window.getWidth(), Window.getHeight()));
        
        return windowSpace;
    }
    
    public static float getScreenX() {
        return getScreen().x;
    }
    
    public static float getScreenY() {
        return getScreen().y;
    }
    
    public static Vector2f getScreen() {
        float currentX = getX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 1920.0f;
        float currentY = getY() - gameViewportPos.y;
        currentY = 1080f -((currentY / gameViewportSize.y) * 1080f); // ImGui has its y value inverted
        
        return new Vector2f(currentX,currentY);
    }
    
    public static float getWorldX() {
        return getWorld().x;
    }
    
    public static float getWorldY() {
        return getWorld().y;
    }
    
    public static Vector2f getWorld() {
        float currentX = getX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 2.0f - 1.0f;
        float currentY = getY() - gameViewportPos.y;
        currentY = -((currentY / gameViewportSize.y) * 2.0f - 1.0f); // ImGui has its y value inverted
        Vector4f tmp = new Vector4f(currentX,currentY,0,1);
        
        Camera camera = Window.getScene().camera();
        Matrix4f inverseView = new Matrix4f(camera.getInverseView()); 
        Matrix4f inverseProjection = new Matrix4f(camera.getInverseProjection());
        tmp.mul(inverseView.mul(inverseProjection));
        
        return new Vector2f(tmp.x, tmp.y);
    }
    
    public static float getWorldDX() {
        return (float)(getWorldX() - get().lastWorldX);
    }
    
    public static float getWorldDY() {
        return (float)(getWorldY() - get().lastWorldY);
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        get().gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        get().gameViewportSize.set(gameViewportSize);
    }
    
    
}
