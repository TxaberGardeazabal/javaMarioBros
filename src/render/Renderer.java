/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package render;

import gameEngine.GameObject;
import components.SpriteRenderer;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Controlador del renderizado del juego principal.
 * Muestra los objetos en pantalla a base de pasar varios batches de informacion de renderizado al GPU,
 * el GPU luego interpreta la informacion recibida con un shader y lo muestra en pantalla.
 * Es posible tener multiples renderers, pero no he querido testearlo
 * @author txaber
 */
public class Renderer {
    
    private final int MAX_BATCH_SIZE = 1000;
    private ArrayList<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList();
    }
    
    /**
     * Incluye la parte grafica (spriteRenderer) del objeto para dibujar
     * @param go un gameobject que contiene un spriterenderer
     */
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
    
    /**
     * Añade un sprite para dibujar en uno de los batches
     * @param sprite un spriteRenderer para añadir
     */
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
    
    /**
     * Renderiza el contenido de todos los batches en la GPU
     */
    public void render() {
        //System.out.println("render call, total batches: "+batches.size());
        currentShader.use();
        for (int i = 0; i < batches.size(); i++) {
            //System.out.println("batch "+(i+1));
            batches.get(i).render();
        }
    }
    
    /**
     * Elimina el sprite del objeto especifico de ser dibujado.
     * Esta funcion se llama cuando se borra un objeto.
     * @param go el gameobject que contiene un spriterenderer
     */
    public void destroyGameObject(GameObject go) {
        if (go.getComponent(SpriteRenderer.class) == null) return;
        for (RenderBatch batch : batches) {
            if (batch.destroyIfExists(go)) {
                return;
            }
        }
        
    }
    
    /**
     * Set currentShader.
     * @param shader un shader nuevo para asignar
     */
    public static void bindShader(Shader shader) {
        currentShader = shader;
    }
    
    /**
     * Get currentShader.
     * @return currentShader
     */
    public static Shader getBoundShader() {
        return currentShader;
    }
}
