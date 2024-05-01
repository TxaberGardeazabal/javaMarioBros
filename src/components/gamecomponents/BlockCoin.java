/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.Component;
import org.joml.Vector2f;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class BlockCoin extends Component{
    private Vector2f topY;
    private float coinSpeed = 1.4f;
    
    @Override
    public void start() {
        topY = new Vector2f(gameObject.transform.position.y).add(0,0.5f);
    }
    
    @Override
    public void update(float dt) {
        if  (this.gameObject.transform.position.y < topY.y) {
            this.gameObject.transform.position.y += dt * coinSpeed;
            this.gameObject.transform.scale.x -= (0.5f * dt) % -1.0f;
        } else {
            gameObject.destroy();
        }
    }
}
