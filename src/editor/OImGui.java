/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import components.Sprite;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImString;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.Settings;

/**
 * Clase con funciones imgui para mantener un aspecto consistente entre todas las ventanas
 * @author txaber gardeazabal
 */
public class OImGui {
    
    private static float defColumnWidht = 220.0f;
    /**
     * Muestra un vector de 2 filas
     * @param label texto label
     * @param values valor a mostrar
     * @return el nuevo valor editado en ventana
     */
    public static Vector2f drawVec2Control(String label, Vector2f values) {
        return drawVec2Control(label, values, 0.0f, defColumnWidht);
    }
    /**
     * Muestra un vector de 2 filas
     * @param label texto label
     * @param values valor a mostrar
     * @param resetValue valor por defecto para resetear
     * @return el nuevo valor editado en ventana
     */
    public static Vector2f drawVec2Control(String label, Vector2f values, float resetValue) {
        return drawVec2Control(label, values, resetValue, defColumnWidht);
    }
    /**
     * Muestra un vector de 2 filas
     * @param label texto label
     * @param values valor a mostrar
     * @param resetValue valor por defecto para resetear
     * @param columnWidht anchura de las columnas
     * @return el nuevo valor editado en ventana
     */
    public static Vector2f drawVec2Control(String label, Vector2f values, float resetValue, float columnWidht) {
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
        
        return values;
    }
    
    /**
     * Muestra un valor float
     * @param label texto label
     * @param value valor a mostrar
     * @return el nuevo valor editado en ventana
     */
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
    
    /**
     * Muestra un valor integer
     * @param label texto label
     * @param value valor a mostrar
     * @return el nuevo valor editado en ventana
     */
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
    
    /**
     * Muestra un valor RGBA con un selector en pantalla
     * @param label texto label
     * @param color valor RGBA a mostrar
     * @return el nuevo valor editado en ventana
     */
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

    /**
     * Muestra un valor de texto string
     * @param label texto label
     * @param text texto a mostrar
     * @return el nuevo valor editado en ventana
     */
    public static String inputText(String label, String text) {
        ImGui.pushID(label);
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, defColumnWidht);
        ImGui.text(label);
        ImGui.nextColumn();
        
        ImString outString = new ImString(text, 256);
        if (ImGui.inputText("##text" + label, outString)) {
            ImGui.columns();
            ImGui.popID();
            
            return outString.get();
        }
        
        ImGui.columns(1);
        ImGui.popID();
        
        return text;
    }
    
    /**
     * Muestra un valor booleano
     * @param label texto label
     * @param bool valor a mostrar
     * @return el nuevo valor editado en ventana
     */
    public static boolean inputBoolean(String label, boolean bool) {
        ImGui.pushID(label);
        
        ImGui.columns(2);
        ImGui.setColumnWidth(0, defColumnWidht);
        ImGui.text(label);
        ImGui.nextColumn();
        
        if (ImGui.checkbox("##" + label, bool)) {
            ImGui.columns(1);
            ImGui.popID();
            return !bool;
        }
        
        ImGui.columns(1);
        ImGui.popID();
        return bool;
    }
    
    /**
     * Crea un boton imgui simple con imagen.
     * @param sprite sprite del boton
     * @param bId id unico para el boton
     * @return true si el boton esta siendo pulsado, false de lo contrario.
     */
    public static boolean spriteButton(Sprite sprite, int bId) {
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowContentRegionMax(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);
        float windowX2 = windowPos.x + windowSize.x;
                
        int texId = sprite.getTexId();
        Vector2f[] texCoords = sprite.getTexCoords();

        ImGui.pushID(bId);
        boolean ret = ImGui.imageButton(texId, Settings.BUTTON_SIZE, Settings.BUTTON_SIZE,texCoords[2].x,texCoords[0].y,texCoords[0].x,texCoords[2].y);
        ImGui.popID();
        
        ImVec2 lastButtonPos = new ImVec2();
        ImGui.getItemRectMax(lastButtonPos);
        float lastButtonX2 = lastButtonPos.x;
        float nextButtonX2 = lastButtonX2 + itemSpacing.x + Settings.BUTTON_SIZE;
        if (nextButtonX2 < windowX2) {
            ImGui.sameLine();
        }
        return ret;
    }
}
