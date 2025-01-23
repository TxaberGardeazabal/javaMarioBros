/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import gameEngine.GameObject;
import gameEngine.KeyListener;
import gameEngine.Window;
import imgui.ImGui;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import physics2D.components.Box2DCollider;
import physics2D.components.CircleCollider;
import physics2D.components.PillboxCollider;
import scene.LevelEditorSceneInitializer;
import scene.LevelSceneInitializer;
import scene.MainMenuSceneInitializer;
import util.AssetPool;
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
                newFile();
            }
            
            if (ImGui.menuItem("Open", "Crtl+O")) {
                open();
            }
            
            if (ImGui.menuItem("Save", "Crtl+S")) {
                save();
            }
            
            if (ImGui.menuItem("Save as", "")) {
                saveAs();
            }
            
            if (ImGui.menuItem("Reload", "Crtl+R")) {
                reload();
            }
            
            if (ImGui.menuItem("Delete", "Crtl+shift+D")) {
                delete();
            }
            
            if (ImGui.menuItem("Exit")) {
                int res = JOptionPane.showConfirmDialog(null, "you have unsaved changes on this level, do you want to save those changes before exiting?");
                if (res != 2) {
                    if (res == 0) {
                        // selected yes
                        save();
                    }
                    
                    EventSystem.notify(null, new Event(EventType.EndWindow));
                    File file = new File(Settings.mainMenuLevel);
                    Window.changeScene(new MainMenuSceneInitializer(), file.getAbsolutePath());
                }
            }
            
            ImGui.endMenu();
        }
        
        // key shortcuts
        if (KeyListener.isKeyPressed(Settings.LEFT_CONTROL) &&
                KeyListener.keyBeginPress(Settings.EDITOR_NEW_LEVEL))
            newFile();
        else if (KeyListener.isKeyPressed(Settings.LEFT_CONTROL) &&
                KeyListener.keyBeginPress(Settings.EDITOR_OPEN_LEVEL))
            open();
        else if (KeyListener.isKeyPressed(Settings.LEFT_CONTROL) &&
                KeyListener.keyBeginPress(Settings.EDITOR_SAVE_LEVEL))
            save();
        else if (KeyListener.isKeyPressed(Settings.LEFT_CONTROL) &&
                KeyListener.keyBeginPress(Settings.EDITOR_RELOAD_LEVEL))
            reload();
        else if (KeyListener.isKeyPressed(Settings.LEFT_CONTROL) &&
                KeyListener.isKeyPressed(Settings.LEFT_SHIFT) &&
                KeyListener.keyBeginPress(Settings.EDITOR_DELETE_LEVEL))
            delete();

        if (ImGui.beginMenu("Debug")) {
            
            if (ImGui.menuItem("Show grid", null, Settings.mGrid)) {
                Settings.mGrid = !Settings.mGrid; 
            }
            if (ImGui.beginMenu("Show collision boundaries")) {
                if (ImGui.menuItem("All", null, Settings.mBorders)) {
                    Settings.mBorders = !Settings.mBorders;
                    Settings.mColisionBorders = Settings.mBorders;
                    Settings.mPipeBorders = Settings.mBorders;
                    if (Settings.mColisionBorders) {
                        showAllPhysicsCollisions();
                    } else {
                        disableAllPhysicsCollisions();
                    }
                    
                }
                if (ImGui.menuItem("Body collisions", null, Settings.mColisionBorders)) {
                    Settings.mColisionBorders = !Settings.mColisionBorders;
                    if (Settings.mColisionBorders) {
                        showAllPhysicsCollisions();
                    } else {
                        disableAllPhysicsCollisions();
                    }
                }
                if (ImGui.menuItem("Pipe boundaries", null, Settings.mPipeBorders)) {
                    Settings.mPipeBorders = !Settings.mPipeBorders; 
                }
                
                ImGui.endMenu();
            }
            if (ImGui.beginMenu("Show raycasts")) {
                if (ImGui.menuItem("All", null, Settings.mRaycasts)) {
                    Settings.mRaycasts = !Settings.mRaycasts;
                    Settings.mGroundRaycast = Settings.mRaycasts;
                    Settings.mPitRaycast = Settings.mRaycasts;
                    Settings.mOtherRaycast = Settings.mRaycasts;
                }
                if (ImGui.menuItem("Ground raycasts", null, Settings.mGroundRaycast)) {
                    Settings.mGroundRaycast = !Settings.mGroundRaycast; 
                }
                if (ImGui.menuItem("Pit Level", null, Settings.mPitRaycast)) {
                    Settings.mPitRaycast = !Settings.mPitRaycast; 
                }
                if (ImGui.menuItem("Other raycasts", null, Settings.mOtherRaycast)) {
                    Settings.mOtherRaycast = !Settings.mOtherRaycast;
                }
                
                ImGui.endMenu();
            }
            if (ImGui.menuItem("Console window", null, Settings.mConsole)) {
                Settings.mConsole = !Settings.mConsole;
                if (Settings.mConsole) {
                    Window.getImGuiLayer().startConsoleWindow();
                } else {
                    Window.getImGuiLayer().endConsoleWindow();
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
    
    private void newFile() {
        int res = JOptionPane.showConfirmDialog(null, "you have unsaved changes on this level, do you want to save those changes before exiting?");

        // 2 is cancel
        if (res != 2) {
            if (res == 0) {
                // selected yes
                save();
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
                        ConsoleWindow.addLog("Something went wrong:\n"+ex.getMessage(), ConsoleWindow.LogCategory.error);
                        Logger.getLogger(MenuBar.class.getName()).log(Level.SEVERE, null, ex);
                    }                            
                } else {
                    if (Settings.isValidFile(fileChooser.getSelectedFile().getAbsolutePath(),"txt")) {
                        int res2 = JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite this file?", "New level", JOptionPane.OK_CANCEL_OPTION);
                        if (res2 == 0) {
                            AssetPool.stopAllSounds();
                            Window.changeScene(new LevelEditorSceneInitializer(), file.getAbsolutePath());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "The selected file format is incorrect", "New level", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
    }
    
    private void open() {
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
                        save();
                    }

                    AssetPool.stopAllSounds();
                    Window.changeScene(new LevelEditorSceneInitializer(), file.getAbsolutePath());
                }

            } else {
                JOptionPane.showMessageDialog(null, "The selected file format is incorrect", "Open level", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    private void save() {
        String absolutePath = Window.getScene().getLevelFilepath();
        File testFile = new File(Settings.defaultLevel);
        if (absolutePath.equals(testFile.getAbsolutePath())) {
            // prevents the player from overriding the default level
            saveAs();
            return;
        }
        
        File file = new File(absolutePath);
        if (!file.exists()) {
            saveAs();
        } else {
            EventSystem.notify(null, new Event(EventType.SaveLevel));
        }
    }
    
    public void saveAs() {
        JFileChooser fileChooser = this.configSaveWindow();
        int response = fileChooser.showSaveDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            String path = fileChooser.getSelectedFile().getAbsolutePath();

            if (!path.contains(".txt")) {
                path = path.concat(".txt");
            }

            File testFile = new File(Settings.defaultLevel);
            if (path.equals(testFile.getAbsolutePath())) {
                // prevents the player from overriding the default level
                JOptionPane.showMessageDialog(null, "please save the level on a different file instead of the default one", "Save As", JOptionPane.WARNING_MESSAGE);
                ConsoleWindow.addLog("ERROR: couldn't create file "+path, ConsoleWindow.LogCategory.error);
                return;
            }
            
            File file = new File(path);
            if (!file.exists()) {
                try {
                    // create a file
                    boolean fileCreated = file.createNewFile();
                    if (fileCreated) {
                        Window.getScene().changeLevelFilepath(path);
                        EventSystem.notify(null, new Event(EventType.SaveLevel));
                        Window.changeScene(new LevelEditorSceneInitializer(), path);
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
    
    private void reload() {
        AssetPool.stopAllSounds();
        EventSystem.notify(null,new Event(EventType.LoadLevel));
    }
    
    private void delete() {
        int res2 = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this level forever?\nthat's a long time", "Delete level", JOptionPane.OK_CANCEL_OPTION);
        if (res2 == 0) {
            String levelFilepath = Window.getScene().getLevelFilepath();
            File file = new File(levelFilepath);
            file.delete();
            File file2 = new File(Settings.mainMenuLevel);
            AssetPool.stopAllSounds();
            Window.changeScene(new LevelSceneInitializer(), file2.getAbsolutePath());
        }
    }
    
    private void showAllPhysicsCollisions() {
        List<GameObject> GOs = Window.getScene().getGameObjects();
        
        for (GameObject go : GOs) {
            if (go.getComponent(Box2DCollider.class) != null) {
                go.getComponent(Box2DCollider.class).showBoundaries = true;
            }
            if (go.getComponent(CircleCollider.class) != null) {
                go.getComponent(CircleCollider.class).showBoundaries = true;
            }
            if (go.getComponent(PillboxCollider.class) != null) {
                go.getComponent(PillboxCollider.class).showBoundaries = true;
            }
        }
    }
    
    private void disableAllPhysicsCollisions() {
        List<GameObject> GOs = Window.getScene().getGameObjects();
        
        for (GameObject go : GOs) {
            if (go.getComponent(Box2DCollider.class) != null) {
                go.getComponent(Box2DCollider.class).showBoundaries = false;
            }
            if (go.getComponent(CircleCollider.class) != null) {
                go.getComponent(CircleCollider.class).showBoundaries = false;
            }
            if (go.getComponent(PillboxCollider.class) != null) {
                go.getComponent(PillboxCollider.class).showBoundaries = false;
            }
        }
    }
}
