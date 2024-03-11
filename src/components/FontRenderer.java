/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package components;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joml.Vector4f;



/**
 *
 * @author txaber
 */
public class FontRenderer extends Component {

    public transient Font font;
    public String content = "a";
    public int size = 1;
    public Vector4f color = new Vector4f(1f,1f,1f,1f);
    
    @Override
    public void start() {
        if (gameObject.getComponent(SpriteRenderer.class) != null) {
            System.out.println("found font renderer");
        }
        try {
            font = Font.createFont(Font.PLAIN, new File("assets/images/fonts/super-mario-bros-nes.ttf"));
            System.out.println(font.canDisplay('a'));
            //HashMap map = font.getAttributes();
            
            
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(FontRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    @Override
    public void update(float dt) {
        
    }
    
}
