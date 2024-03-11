/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector4f;

/**
 *
 * @author txaber gardeazabal
 */
public class OImGui {
    
    private static float defColumnWidht = 220.0f;
    public static void drawVec2Control(String label, Vector2f values) {
        drawVec2Control(label, values, 0.0f, defColumnWidht);
    }
    public static void drawVec2Control(String label, Vector2f values, float resetValue) {
        drawVec2Control(label, values, resetValue, defColumnWidht);
    }
    public static void drawVec2Control(String label, Vector2f values, float resetValue, float columnWidht) {
        ImGui.pushID(label);
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, columnWidht);
        ImGui.text(label);
        ImGui.nextColumn();
        
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 0);
        float lineHeigth = ImGui.getFontSize() + ImGui.getStyle().getFramePaddingY() * 2.0f;
        Vector2f buttonSize = new Vector2f(lineHeigth + 2.0f, lineHeigth);
        float widhtEach = (ImGui.calcItemWidth() - buttonSize.x * 2.0f) / 2.0f;
        
        ImGui.pushItemWidth(widhtEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.8f, 0.1f, 0.15f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.9f, 0.2f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.8f, 0.1f, 0.15f, 1.0f);
        if (ImGui.button("X", buttonSize.x, buttonSize.y)) {
            values.x = resetValue;
        }
        ImGui.popStyleColor(3);
        
        ImGui.sameLine();
        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##x", vecValuesX, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();
        
        ImGui.pushItemWidth(widhtEach);
        ImGui.pushStyleColor(ImGuiCol.Button, 0.2f, 0.7f, 0.2f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.3f, 0.8f, 0.3f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.2f, 0.7f, 0.2f, 1.0f);
        if (ImGui.button("Y", buttonSize.x, buttonSize.y)) {
            values.y = resetValue;
        }
        ImGui.popStyleColor(3);
        
        ImGui.sameLine();
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##y", vecValuesY, 0.1f);
        ImGui.popItemWidth();
        ImGui.sameLine();
        
        ImGui.nextColumn();
        
        // set the values
        values.x = vecValuesX[0];
        values.y = vecValuesY[0];
        
        ImGui.popStyleVar();
        ImGui.columns(1);
        ImGui.popID();
    }
    
    public static float dragFloat(String label, float value) {
        ImGui.pushID(label);
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, defColumnWidht);
        ImGui.text(label);
        ImGui.nextColumn();
        
        float[] valArr = {value};
        ImGui.dragFloat("##dragFloat", valArr, 0.1f);
        
        ImGui.columns(1);
        ImGui.popID();
        
        return valArr[0];
    }
    
    public static int dragInt(String label, int value) {
        ImGui.pushID(label);
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, defColumnWidht);
        ImGui.text(label);
        ImGui.nextColumn();
        
        int[] valArr = {value};
        ImGui.dragInt("##dragFloat", valArr, 0.1f);
        
        ImGui.columns(1);
        ImGui.popID();
        
        return valArr[0];
    }
    
    public static boolean colorPicker4(String label, Vector4f color) {
        boolean res = false;
        ImGui.pushID(label);
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, defColumnWidht);
        ImGui.text(label);
        ImGui.nextColumn();
        
        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorEdit4("##colorPicker", imColor)) {
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            res = true;
        }
        
        ImGui.columns(1);
        ImGui.popID();
        
        return res;
    }

    public static String inputText(String label, String text) {
        ImGui.pushID(label);
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, defColumnWidht);
        ImGui.text(label);
        ImGui.nextColumn();
        
        ImString outString = new ImString(text, 256);
        if (ImGui.inputText("##" + label, outString)) {
            ImGui.columns();
            ImGui.popID();
            
            return outString.get();
        }
        
        ImGui.columns(1);
        ImGui.popID();
        
        return text;
    }
}
