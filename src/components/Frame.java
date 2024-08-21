/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

/**
 * Cada frame individual de una animacion
 * @author txaber gardeazabal
 */
public class Frame {
    public Sprite sprite;
    public float frameTime;
    public int spriteNumber = 0;
    
    public Frame() {}
    
    public Frame(Sprite sprite, float time) {
        this.sprite = sprite;
        this.frameTime = time;
    }
    
    public Frame(Sprite sprite, float time, int spriteNumber) {
        this.sprite = sprite;
        this.frameTime = time;
        this.spriteNumber = spriteNumber;
    }
}
