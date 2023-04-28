package com.devcharles.piazzapanic.scene2d;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.devcharles.piazzapanic.MainMenuScreen;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;

/**
 * custom class to add pop up dialog box with input for scenario mode customers
 */
public class InputDialog extends Dialog {

    final PiazzaPanic game;
    private TextField input;

    public InputDialog(String title, TextField input, Skin skin, PiazzaPanic game) {
        super(title, skin);
        this.input = input;
        this.add(input);
        this.row();
        this.game = game;
        this.align(Align.center);
        //TODO Auto-generated constructor stub
    }
    
    /**
     * when button clicked sets maxCustomers to the inputted value and starts the game
     */
    @Override
    protected void result(Object object) {
        
        this.game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
        CustomerAISystem.setMaxCustomers(Integer.parseInt(input.getText()));
        // TODO Auto-generated method stube
        super.result(object);
    }


}
