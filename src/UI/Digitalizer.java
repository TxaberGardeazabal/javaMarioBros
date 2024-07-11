/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package UI;

import components.Component;
import gameEngine.GameObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author txaber gardeazabal
 */
public class Digitalizer extends Component{
    
    private ArrayList<Digit> digits = new ArrayList<>();
    private int value = 0;

    @Override
    public void start() {
        List<GameObject> childGOs = this.gameObject.getChildGOs();
        digits = new ArrayList<>();

        for (GameObject go : childGOs) {
            Digit d = go.getComponent(Digit.class);
            if (d != null) {
                digits.add(d);
            }
        }
    }
    
    @Override
    public void update(float dt) {
        add(1);
    }
    
    public void add(int number) {
        setValue(value+number);
    }

    public void sub(int number) {
        setValue(value-number);
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        
        // display the value
        String[] valArray = String.valueOf(value).split("");
        if (valArray.length > this.digits.size()) {
            // to long to display
            System.out.println("warning: number "+value+" too long to be diplayed for size "+this.digits.size()+" in "+this.gameObject.name);
            valArray = new String [this.digits.size()];
            Arrays.fill(valArray, "9");
        }
        
        int diff = this.digits.size() - valArray.length;
        for (int i = valArray.length; i > 0 ; i--) {
            digits.get(i+diff-1).setDigit(valArray[i-1]);
            
            if (Integer.parseInt(valArray[i-1]) > 9 || Integer.parseInt(valArray[i-1]) < 0) {
                System.out.println("warning: number "+valArray[i-1]+" cannot be displayed in a single digit in "+this.gameObject.name);
            }
        }
    }
    
    public int getMaxSize() {
        return this.digits.size();
    }
}
