package com.devcharles.piazzapanic;

import java.io.Console;
import java.io.File;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.scene2d.Slideshow;
import com.devcharles.piazzapanic.utility.SaveHandler;

/**
 * Main menu of the game, transitions the player to the Tutorial
 * {@link Slideshow} on button press
 */
public class MainMenuScreen extends ApplicationAdapter implements Screen {

    final PiazzaPanic game;
    OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private Skin skin2;
    private Batch batch;
    private Sprite sprite;
    private BitmapFont gamesFont;
    private Label title;
    // Values used for the implementations of FR_DIFFICULTY, FR_SCRENARIO_MODE,
    // FR_ENDLESS_MODE and FR_GAME_MODE
    private TextField input;
    private SelectBox<String> difficultySelect;
    private Dialog playModeDialog;
    private final Button playScenario;
    private final Button playEndless;

    /**
     * creates main menu stage
     * 
     * @param game
     */
    public MainMenuScreen(final PiazzaPanic game) {

        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        batch = new SpriteBatch();

        sprite = new Sprite(new Texture(Gdx.files.internal("mainMenuImage.png")));
        sprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        skin = new Skin(Gdx.files.internal("craftacular/skin/craftacular-ui.json"));
        skin2 = new Skin(Gdx.files.internal("skin2/uiskin.json"));
        stage = new Stage(new ScreenViewport());

        Label.LabelStyle menuLabelStyle = new Label.LabelStyle();
        gamesFont = new BitmapFont(Gdx.files.internal("craftacular/raw/font-title-export.fnt"));
        menuLabelStyle.font = gamesFont;

        title = new Label("Piazza Panic", menuLabelStyle);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        root.add(title).expandX().padBottom(100);
        root.row();
        // Implementation of FR_GAME_MODES
        TextButton scenarioModeButton = new TextButton("Scenario Mode", skin);
        root.add(scenarioModeButton).expandX().padBottom(20);

        // Textfield to input number of customers in scenario mode
        input = new TextField("5", skin);
        input.setTextFieldFilter(new DigitsOnlyFilter());
        input.setAlignment(Align.center);

        // SelectBox to choose difficulty setting
        // Implementation of FR_DIFFICULTY
        difficultySelect = new SelectBox<String>(skin2);
        difficultySelect.setItems("Easy", "Medium", "Hard");
        difficultySelect.setAlignment(Align.center);

        // listener to set the difficulty based on selection
        difficultySelect.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (difficultySelect.getSelected().equals("Easy")) {
                    CustomerAISystem.setDifficulty(0);
                } else if (difficultySelect.getSelected().equals("Medium")) {
                    CustomerAISystem.setDifficulty(1);
                } else if (difficultySelect.getSelected().equals("Hard")) {
                    CustomerAISystem.setDifficulty(2);
                }
            }
        });

        // dialog box to pop up when endless mode is chosen
        // includes difficulty selection
        playModeDialog = new Dialog("", skin);

        playModeDialog.text("Difficulty:");
        playModeDialog.getContentTable().add(difficultySelect);
        playModeDialog.getContentTable().row();

        playEndless = new TextButton("Play", skin);
        playEndless.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
                dispose();
            }
        });

        playScenario = new TextButton("Play", skin);
        // Implementation of configurable customer count as per FR_SCENARIO_MODE
        playScenario.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                CustomerAISystem.setMaxCustomers(Integer.parseInt(input.getText()));
                game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
                dispose();
            }
        });

        // Checks if button is clicked and if clicked goes onto the tutorial set game to
        // scenario mode, opens dialog box to input number of customers
        scenarioModeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                playModeDialog.getContentTable().row();
                playModeDialog.text("Enter number of customers:");
                playModeDialog.getContentTable().add(input);
                playModeDialog.button(playScenario);
                playModeDialog.show(stage);
                // game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
                // dispose();
            }
        });

        root.row();

        TextButton endlessModeButton = new TextButton("Endless Mode", skin);
        root.add(endlessModeButton).expandX().padBottom(20);

        // Checks if button is clicked and if clicked goes onto the tutorial set game to
        // endless mode
        endlessModeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                CustomerAISystem.setMaxCustomers((int) Double.POSITIVE_INFINITY);
                playModeDialog.button(playEndless);
                playModeDialog.show(stage);
            }
        });

        root.row();
        // Implementation of FR_SAVE_FILES
        TextButton loadFromFileButton = new TextButton("Resume previous", skin);
        root.add(loadFromFileButton).expandX().padBottom(20);
        File saveFile = new File(SaveHandler.SAVE_FILE);
        loadFromFileButton.setDisabled(!saveFile.isFile());

        loadFromFileButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.loadFrom = SaveHandler.SAVE_FILE;
                game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
                dispose();
            }
        });

        root.row();

        TextButton quitButton = new TextButton("Quit", skin);
        root.add(quitButton).expandX().padBottom(20);

        // Checks if button is clicked and if clicked quits
        quitButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // draws everything (dont change this order unless you know what youre doing)
        batch.begin();
        sprite.draw(batch);
        batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {

    }

    public void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
