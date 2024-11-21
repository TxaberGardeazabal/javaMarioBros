/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.Camera;
import gameEngine.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import render.DebugDraw;
import util.Settings;

/**
 * Componente que genera la cuadricula en la escena editor
 * @author txaber gardeazabal
 */
public class GridLines extends Component{
    
    @Override
    public void editorUpdate(float dt) {
        if (!Settings.mGrid) {
            return;
        }
        
        Camera camera = Window.getScene().camera();
        Vector2f cameraPos = camera.position;
        Vector2f projectionSize = camera.getProjectionSize();
        
        float firstX = ((int)(cameraPos.x / Settings.GRID_WIDTH)) * Settings.GRID_WIDTH;
        float firstY = ((int)(cameraPos.y / Settings.GRID_HEIGHT)) * Settings.GRID_HEIGHT;
        
        int numVtLines = (int)(projectionSize.x * camera.getZoom() / Settings.GRID_WIDTH) +2;
        int numHzLines = (int)(projectionSize.y * camera.getZoom() / Settings.GRID_HEIGHT) +2;
        
        float height = (int)(projectionSize.y * camera.getZoom()) + Settings.GRID_HEIGHT * 5;
        float width = (int)(projectionSize.x * camera.getZoom()) + Settings.GRID_WIDTH * 5;
        
        int maxLines = Math.max(numVtLines, numHzLines);
        Vector3f color = new Vector3f(0.2f,0.2f,0.2f);
        for (int i = 0; i < maxLines; i++) {
            float x = firstX + (Settings.GRID_WIDTH * i);
            float y = firstY + (Settings.GRID_HEIGHT * i);
            
            if (i < numVtLines) {
                DebugDraw.addLine2D(new Vector2f(x,firstY), new Vector2f(x,firstY + height), color);
            }
            
            if (i < numHzLines) {
                DebugDraw.addLine2D(new Vector2f(firstX,y), new Vector2f(firstX + width,y), color);
            }
        }
    }
}
