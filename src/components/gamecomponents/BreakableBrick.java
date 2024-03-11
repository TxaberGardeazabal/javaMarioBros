/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components.gamecomponents;

import components.gamecomponents.Block;
import components.gamecomponents.PlayerController;
import util.AssetPool;

/**
 *
 * @author txaber gardeazabal
 */
public class BreakableBrick extends Block {

    @Override
    public void playerHit(PlayerController playerController) {
        if (!playerController.isSmall()) {
            AssetPool.getSound("assets/sounds/break_block.ogg").play();
            gameObject.destroy();
        }
    }
    
}
