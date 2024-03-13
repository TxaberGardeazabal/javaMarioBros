/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mario;

import gameEngine.Window;
import render.FontTest;
/**
 *  game/program to run super mario bros,
 *  game engine (jade) taken from https://www.youtube.com/c/GamesWithGabe/playlists
 *  made for learning purposes
 *  @author txaber
 */
public class Mario {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //FontTest font = new FontTest("assets/fonts/super-mario-bros-nes.ttf", 64);
        
        Window window = Window.get();
        window.run();
    }
    
}
