/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package editor;

import gameEngine.GameObject;
import components.MouseControls;
import components.Sprite;
import components.SpriteSheet;
import components.gamecomponents.LevelController;
import gameEngine.Prefab;
import gameEngine.PrefabSave;
import gameEngine.Sound;
import gameEngine.Window;
import imgui.ImGui;
import imgui.type.ImInt;
import java.io.File;
import java.util.Collection;
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
                        
                        if (OImGui.spriteButton(sprite,i)) {
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
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeUp.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeLeft.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(2),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeDown.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/pipeRight.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png");
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/itemBlock.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/invisibleItemBlock.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png");
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/brickItemBlock1.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(2),uid++)) {
                    pre.setFilepath("assets/prefabs/blocks/brickItemBlock2.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    pre.setFilepath("assets/prefabs/fireRod.prefab");
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
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/mario.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png");
                
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/levelCoin.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png");
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/goomba.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png");
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/greenKoopa.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/mushroom.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/fireFlower.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(6),uid++)) {
                    pre.setFilepath("assets/prefabs/entities/star.prefab");
                    GameObject object = pre.load();
                    if (object != null) {
                        mc.pickUpObject(object);
                    }
                }
                
                if (OImGui.spriteButton(sprites.getSprite(9),uid++)) {
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
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                // mundo
                text = new Sprite(AssetPool.getTexture("assets/images/text/mundo.png"));
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                // tiempo
                text = new Sprite(AssetPool.getTexture("assets/images/text/tiempo.png"));
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                // empezar
                text = new Sprite(AssetPool.getTexture("assets/images/text/empezar.png"));
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateStartButton(text);
                    mc.pickUpObject(object);
                }
                
                // salir
                text = new Sprite(AssetPool.getTexture("assets/images/text/salir.png"));
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateExitButton(text);
                    mc.pickUpObject(object);
                }
                
                // editor
                text = new Sprite(AssetPool.getTexture("assets/images/text/editor.png"));
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateEditorButton(text);
                    mc.pickUpObject(object);
                }
                
                // seleccionar
                text = new Sprite(AssetPool.getTexture("assets/images/text/seleccionar_nivel.png"));
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateSelectButton(text);
                    mc.pickUpObject(object);
                }
                
                // digitalizer
                text = new Sprite(AssetPool.getTexture("assets/images/text/Super_Mario_Bros._Logo.png"));
                if (OImGui.spriteButton(text,uid++)) {
                    GameObject object = Prefab.generateUIText(text);
                    mc.pickUpObject(object);
                }
                
                SpriteSheet text2 = AssetPool.getSpritesheet("assets/images/text/fontFace.png");
                if (OImGui.spriteButton(text2.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateDigitalizer(6);
                    mc.pickUpObject(object);
                }
                
                for (int i = 0; i < text2.size(); i++) {
                    if (OImGui.spriteButton(text2.getSprite(i), uid++)) {
                        GameObject object = Prefab.generateUIText(text2.getSprite(i));
                        mc.pickUpObject(object);
                    }
                }
                
                SpriteSheet extra = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/UIExtra.png");
                if (OImGui.spriteButton(extra.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateUIText(extra.getSprite(0));
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(extra.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateUICoin();
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateUIText(sprites.getSprite(0));
                    mc.pickUpObject(object);
                }
                
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Legacy")) {
                int uid = 0;
                
                // empty object
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/UIExtra.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateEmptyObject();
                    mc.pickUpObject(object);
                }
                
                // mario
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/mario/marioSmall.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateMario();
                    mc.pickUpObject(object);
                    
                }
                
                // coin inside of blocks
                Sprite sprite = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/blockCoin.png").getSprite(0);
                if (OImGui.spriteButton(sprite,uid++)) {
                    GameObject object = Prefab.generateBlockCoin();
                    mc.pickUpObject(object);
                }
                
                // coin in levels
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generateCoin(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksUnderground.png");
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generateCoin(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksCastle.png");
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generateCoin(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksUnderwater.png");
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generateCoin(sprites);
                    mc.pickUpObject(object);
                }
                
                // item blocks
                // overworld
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateInvItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                SpriteSheet sprites2 = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png");
                if (OImGui.spriteButton(sprites2.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(1),sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites2.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(2),sprites);
                    mc.pickUpObject(object);
                }
                
                // underground
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksUnderground.png");
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateInvItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites2 = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderground.png");
                if (OImGui.spriteButton(sprites2.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(1),sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites2.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(2),sprites);
                    mc.pickUpObject(object);
                }
                
                // castle
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksCastle.png");
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateInvItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites2 = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundCastle.png");
                if (OImGui.spriteButton(sprites2.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(1),sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites2.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(2),sprites);
                    mc.pickUpObject(object);
                }
                
                // underwater
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksUnderwater.png");
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateInvItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateItemBlock(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites2 = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderwater.png");
                if (OImGui.spriteButton(sprites2.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(1),sprites);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites2.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateItemBrickBlock(sprites2.getSprite(2),sprites);
                    mc.pickUpObject(object);
                }
                
                // breakable brick blocks
                // overworld
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(2), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(1), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                // underground
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderground.png");
                if (OImGui.spriteButton(sprites.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(2), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(1), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                // castle
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundCastle.png");
                if (OImGui.spriteButton(sprites.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(2), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(1), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                // underwater
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/groundUnderwater.png");
                if (OImGui.spriteButton(sprites.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(2), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateBreakableObject(sprites.getSprite(1), 0.25f, 0.25f);
                    mc.pickUpObject(object);
                }
                
                // flagpole
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioParticles.png");
                if (OImGui.spriteButton(sprites.getSprite(12),uid++)) {
                    GameObject object = Prefab.generateFlag();
                    mc.pickUpObject(object);
                }

                // level controller
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/UIExtra.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    if (Window.getScene().getGameObjectWith(LevelController.class) == null) {
                        GameObject object = Prefab.generateLevelController();
                        Window.getScene().addGameObjectToScene(object);
                        //object.start();
                    }
                }
                
                // powerups
                // mushroom
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/marioPowerups.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateMushroom(0);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(18),uid++)) {
                    GameObject object = Prefab.generateMushroom(18);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(27),uid++)) {
                    GameObject object = Prefab.generateMushroom(27);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(9),uid++)) {
                    GameObject object = Prefab.generateLiveMushroom();
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateFlower(0);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(10),uid++)) {
                    GameObject object = Prefab.generateFlower(9);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(19),uid++)) {
                    GameObject object = Prefab.generateFlower(18);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(28),uid++)) {
                    GameObject object = Prefab.generateFlower(27);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(6),uid++)) {
                    GameObject object = Prefab.generateStar(0);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(17),uid++)) {
                    GameObject object = Prefab.generateStar(9);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(24),uid++)) {
                    GameObject object = Prefab.generateStar(18);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(33),uid++)) {
                    GameObject object = Prefab.generateStar(27);
                    mc.pickUpObject(object);
                }
                
                // goomba
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateGoomba(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundUnderground.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateGoomba(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundCastle.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateGoomba(sprites);
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGroundUnderwater.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateGoomba(sprites);
                    mc.pickUpObject(object);
                }
                
                // green koopa
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateKoopa();
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenUnderground.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateKoopa();
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenUnderwater.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateKoopa();
                    mc.pickUpObject(object);
                }

                // piranha plant
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generatePiranhaPlant();
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenUnderground.png");
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generatePiranhaPlant();
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesGreenUnderwater.png");
                if (OImGui.spriteButton(sprites.getSprite(4),uid++)) {
                    GameObject object = Prefab.generatePiranhaPlant();
                    mc.pickUpObject(object);
                }
                
                // red koopa
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/enemiesRed.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateRedKoopa();
                    mc.pickUpObject(object);
                }
                
                // red paratroopa
                if (OImGui.spriteButton(sprites.getSprite(2),uid++)) {
                    GameObject object = Prefab.generateRedParatroopa();
                    mc.pickUpObject(object);
                }
                
                // bowser
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/enemies/bowser.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateBowser();
                    mc.pickUpObject(object);
                }
                
                // end castle characters
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/mario/toadAndPeach.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateSpriteObject(sprites.getSprite(0), 0.25f, 0.375f);
                    mc.pickUpObject(object);
                }
                
                if (OImGui.spriteButton(sprites.getSprite(1),uid++)) {
                    GameObject object = Prefab.generateSpriteObject(sprites.getSprite(1), 0.25f, 0.375f);
                    mc.pickUpObject(object);
                }  
                
                // platform logic
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/platformsTileable.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateMovingPlatform();
                    mc.pickUpObject(object);
                }
                
                // platform 1x1
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateGroundObject(sprites.getSprite(0), 0.125f, 0.125f);
                    object.name = "platform";
                    mc.pickUpObject(object);
                }
                
                // platform 2x1
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/platformsTileable2.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateGroundObject(sprites.getSprite(0), 0.25f, 0.125f);
                    object.name = "platform";
                    mc.pickUpObject(object);
                }
                
                // platform spawner
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generatePlatformSpawner();
                    mc.pickUpObject(object);
                }
                
                // fire jet
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/fireJet.png");
                if (OImGui.spriteButton(sprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generateFireJet();
                    mc.pickUpObject(object);
                }
               
                // bridge axe
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(12),uid++)) {
                    GameObject object = Prefab.generateAxe();
                    mc.pickUpObject(object);
                }
                
                /*sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateItemBlock(sprites);
                    mc.pickUpObject(object);
                }*/
                
                ImGui.separator();
                
                ImGui.sameLine();
                ImGui.endTabItem();
            }
            
            if (ImGui.beginTabItem("Experimental")) {
                ImGui.text("objetos experimentales creados para probar funcionalidades, NO USAR pueden corromper tus niveles");
                int uid = 0;
                
                SpriteSheet pipeSprites = AssetPool.getSpritesheet("assets/images/spriteSheets/blocksAndScenery/pipesFull.png");
                // old pipe w inheritance
                if (OImGui.spriteButton(pipeSprites.getSprite(0),uid++)) {
                    GameObject object = Prefab.generatePipeExp();
                    mc.pickUpObject(object);
                }
                
                // fireball sprite for render test
                Sprite fire = new Sprite(AssetPool.getTexture("assets/images/spriteSheets/marioFireball.png"));
                if (OImGui.spriteButton(fire,uid++)) {
                    GameObject object = Prefab.generateUIText(fire);
                    mc.pickUpObject(object);
                }
                
                sprites = AssetPool.getSpritesheet("assets/images/spriteSheets/particles/coinBlocksOverworld.png");
                if (OImGui.spriteButton(sprites.getSprite(3),uid++)) {
                    GameObject object = Prefab.generateFireRod();
                    mc.pickUpObject(object);
                }
                
                ImGui.endTabItem();
            }
            ImGui.endTabBar();
        }
        ImGui.end();
    }
    
}
