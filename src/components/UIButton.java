/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package components;

import gameEngine.GameObject;
import gameEngine.MouseListener;
import gameEngine.Window;

/**
 *
 * @author txaber gardeazabal
 */
public class UIButton extends Component {
    public String PanelToOpenName = "";
    public Action action;
    
    public enum Action {
        openPanel,
        startGame,
        startLevel,
        openEditor,
        close
    }
    
    @Override
    public void update(float dt) {
        if (isClicked()) {
            System.out.println("button pressed!!");
        }
    }
    
    public boolean isClicked() {
        int x = (int)MouseListener.getScreenX();
        int y = (int)MouseListener.getScreenY();
        int goId = Window.getPickingTexture().readPixel(x, y);
        GameObject pickObj = Window.getScene().getGameObject(goId);
        return pickObj != null && pickObj.equals(this.gameObject);
    }
}
