/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import gameEngine.GameObject;
import components.MouseControls;
import components.Sprite;
import components.SpriteSheet;
import gameEngine.Prefab;
import gameEngine.PrefabSave;
import gameEngine.Sound;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImInt;
import java.io.File;
import java.util.Collection;
import org.joml.Vector2f;
import util.AssetPool;
import util.Settings;

/**
 * Controlador de ventana de assets del editor.
 * La ventana de assets muestra todas las piezas para poder usar en el juego, entre otras cosas bloques, sonidos, prefabs, componentes del UI, entidades fisicas y mas.
 * @author txaber gardeazabal
 */
public class AssetWindow {
    
    private MouseControls mc;
    private boolean isGround = true;
    private int zIndex;
    
    public AssetWindow(MouseControls mouseControls) {
        this.mc = mouseControls;
    }
    
    /**
     * Ejecuta codigo imgui para mostrar y actualizar la ventana
     */
    public void imGui(){
        ImGui.begin("Objects");
        SpriteSheet sprites;
        String relativePath;
        
        
        if (ImGui.beginTabBar("WindowTabBar")) {
            if (ImGui.beginTabItem("Solid blocks")) {
                
                
                ImGui.columns(2);
                ImGui.setColumnWidth(-1, 100);
                boolean b = isGround;
                if (ImGui.checkbox("ground: ", b)) {
                    isGround = !b;
                }
                
                ImGui.text("z-index:");
                ImInt val = new ImInt(zIndex);
                if (ImGui.inputInt(" ", val)) {
                    zIndex = val.get();
                }
                
                ImGui.nextColumn();
                
                File blocksDir = new File("assets/images/spriteSheets/blocksAndScenery");
                File[] blocks = blocksDir.listFiles();
                for (File spriteSheet : blocks) {
                    //System.out.println(spriteSheet.getAbsolutePath());
                    relativePath = Settings.getRelativePath(spriteSheet.getAbsolutePath());
                    //System.out.println(relativePath);
                    sprites = AssetPool.getSpritesheet(relativePath);
                    
                    ImGui.text(relativePath);
                    
                    for (int i = 0; i < sprites.size(); i++) {
                        Sprite sprite = sprites.getSprite(i);
                        
                        if (spriteButton(sprite,i)) {
                            GameObject object;
                            if (isGround) {
                                object = Prefab.generateGroundObject(sprite, Settings.BLOCK_WIDTH, Settings.BLOCK_HEIGHT);
                            } else {
                                object = Prefab.generateSpriteObject(sprite, Settings.BLOCK_WIDTH, Settings.BLOCK_HEIGHT);
                            }
                            object.transform.zIndex = zIndex;
                            
                            mc.pickUpObject(object);
                        }
                        
                        if (i + 1 == sprites.size()) {
                            ImGui.newLine();
                        }
                    }
                }
                
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Special blocks")) {
                int uid = 0;
                
                PrefabSave pre = new PrefabSave("");
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png");
                
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeUp.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(1),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeLeft.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(2),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeDown.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(3),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeRight.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
                
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/itemBlock.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(3),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/invisibleItemBlock.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png");
                
                if (spriteButton(sprites.getSprite(1),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/brickItemBlock1.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(2),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/brickItemBlock2.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Entities")) {
                int uid = 0;
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png");
                PrefabSave pre = new PrefabSave("");
                
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/mario.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
                
                if (spriteButton(sprites.getSprite(4),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/levelCoin.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png");
                
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/goomba.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png");
                
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/greenKoopa.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
                
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/mushroom.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(1),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/fireFlower.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(6),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/star.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (spriteButton(sprites.getSprite(9),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/oneUpMushroom.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("assembled structures")) {
                int uid = 0;
                PrefabSave pre = new PrefabSave("");
                
                
                File StructureDir = new File("assets/prefabs/structures");
                File[] blocks = StructureDir.listFiles();
                for (File file : blocks) {
                    pre.setFilepath(file.getAbsolutePath());
                    
                    if (ImGui.button(Settings.getRelativePath(file.getAbsolutePath()))) {
                        GameObject go = pre.load();
                        if (go != null) {
                            mc.pickUpObject(go);
                        }                    
                        
                    }
                    
                }
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Sounds")) {
                Collection<Sound> sounds = AssetPool.getAllSounds();
                for (Sound sound : sounds) {
                    File tmp = new File(sound.getFilepath());
                    if (ImGui.button(tmp.getName())) {
                        if (!sound.isPlaying()) {
                            sound.play();
                        }
                        else {
                            sound.stop();
                        }
                    }
                    
                    /*if (ImGui.getContentRegionAvailX() > 100) {
                        ImGui.sameLine();
                    }*/
                }
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("UI")) {
                ImGui.text("elementos de la interfaz de usuario");
                int uid = 0;
                
                // TODO: make  theese into prefabs
                Sprite text = new Sprite(AssetPool.getTexture("assets/images/text/mario.png"));
                // mario
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                // mundo
                text = new Sprite(AssetPool.getTexture("assets/images/text/mundo.png"));
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                // tiempo
                text = new Sprite(AssetPool.getTexture("assets/images/text/tiempo.png"));
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                // empezar
                text = new Sprite(AssetPool.getTexture("assets/images/text/empezar.png"));
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateStartButton(text);
                    mc.pickUpObject(object);
                }
                
                // salir
                text = new Sprite(AssetPool.getTexture("assets/images/text/salir.png"));
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateExitButton(text);
                    mc.pickUpObject(object);
                }
                
                // editor
                text = new Sprite(AssetPool.getTexture("assets/images/text/editor.png"));
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateEditorButton(text);
                    mc.pickUpObject(object);
                }
                
                // seleccionar
                text = new Sprite(AssetPool.getTexture("assets/images/text/seleccionar_nivel.png"));
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateSelectButton(text);
                    mc.pickUpObject(object);
                }
                
                // digitalizer
                text = new Sprite(AssetPool.getTexture("assets/images/text/Super_Mario_Bros._Logo.png"));
                if (spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                SpriteSheet text2 = AssetPool.getSpritesheet("assets/images/text/fontFace.png");
                if (spriteButton(text2.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateDigitalizer(6);
                    mc.pickUpObject(object);
                }
                
                for (int i = 0; i < text2.size(); i++) {
                    if (spriteButton(text2.getSprite(i), uid++)) {
                        GameObject object = Prefab.generateUIText(text2.getSprite(i));
                        mc.pickUpObject(object);
                    }
                }
                
                SpriteSheet extra = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/UIExtra.png");
                if (spriteButton(extra.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateUIText(extra.getSprite(0));
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(extra.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateUICoin();
                    mc.pickUpObject(object);
                }
                
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Experimental")) {
                ImGui.text("objetos experimentales creados para probar funcionalidades, NO USAR pueden corromper tus niveles");
                int uid = 0;
                SpriteSheet pipeSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png");

                // old pipe w inheritance
                if (spriteButton(pipeSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generatePipeExp();
                    mc.pickUpObject(object);
                }
                
                // fireball sprite for render test
                Sprite fire = new Sprite(AssetPool.getTexture("assets/images/spriteSheets/marioFireball.png"));
                if (spriteButton(fire,uid++)) {
                    GameObject object = Prefab.generateUIText(fire);
                    mc.pickUpObject(object);
                }
                
                // empty object
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/UIExtra.png");
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateEmptyObject();
                    mc.pickUpObject(object);
                }
                
                // coin inside of blocks
                fire = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/blockCoin.png").getSprite(0);
                if (spriteButton(fire,uid++)) {
                    GameObject object = Prefab.generateBlockCoin();
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png");
                
                if (spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateMario();
                    mc.pickUpObject(object);
                    
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioParticles.png");
                if (spriteButton(sprites.getSprite(12),uid++)) {
                    GameObject object = Prefab.generateFlag();
                    mc.pickUpObject(object);
                }
                
                /*sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
                if (spriteButton(sprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateItemBlock(sprites);
                    mc.pickUpObject(object);
                }*/
                
                ImGui.separator();
                
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
    
    /**
     * Crea un boton imgui simple con imagen.
     * @param sprite sprite del boton
     * @param bId id unico para el boton
     * @return true si el boton esta siendo pulsado, false de lo contrario.
     */
    private boolean spriteButton(Sprite sprite, int bId) {
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
