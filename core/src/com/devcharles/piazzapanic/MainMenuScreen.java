package com.devcharles.piazzapanic;

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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.devcharles.piazzapanic.scene2d.Slideshow;

/**
 * Main menu of the game, transitions the player to the Tutorial
 * {@link Slideshow} on button press
 */
public class MainMenuScreen extends ApplicationAdapter implements Screen {

    final PiazzaPanic game;
    OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private Batch batch;
    private Sprite sprite;
    private BitmapFont gamesFont;
    private Label title;

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
        // Checks if button is clicked and if clicked goes onto the tutorial set game to
        // endless mode
        scenarioModeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
                dispose();
            }
        });

        root.row();
        
        TextButton endlessModeButton = new TextButton("Endless Mode", skin);
        root.add(endlessModeButton).expandX().padBottom(20);

        // Checks if button is clicked and if clicked goes onto the tutorial set game to
        // scenario mode
        endlessModeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Slideshow(game, Slideshow.Type.tutorial));
                dispose();
            }
        });

        root.row();
        
        TextButton quitButton = new TextButton("Quit", skin);
        root.add(quitButton).expandX().padBottom(20);

        // Checks if button is clicked and if clicked goes onto the tutorial set game to
        // scenario mode
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
