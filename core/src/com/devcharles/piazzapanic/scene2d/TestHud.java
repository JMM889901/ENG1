package com.devcharles.piazzapanic.scene2d;

import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.scene2d.Hud;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.devcharles.piazzapanic.GameScreen;
import com.devcharles.piazzapanic.MainMenuScreen;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.utility.EntityFactory;

/**
 * HUD user interface rendering for the game, also includes the win screen.
 */
public class TestHud extends Hud {
    public boolean won;
    public boolean triggerWin = false;

    /**
     * Create a dud Hud.
     */
    public TestHud() {
        super(null, null, null, null, new Integer[] { 1 }, new Integer[] { 1 }, null);
    }

    /**
     * Update HUD inventory section.
     * 
     * @param inventory array of {@link FoodType} to display.
     */
    public void updateInventory(FoodType[] inventory) {

    }

    /**
     * Update the current orders HUD section.
     * 
     * @param orders array of {@link FoodType} to display.
     */
    public void updateOrders(FoodType[] orders) {

    }

    /**
     * Render the hud. If {@code triggerWin} is true when this runs, the Win screen
     * will be shown.
     * 
     * @param deltaTime the time elapsed since last frame.
     */
    public void update(float deltaTime) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    /**
     * Freeze the customer timer for a given amount of time.
     * 
     * @param freezeTime how many seconds to freeze the timer for.
     */
    public void freezeCustomers(int freezeTime) {

    }

    /**
     * Win screen
     */
    private void win() {

    }

    @Override
    public void resize(int width, int height) {

    }

    public void dispose() {

    }

    private ClickListener createListener(final Screen screen) {
        return null;
    }
}
