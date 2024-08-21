/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import gameEngine.Window;
import imgui.ImGui;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import scene.LevelEditorSceneInitializer;
import scene.LevelSceneInitializer;
import scene.MainMenuSceneInitializer;
import util.Settings;

/**
 * Controlador de la barra de menu del editor.
 * Contiene las funcionalidades y herramientas principales de la barra de menu
 * @author txaber gardeazabal
 */
public class MenuBar {
    
    /**
     * Ejecuta codigo imgui para mostrar y actualizar la ventana
     */
    public void imGui() {
        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("File")) {
            
            if (ImGui.menuItem("New", "Crtl+N")) {
                int res = JOptionPane.showConfirmDialog(null, "you have unsaved changes on this level, do you want to save those changes before exiting?");
                
                // 2 is cancel
                if (res != 2) {
                    if (res == 0) {
                        // selected yes
                        EventSystem.notify(null, new Event(EventType.SaveLevel));
                    }
                    JFileChooser fileChooser = this.configSaveWindow();
                    int response = fileChooser.showSaveDialog(null);
                    
                    if (response == JFileChooser.APPROVE_OPTION) {
                        String path = fileChooser.getSelectedFile().getAbsolutePath();
                        
                        if (!path.contains(".txt")) {
                            path = path.concat(".txt");
                        }
                        
                        File file = new File(path);
                        
                        if (!file.exists()) {
                            try {
                                // create a file
                                boolean fileCreated = file.createNewFile();
                                if (fileCreated) {
                                    Window.changeScene(new LevelEditorSceneInitializer(), file.getAbsolutePath());
                                } else {
                                    JOptionPane.showMessageDialog(null, "ERROR: couldn't create file "+file.getAbsolutePath());
                                }
                                
                            } catch (IOException ex) {
                                Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                            }                            
                        } else {
                            if (Settings.isValidFile(fileChooser.getSelectedFile().getAbsolutePath(),"txt")) {
                                int res2 = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite this file?", "New level", JOptionPane.OK_CANCEL_OPTION);
                                if (res2 == 0) {
                                    Window.changeScene(new LevelEditorSceneInitializer(), file.getAbsolutePath());
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "The selected file format is incorrect", "New level", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                }
            }
            
            if (ImGui.menuItem("Open", "Crtl+O")) {
                JFileChooser fileChooser = this.configSaveWindow();
                int response = fileChooser.showOpenDialog(null);
                
                if (response == JFileChooser.APPROVE_OPTION) {
                    File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                    
                    if (Settings.isValidFile(file.getAbsolutePath(),"txt")) {
                        int res = JOptionPane.showConfirmDialog(null, "you have unsaved changes on this level, do you want to save those changes before exiting?");
                
                        // 2 is cancel
                        if (res != 2) {
                            if (res == 0) {
                                // selected yes
                                EventSystem.notify(null, new Event(EventType.SaveLevel));
                            }
                            
                            Window.changeScene(new LevelEditorSceneInitializer(), file.getAbsolutePath());
                        }
                        
                    } else {
                        JOptionPane.showMessageDialog(null, "The selected file format is incorrect", "Open level", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            
            if (ImGui.menuItem("Save", "Crtl+S")) {
                File file = new File(Window.getScene().getLevelFilepath());
                if (!file.exists()) {
                    JFileChooser fileChooser = this.configSaveWindow();
                    int response = fileChooser.showSaveDialog(null);

                    if (response == JFileChooser.APPROVE_OPTION) {
                        String path = fileChooser.getSelectedFile().getAbsolutePath();

                        if (!path.contains(".txt")) {
                            path = path.concat(".txt");
                        }

                        file = new File(path);

                        if (!file.exists()) {
                            try {
                                // create a file
                                boolean fileCreated = file.createNewFile();
                                if (fileCreated) {
                                    Window.getScene().changeLevelFilepath(fileChooser.getSelectedFile().getAbsolutePath());
                                    EventSystem.notify(null, new Event(EventType.SaveLevel));
                                } else {
                                    JOptionPane.showMessageDialog(null, "ERROR: couldn't create file "+file.getAbsolutePath());
                                }

                            } catch (IOException ex) {
                                Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                            }                            
                        } else {
                            if (Settings.isValidFile(fileChooser.getSelectedFile().getAbsolutePath(),"txt")) {
                                int res2 = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite this file?", "Save level", JOptionPane.OK_CANCEL_OPTION);
                                if (res2 == 0) {
                                    Window.getScene().changeLevelFilepath(fileChooser.getSelectedFile().getAbsolutePath());
                                    EventSystem.notify(null, new Event(EventType.SaveLevel));
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "The selected file format is incorrect", "Save level", JOptionPane.WARNING_MESSAGE);
                            }
                        }
                    }
                } else {
                    EventSystem.notify(null, new Event(EventType.SaveLevel));
                }
            }
            
            if (ImGui.menuItem("Save as", "")) {
                
                JFileChooser fileChooser = this.configSaveWindow();
                int response = fileChooser.showSaveDialog(null);
                
                if (response == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();

                    if (!path.contains(".txt")) {
                        path = path.concat(".txt");
                    }

                    File file = new File(path);

                    if (!file.exists()) {
                        try {
                            // create a file
                            boolean fileCreated = file.createNewFile();
                            if (fileCreated) {
                                Window.getScene().changeLevelFilepath(fileChooser.getSelectedFile().getAbsolutePath());
                                EventSystem.notify(null, new Event(EventType.SaveLevel));
                            } else {
                                JOptionPane.showMessageDialog(null, "ERROR: couldn't create file "+file.getAbsolutePath());
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                        }                            
                    } else {
                        if (Settings.isValidFile(fileChooser.getSelectedFile().getAbsolutePath(),"txt")) {
                            int res2 = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite this file?", "Save level", JOptionPane.OK_CANCEL_OPTION);
                            if (res2 == 0) {
                                Window.getScene().changeLevelFilepath(fileChooser.getSelectedFile().getAbsolutePath());
                                EventSystem.notify(null, new Event(EventType.SaveLevel));
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "The selected file format is incorrect", "Save level", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
            
            if (ImGui.menuItem("Reload", "")) {
                EventSystem.notify(null,new Event(EventType.LoadLevel));
            }
            
            if (ImGui.menuItem("Delete", "Crtl+shift+D")) {
                int res2 = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this level forever?\nthat's a long time", "Delete level", JOptionPane.OK_CANCEL_OPTION);
                if (res2 == 0) {
                    String levelFilepath = Window.getScene().getLevelFilepath();
                    File file = new File(levelFilepath);
                    file.delete();
                    Window.changeScene(new LevelSceneInitializer(), "");
                }
                
            }
            
            if (ImGui.menuItem("Exit")) {
                int res = JOptionPane.showConfirmDialog(null, "you have unsaved changes on this level, do you want to save those changes before exiting?");
                if (res != 2) {
                    if (res == 0) {
                        // selected yes
                        EventSystem.notify(null, new Event(EventType.SaveLevel));
                    }
                    
                    EventSystem.notify(null, new Event(EventType.EndWindow));
                    File file = new File("assets/levels/mainmenu.txt");
                    Window.changeScene(new MainMenuSceneInitializer(), file.getAbsolutePath());
                }
                
            }
        
            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();
    }
    
    /**
     * Muestra una ventana para seleccionar niveles
     * @return la ventana creada
     */
    private JFileChooser configSaveWindow() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("assets/levels"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setFileFilter(new FileNameExtensionFilter("level files (.txt)","txt"));
        return fileChooser;
    }
}
