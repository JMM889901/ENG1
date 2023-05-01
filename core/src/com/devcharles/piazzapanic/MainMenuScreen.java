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
import com.devcharles.piazzapanic.input.MyTextInputListener;
import com.devcharles.piazzapanic.scene2d.InputDialog;
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
    private Dialog numCustomersInput;
    private TextField input;
    private SelectBox<String> difficultySelect;

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
        TextButton scenarioModeButton = new TextButton("Scenario Mode", skin);
        root.add(scenarioModeButton).expandX().padBottom(20);

        input = new TextField("5", skin);
        input.setTextFieldFilter(new DigitsOnlyFilter());
        input.setAlignment(1);

        numCustomersInput = new InputDialog("", input, skin, game);
        numCustomersInput.text("Enter number of customers:");
        numCustomersInput.row();

        difficultySelect = new SelectBox<String>(skin2);
        difficultySelect.setItems("Easy", "Medium", "Hard");
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
        difficultySelect.setAlignment(0);

        numCustomersInput.row();

        numCustomersInput.add(difficultySelect);
        // numCustomersInput.add(input);
        numCustomersInput.row();
        numCustomersInput.button("Play");
        // numCustomersInput.scaleBy(1.5f);
        numCustomersInput.align(Align.center);
        root.addActor(numCustomersInput);
        numCustomersInput.hide();

        // Checks if button is clicked and if clicked goes onto the tutorial set game to
        // scenario mode, opens dialog box to input number of customers
        scenarioModeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                numCustomersInput.show(stage);
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
                game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
                dispose();
            }
        });

        root.row();

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

        MyTextInputListener listener = new MyTextInputListener();
        Gdx.input.getTextInput(listener, "Dialog Title", "Initial Textfield Value", "Hint Value");

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
