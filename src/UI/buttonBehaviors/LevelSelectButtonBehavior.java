/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI.buttonBehaviors;

import UI.ButtonBehavior;
import gameEngine.Window;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import scene.LevelEditorSceneInitializer;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class LevelSelectButtonBehavior implements ButtonBehavior {

    
    @Override
    public void onClick() {
        Map a = new HashMap<>();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("assets/levels"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("level files (.txt)","txt"));

        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

            if (Settings.isValidFile(file.getAbsolutePath(),"txt")) {
                
                a.put("filepath", file.getAbsolutePath());
                EventSystem.notify(null, new Event(EventType.PlayLevel, a));

            } else {
                JOptionPane.showMessageDialog(null, "The selected file format is incorrect", "Open level", JOptionPane.WARNING_MESSAGE);
            }
        }
        
    }

    @Override
    public void onHold() {
    }

    @Override
    public void onHover() {
    }
    
}
