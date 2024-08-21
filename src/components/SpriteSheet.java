/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import editor.ConsoleWindow;
import java.util.ArrayList;
import org.joml.Vector2f;
import render.Texture;

/**
 * Esta clase interpreta y maneja las texturas con varias imagenes (spritesheets).
 * La clase no puede interpretar imagenes cuyos sprites no mantienen el orden con el que estan
 * guardados aqui (diferentes tamaños, espaciado, posicion...)
 * @author txaber
 */
public class SpriteSheet {
    
    private Texture texture;
    private ArrayList<Sprite> sprites;

    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int spacing) {
        this.sprites = new ArrayList();
        
        this.texture = texture;
        int currentX = 0;
        int currentY = texture.getHeight() - spriteHeight;
        for (int i = 0; i < numSprites; i++) {
            float topY = (currentY + spriteHeight) / (float)texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float)texture.getWidth();
            float leftX = currentX / (float)texture.getWidth();
            float bottomY = currentY / (float)texture.getHeight();
            
            Vector2f[] texCoords = {
                new Vector2f(rightX, topY),
                new Vector2f(rightX, bottomY),  
                new Vector2f(leftX, bottomY),  
                new Vector2f(leftX, topY)
            };
            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTexCoords(texCoords);
            sprite.setWidth(spriteWidth);
            sprite.setHeight(spriteHeight);
            this.sprites.add(sprite);
            
            currentX += spriteWidth + spacing;
            if(currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + spacing;
            }
        }
    }

    /**
     * Devuelve la imagen que esta en la posicion index dentro del spritesheet.
     * @param index una posicion dentro del spritesheet
     * @return el sprite en la posicion pasada
     */
    public Sprite getSprite(int index) {
        if (index < this.sprites.size()) {
            return this.sprites.get(index);
        }
        ConsoleWindow.addLog("sprite index "+index+" out of bound "+sprites.size(),
                ConsoleWindow.LogCategory.warning);
        return null;
    }
    
    public Texture getTexture() {
        return this.texture;
    }
    
    public int size() {
        return sprites.size();
    }
}
