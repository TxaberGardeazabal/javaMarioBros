/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import gameEngine.MouseListener;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import gameEngine.Window;
import imgui.type.ImBoolean;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;
/**
 *
 * @author txaber gardeazabal
 */
public class GameViewWindow {
    
    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;
    private boolean showMenuBar = true;
    
    public void imGui() {
        ImGui.begin("Game viewport", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse
                        | ImGuiWindowFlags.MenuBar);
        if (showMenuBar) {
           ImGui.beginMenuBar();
        
            if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {

                isPlaying = true;
                EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
            }
            if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
                isPlaying = false;
                EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
            }
            ImGui.endMenuBar(); 
        } else {
            ImGui.collapsingHeader("Game viewport", new ImBoolean(false));
        }
        
        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);
        
        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        leftX = topLeft.x; 
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;
        
        /*leftX = windowPos.x +10;// HOTFIX add offset of 10 ep:45
        bottomY = windowPos.y;
        rightX = windowPos.x + windowSize.x +10;
        topY = windowPos.y + windowSize.y;
        */
        int TextureId = Window.getFrameBuffer().getTextureId();
        ImGui.image(TextureId, windowSize.x, windowSize.y,0,1,1,0);
        
        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y - 28)); // dunno how to fix, add offset
        //MouseListener.setGameViewportPos(new Vector2f(windowPos.x +10, windowPos.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));
        ImGui.end();
    }
    
    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // we switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }
        return new ImVec2(aspectWidth,aspectHeight);
    }
    
    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        
        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);
        
        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }

    public boolean isShowMenuBar() {
        return showMenuBar;
    }

    public void setShowMenuBar(boolean showMenuBar) {
        this.showMenuBar = showMenuBar;
    }
    
    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() <= topY && MouseListener.getY() >= bottomY; // inverted top-bottom
    }
    
    public void debugSoutViewportBounds() {
        System.out.println(String.valueOf(leftX) + " " + String.valueOf(rightX) + " " + String.valueOf(bottomY) + " " + String.valueOf(topY));
        System.out.println(String.valueOf("mouse position= " + MouseListener.getX()) + " " + String.valueOf(MouseListener.getY()));
    }
}
