/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import gameEngine.GameObject;
import components.MouseControls;
import components.Sprite;
import components.SpriteSheet;
import components.propertieComponents.Ground;
import gameEngine.Direction;
import gameEngine.Prefab;
import gameEngine.Sound;
import gameEngine.Window;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.type.ImInt;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.joml.Vector2f;
import scene.LevelEditorSceneInitializer;
import util.AssetPool;
import util.Settings;

/**
 *
 * @author txaber gardeazabal
 */
public class AssetWindow {
    
    private MouseControls mc;
    private boolean isGround = true;
    private int zIndex;
    
    public AssetWindow(MouseControls mouseControls) {
        this.mc = mouseControls;
    }
    
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
                SpriteSheet pipeSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png");
                
                if (spriteButton(pipeSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generatePipe(Direction.Up);
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(pipeSprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generatePipe(Direction.Left);
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(pipeSprites.getSprite(2),uid++)) {
                    GameObject object = Prefab.generatePipe(Direction.Down);
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(pipeSprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generatePipe(Direction.Right);
                    mc.pickUpObject(object);
                }
                
                SpriteSheet blockSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
                
                if (spriteButton(blockSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateItemBlock(blockSprites);
                    mc.pickUpObject(object);
                }
                
                SpriteSheet brickSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png");
                
                if (spriteButton(brickSprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(brickSprites.getSprite(1), blockSprites);
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(brickSprites.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(brickSprites.getSprite(2), blockSprites);
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(blockSprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateInvItemBlock(blockSprites);
                    mc.pickUpObject(object);
                }
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Entities")) {
                int uid = 0;
                SpriteSheet playerSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png");
                
                if (spriteButton(playerSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateMario();
                    mc.pickUpObject(object);
                }
                
                SpriteSheet coinSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocks.png");
                
                if (spriteButton(coinSprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generateCoin();
                    mc.pickUpObject(object);
                }
                
                SpriteSheet enemieSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png");
                
                
                if (spriteButton(enemieSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateGoomba();
                    mc.pickUpObject(object);
                }
                
                SpriteSheet koopaSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png");
                
                if (spriteButton(koopaSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateKoopa();
                    mc.pickUpObject(object);
                }
                
                SpriteSheet itemSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
                
                if (spriteButton(itemSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateMushroom();
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(itemSprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateFlower();
                    mc.pickUpObject(object);
                }
                
                if (spriteButton(itemSprites.getSprite(6),uid++)) {
                    GameObject object = Prefab.generateStar();
                    mc.pickUpObject(object);
                }
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("assembled structures")) {
                int uid = 0;
                SpriteSheet bushSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesAndSceneryOverworld.png");

                if (spriteButton(bushSprites.getSprite(9),uid++)) {
                    GameObject object = Prefab.generateBush1();
                    mc.pickUpObject(object);
                }
                ImGui.sameLine();
                
                SpriteSheet flagSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioParticles.png");

                if (spriteButton(flagSprites.getSprite(12),uid++)) {
                    GameObject object = Prefab.generateFlag();
                    mc.pickUpObject(object);
                }
                ImGui.sameLine();
                
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
                ImGui.text("elementos de la interfaz de usuario, no recomendado");
                int uid = 0;
                
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
                
                SpriteSheet text2 = AssetPool.getSpritesheet("assets/images/text/numeros.png");
                if (spriteButton(text2.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateDigitalizer(6);
                    mc.pickUpObject(object);
                }
                
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Experimental")) {
                ImGui.text("objetos experimentales creados para probar funcionalidades, NO USAR pueden corromper tus niveles");
                int uid = 0;
                SpriteSheet pipeSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png");

                if (spriteButton(pipeSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generatePipeExp();
                    mc.pickUpObject(object);
                }
                
                Sprite fire = new Sprite(AssetPool.getTexture("assets/images/spriteSheets/marioFireball.png"));
                
                if (spriteButton(fire,uid++)) {
                    GameObject object = Prefab.generateUIText(fire);
                    mc.pickUpObject(object);
                }
                
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
    
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
