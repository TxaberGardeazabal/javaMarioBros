/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mario;

import gameEngine.Window;
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
        Window window = Window.get();
        window.run();
    }
    
}
