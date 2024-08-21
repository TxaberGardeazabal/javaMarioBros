/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mario;

import gameEngine.Window;
/**
 *  framework/motor para juegos para poder correr super mario bros para la NES,
 *  motor basado en la serie de youtube creado por gabe https://www.youtube.com/c/GamesWithGabe/playlists
 *  hecho con propositos de aprendizaje
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
