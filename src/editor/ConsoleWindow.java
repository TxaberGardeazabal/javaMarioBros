/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import imgui.ImGui;
import imgui.ImGuiListClipper;
import imgui.ImGuiTextFilter;
import imgui.callback.ImListClipperCallback;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import org.joml.Vector2i;

/**
 * Controlador de ventana de consola.
 * La ventana de consola muestra informacion interna y mensajes internos a tiempo real.
 * @author txaber gardeazabal
 */
public class ConsoleWindow {
    
    private static ArrayList<String> buf = new ArrayList();
    private ImGuiTextFilter filter;
    private Vector2i lineOffsets;
    private ImBoolean autoScroll;

    public ConsoleWindow() {
        autoScroll = new ImBoolean(true);
        filter = new ImGuiTextFilter();
        lineOffsets = new Vector2i();
    }
    
    /**
     * Limpia todos los mensajes anteriores
     */
    public void clear() {
        buf.clear();
        lineOffsets.zero();
    }
    
    /**
     * Añade un mensaje a ser mostrado en la pantalla de la ventana de consola
     * @param line el mensaje a mostrar
     * @param cat la categoria informativa del mensaje
     */
    public static void addLog(String line, LogCategory cat) {
        // todo el formatting aqui
        String log = "";
        
        ZonedDateTime time = ZonedDateTime.now();
        
        log += time.getHour()+":"+time.getMinute()+":"+time.getSecond();
        log += " ["+cat+"]";
        log += " "+line;
        
        // IDE debugging, remove this line in builds
        System.out.println(log);
        buf.add(log);
    }
    
    /**
     * Ejecuta codigo imgui para mostrar y actualizar la ventana
     */
    public void imGui() {
        
        if (!ImGui.begin("logs"))
        {
            ImGui.end();
            return;
        }
        
        // Options menu
        if (ImGui.beginPopup("Options"))
        {
            ImGui.checkbox("Auto-scroll", autoScroll);
            ImGui.endPopup();
        }
        
        if (ImGui.button("Options"))
            ImGui.openPopup("Options");
        ImGui.sameLine();
        boolean clear = ImGui.button("Clear");
        ImGui.sameLine();
        boolean copy = ImGui.button("Copy");
        ImGui.sameLine();
        filter.draw("Filter", -100.0f);
        
        ImGui.separator();
        
        if (ImGui.beginChild("scrolling", 0, 0, false, ImGuiWindowFlags.HorizontalScrollbar)) {
            
            if (clear)
                clear();
            if (copy)
                ImGui.logToClipboard();
            
            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0,0); 
            
            if (filter.isActive()) {
                for (int i = 0; i < buf.size(); i++) {
                    if (filter.passFilter(buf.get(i))) {
                        ImGui.textUnformatted(i + " " + buf.get(i));
                    }
                }
            } else {
                ImGuiListClipper.forEach(buf.size(), new ImListClipperCallback(){
                    @Override
                    public void accept(int index) {
                        //ImGui.text(String.format("line number %d", index));
                        
                        ImGui.textUnformatted(index + " " + buf.get(index));
                    }
                });
            }
            
            ImGui.popStyleVar();
            
            if (autoScroll.get() && ImGui.getScrollY() >= ImGui.getScrollMaxY())
                ImGui.setScrollHereY(1.0f);
        }
        ImGui.endChild();
        ImGui.end();
    }
    
    /**
     * Las diferentes categorias informativas de los mensajes
     */
    public enum LogCategory {
        info,
        warning,
        error
    }
}
