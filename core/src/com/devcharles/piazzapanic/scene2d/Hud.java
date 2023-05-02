package com.devcharles.piazzapanic.scene2d;

import com.badlogic.ashley.core.Engine;
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
import com.devcharles.piazzapanic.componentsystems.InWorldStoreSystem;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.SaveHandler;

/**
 * HUD user interface rendering for the game, also includes the win screen.
 */
public class Hud extends ApplicationAdapter {
    private Boolean _ISNTTEST; // This just disables trying a few things that don't aren't needed for testing.
    public Stage stage;
    private Viewport viewport;
    public Integer customerTimer = 000;// Used elsewhere to get game time with respect to the time freeze powerup
    private Integer customerFreezeTimer = 0; // Using a time freeze powerup pauses the customer timer while this one
                                             // counts down.
    private float timeCounter = 0;
    public static Integer[] reputation;
    // Money as a practical requirement of functional requirement FR_INVESTMENT
    private static Integer[] money; // This is static purely for the sake of simplicity (and testing), yes its bad
                                    // practice but cry about it
    private Skin skin;

    private final float fontScale = 0.6f;

    // A label is basically a widget
    LabelStyle hudLabelStyle;
    LabelStyle titleLabelStyle;
    Label timerLabel;
    Label timeNameLabel;
    Label reputationLabel;
    Label reputationNameLabel;
    Label pausedNameLabel;
    Label moneyLabel;
    Label moneyNameLabel;
    BitmapFont uiFont, uiTitleFont;
    // an image used as the background of recipe book and tutorial
    private Image photo;

    private Game game;
    private Engine engine;
    private InWorldStoreSystem inWorldStoreSystem;
    private Table tableBottom, tableRight, tableTop, tablePause, tableBottomLabel;

    private boolean pauseToggled = false;
    public boolean paused = false;

    private GameScreen gameScreen;

    /**
     * Create the hud.
     * 
     * @param spriteBatch      the batch to draw the HUD with
     * @param savedGame        reference to the screen drawing the hud to switch
     *                         back to in case of screen transitions.
     * @param game             {@link PiazzaPanic} instance for switching screens.
     * @param reputationPoints Must be an object to pass by reference, see
     *                         https://stackoverflow.com/questions/3326112/java-best-way-to-pass-int-by-reference
     */
    public Hud(SpriteBatch spriteBatch, final GameScreen savedGame, final Game game, Engine engine,
            Integer[] reputationPoints,
            Integer[] money, InWorldStoreSystem inWorldStoreSystem) {

        _ISNTTEST = spriteBatch != null;
        this.game = game;
        this.engine = engine;
        Hud.reputation = reputationPoints;
        this.gameScreen = savedGame;
        Hud.money = money; // Yes player money is handled here, cope and seethe bozo
        this.inWorldStoreSystem = inWorldStoreSystem; // In world store system for implementation of FR_INVESTMENT

        // Setup the viewport
        if (_ISNTTEST) {
            viewport = new ScreenViewport(new OrthographicCamera(1280, 720));
            stage = new Stage(viewport, spriteBatch);
            viewport.apply();
        }

        // Import the custom skin with different fonts
        skin = new Skin(Gdx.files.internal("craftacular/skin/craftacular-ui.json"));
        uiFont = new BitmapFont(Gdx.files.internal("craftacular/raw/font-export.fnt"));
        uiTitleFont = new BitmapFont(Gdx.files.internal("craftacular/raw/font-title-export.fnt"));

        // Create generic style for labels with the different fonts
        hudLabelStyle = new Label.LabelStyle();
        hudLabelStyle.font = uiFont;
        titleLabelStyle = new Label.LabelStyle();
        titleLabelStyle.font = uiTitleFont;

        if (_ISNTTEST) {// Not active in testing for gdx.graphics reasons
            stage.addListener(new InputListener() {
                @Override
                public boolean keyDown(InputEvent event, int keycode) {
                    if (keycode == Keys.ESCAPE) {
                        pauseToggled = true;
                        // sets game to go bigscreen if F11 is pressed or sets it to go small screen
                    } else if (keycode == Keys.F11) {
                        Boolean fullScreen = Gdx.graphics.isFullscreen();
                        Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
                        if (fullScreen == true) {
                            Gdx.graphics.setWindowedMode(1280, 720);
                        } else {
                            Gdx.graphics.setFullscreenMode(currentMode);
                        }
                    }
                    return true;
                }
            });
        }

        // Create the UI layout.
        if (_ISNTTEST) {// UI not generated in headless tests for obvious reasons
            createTables();
        }
    }

    private void createTables() {

        timerLabel = new Label(String.format("%03d", customerTimer), hudLabelStyle);
        reputationLabel = new Label(String.format("%01d", reputation[0]), hudLabelStyle);
        moneyLabel = new Label(String.format("%01d", money[0]), hudLabelStyle); // (FR_INVESTMENT)
        timeNameLabel = new Label("Time", hudLabelStyle);
        reputationNameLabel = new Label("Reputation", hudLabelStyle);
        moneyNameLabel = new Label("$", hudLabelStyle); // (FR_INVESTMENT)
        // Creates a bunch of labels and sets the fontsize
        reputationLabel.setFontScale(fontScale + 0.1f);
        timerLabel.setFontScale(fontScale + 0.1f);
        moneyLabel.setFontScale(fontScale + 0.1f);
        timeNameLabel.setFontScale(fontScale + 0.1f);
        reputationNameLabel.setFontScale(fontScale + 0.1f);
        moneyNameLabel.setFontScale(fontScale + 0.1f);
        // lays out timer, money and reputation
        tableTop = new Table();
        tableTop.top();
        tableTop.setFillParent(true);

        tableTop.add(timeNameLabel).expandX().padTop(10);
        tableTop.add(reputationNameLabel).expandX().padTop(10);
        tableTop.add(moneyNameLabel).expandX().padTop(10);

        tableTop.row();
        tableTop.add(timerLabel).expandX();
        tableTop.add(reputationLabel).expandX();
        tableTop.add(moneyLabel).expandX();

        tableBottomLabel = new Table();
        tableBottomLabel.bottom();
        tableBottomLabel.setFillParent(true);

        Label inv = new Label("Inventory", hudLabelStyle);
        inv.setFontScale(fontScale);
        tableBottomLabel.add(inv).padBottom(60);
        // the pause screen
        tablePause = new Table();
        tablePause.setFillParent(true);
        tablePause.setVisible(false); // not visible by default

        pausedNameLabel = new Label("Game paused", titleLabelStyle);
        tablePause.add(pausedNameLabel).padBottom(30);

        tablePause.row();
        // checks if resume button is clicked
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton recipeBookButton = new TextButton("Recipe Book", skin);
        TextButton tutorialButton = new TextButton("Tutorial", skin);
        TextButton storeButton = new TextButton("Store", skin); // Store button added for implementation of
                                                                // FR_INVESTMENT
        TextButton saveButton = new TextButton("Save", skin);

        resumeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                pauseToggled = true;
            }
        });

        final Hud myHud = this; // :)

        recipeBookButton.addListener(createListener(new Slideshow(game, Slideshow.Type.recipe, gameScreen)));
        tutorialButton.addListener(createListener(new Slideshow(game, Slideshow.Type.tutorial, gameScreen)));
        storeButton.addListener(createListener(new StoreScreen(game, gameScreen, inWorldStoreSystem, this)));
        saveButton.addListener(new ClickListener() {// Store button added for implementation of FR_INVESTMENT
            public void clicked(InputEvent event, float x, float y) {
                SaveHandler.save(SaveHandler.SAVE_FILE, engine, GameScreen.world, myHud);
                // saveButton.setDisabled(true);
            }
        });

        tablePause.add(resumeButton).width(240).height(70).padBottom(30);
        tablePause.row();
        tablePause.add(recipeBookButton).width(240).height(70).padBottom(30);
        tablePause.row();
        tablePause.add(tutorialButton).width(240).height(70);
        tablePause.row();
        tablePause.add(storeButton).width(240).height(70);
        tablePause.row();
        tablePause.add(saveButton).width(240).height(70);
        this.tableRight = new Table();
        this.tableBottom = new Table();

        stage.addActor(tablePause);
        stage.addActor(tableTop);
        stage.addActor(tableRight);
        stage.addActor(tableBottom);
        stage.addActor(tableBottomLabel);
    }

    /**
     * Update HUD inventory section.
     * 
     * @param inventory array of {@link FoodType} to display.
     */
    public void updateInventory(FoodType[] inventory) {
        tableBottom.clear();
        tableBottom.bottom();
        tableBottom.setFillParent(true);

        tableBottom.row();
        for (int i = 0; i < inventory.length; i++) {
            TextureRegion region = EntityFactory.getFoodTexture(inventory[i]);
            if (region == null) {
                Gdx.app.log("Texture is null", ""); // debugger can be ignored/removed idk do what you want
            } else {
                // adds images of the items the controlled cook is holding into the inventory
                photo = new Image(region);
                tableBottom.add(photo).width(64).height(64).center();
            }
        }
    }

    /**
     * Update the current orders HUD section.
     * 
     * @param orders array of {@link FoodType} to display.
     */
    public void updateOrders(FoodType[] orders) {
        tableRight.clear();
        tableRight.right();
        tableRight.setFillParent(true);

        for (int i = 0; i < orders.length; i++) {
            TextureRegion region = EntityFactory.getFoodTexture(orders[i]);
            if (region == null) {
                Gdx.app.log("Texture is null", "");
            } else {
                // adds all the orders onto the right of the screen with a little number
                Label orderNumberLabel = new Label(String.format("%01d", i + 1), hudLabelStyle);
                tableRight.add(orderNumberLabel).padRight(10);
                photo = new Image(region);
                tableRight.add(photo).width(64).height(64);
                tableRight.row().height(0);
            }
        }
    }

    // Helper functions for FR_INVESTMENT, FR_SAVE_FILES and testing
    public int initMoney(int moneyToSet) {
        money[0] = moneyToSet;
        moneyLabel.setText(money[0]);
        return money[0];
    }

    public int addMoney(int moneyToAdd) {
        money[0] += moneyToAdd;
        if (moneyLabel != null)// hehe
            moneyLabel.setText(money[0]);
        return money[0];
    }

    public static int getMoney() {
        return money[0];
    }

    /**
     * ONLY USE FOR UNIT TESTS.
     * 
     * @param moneyToSet
     * @return
     */
    public static int setMoneyReference_TEST(Integer[] moneyToSet) {
        money = moneyToSet;
        return money[0];
    }

    /**
     * Render the hud. If {@code triggerWin} is true when this runs, the Win screen
     * will be shown.
     * 
     * @param deltaTime the time elapsed since last frame.
     */
    public void update(float deltaTime) {
        if (paused) {
            if (pauseToggled) {
                pauseToggled = false;
                this.resume();
            }

            stage.act();
            stage.draw();
            return;
        }

        timeCounter += won ? 0 : deltaTime; // If won, don't count time.

        // I (Joss) think the comment below was by the previous group implementing a
        // really hacky
        // solution to a performance problem:

        // Staggered once per second using timeCounter makes it way faster
        if (timeCounter >= 1) {

            // If the customer is not frozen, increment the timer.
            if (customerFreezeTimer == 0) {
                customerTimer++;
            } else {
                customerFreezeTimer--;
            }

            timerLabel.setText(String.format("%03d", customerTimer));
            reputationLabel.setText(reputation[0]);
            if (triggerWin) {
                triggerWin = false;
                win();
            } else if (reputation[0] < 1) { // Implementation of FR_GAME_OVER as an extention of FR_REPUTATION
                lose();// BOO AND I CANNOT STRESS THIS ENOUGH, WOMP

            }
            timeCounter -= 1;
        }
        // Pause toggle moved out of timecounter loop to reduce visible input lag
        if (pauseToggled) {
            pauseToggled = false;
            this.pause();
        }
        stage.act();
        stage.draw();

    }

    @Override
    public void pause() {
        paused = true;
        gameScreen.pause();

        // Hide the normal hud
        tableBottom.setVisible(false);
        tableRight.setVisible(false);
        tableTop.setVisible(false);
        tableBottomLabel.setVisible(false);

        // Show the pause hud
        tablePause.setVisible(true);

        // super.pause();
    }

    @Override
    public void resume() {
        paused = false;
        gameScreen.resume();

        // Show the normal hud
        tableBottom.setVisible(true);
        tableRight.setVisible(true);
        tableTop.setVisible(true);
        tableBottomLabel.setVisible(true);

        // Hide the pause hud
        tablePause.setVisible(false);

        super.resume();
    }

    /**
     * Freeze the customer timer for a given amount of time.
     * Implemented as a part of the timefreeze powerup (FR_POWERUPS)
     * 
     * @param freezeTime how many seconds to freeze the timer for.
     */
    public void freezeCustomers(int freezeTime) {
        customerFreezeTimer += freezeTime;
    }

    public boolean won;
    public boolean triggerWin = false;

    /**
     * Win screen
     */
    private void win() {
        won = true;
        // winscreen table made
        stage.clear();
        Table centerTable = new Table();
        centerTable.setFillParent(true);
        // labels given different fonts so it looks nicer
        Label congrats = new Label("Congratulations!", titleLabelStyle);
        Label congratsSubtitle = new Label("You won!", hudLabelStyle);
        // colspan2 important! do some googling if you dont know what it does (scene2d)
        centerTable.add(congrats).padBottom(40).colspan(2);
        centerTable.row();
        centerTable.add(congratsSubtitle).padBottom(30).colspan(2);

        centerTable.row();

        centerTable.add(timeNameLabel);
        centerTable.add(reputationNameLabel);

        centerTable.row();

        centerTable.add(timerLabel);
        centerTable.add(reputationLabel);

        centerTable.row();

        TextButton returnToMenuButton = new TextButton("Main menu", skin);
        centerTable.add(returnToMenuButton).width(240).height(70).padTop(50).colspan(2);

        returnToMenuButton.addListener(createListener(new MainMenuScreen((PiazzaPanic) game)));

        stage.addActor(centerTable);
    }

    // Implementation of loss ui as per FR_GAME_OVER
    private void lose() {
        won = true;// "But you didnt win you cant set won to true" cope harder
        // losescreen table made
        stage.clear();
        Table centerTable = new Table();
        centerTable.setFillParent(true);
        // labels given different fonts so it looks nicer
        Label congrats = new Label("You lost!", titleLabelStyle);
        Label congratsSubtitle = new Label("Better luck next time!", hudLabelStyle);
        // colspan2 important! do some googling if you dont know what it does (scene2d)
        centerTable.add(congrats).padBottom(40).colspan(2);
        centerTable.row();
        centerTable.add(congratsSubtitle).padBottom(30).colspan(2);

        centerTable.row();

        centerTable.add(timeNameLabel);
        centerTable.add(reputationNameLabel);

        centerTable.row();

        centerTable.add(timerLabel);
        centerTable.add(reputationLabel);

        centerTable.row();

        TextButton returnToMenuButton = new TextButton("Main menu", skin);
        centerTable.add(returnToMenuButton).width(240).height(70).padTop(50).colspan(2);

        returnToMenuButton.addListener(createListener(new MainMenuScreen((PiazzaPanic) game)));

        stage.addActor(centerTable);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height, true);
        viewport.apply();
    }

    public void dispose() {
        stage.dispose();
    }

    private ClickListener createListener(final Screen screen) {
        return new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(screen);
            }
        };
    }
}
