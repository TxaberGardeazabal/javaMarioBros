/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package render;

import components.FontRenderer;
import gameEngine.GameObject;
import components.SpriteRenderer;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author txaber
 */
public class Renderer {
    
    private final int MAX_BATCH_SIZE = 1000;
    private ArrayList<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList();
    }
    
    public void add(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        
        if (spr != null) {
            add(spr);
        }
        /*
        FontRenderer fr = go.getComponent(FontRenderer.class);
        if (fr != null) {
            add(fr);
        }*/
    }
    
    public void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.getzIndex() == sprite.gameObject.transform.zIndex) {
                Texture tex = sprite.getTexture();
                if (tex == null || (batch.hasTexture(tex) || batch.hasTextureRoom())) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }
        
        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, 
                    sprite.gameObject.transform.zIndex, this);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }
    
    // TODO: remake this function once font rendering feels more safe
    /*public void add(FontRenderer text) {
        boolean added = false;
        for (FontRenderBatch batch : fontBatches) {
            if (batch.getzIndex() == text.gameObject.transform.zIndex) {
                
                batch.addText(text);
                added = true;
                break;
                
            }
        }
        
        if (!added) {
            FontRenderBatch newBatch = new FontRenderBatch(MAX_BATCH_SIZE, 
                    text.gameObject.transform.zIndex, this);
            newBatch.start();
            fontBatches.add(newBatch);
            newBatch.addText(text);
            Collections.sort(fontBatches);
        }
    }*/
    
    public void render() {
        //System.out.println("render call, total batches: "+batches.size());
        currentShader.use();
        for (int i = 0; i < batches.size(); i++) {
            //System.out.println("batch "+(i+1));
            batches.get(i).render();
        }
    }
    
    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
        
    }
    
    public static void bindShader(Shader shader) {
        currentShader = shader;
    }
    
    public static Shader getBoundShader() {
        return currentShader;
    }
}
