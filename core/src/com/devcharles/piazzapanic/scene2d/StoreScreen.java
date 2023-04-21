package com.devcharles.piazzapanic.scene2d;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.devcharles.piazzapanic.GameScreen;
import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.components.CookingComponent;

public class StoreScreen extends ApplicationAdapter implements Screen {

    OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private Batch batch;
    private Table table;

    // Build the store screen
    public StoreScreen(final Game game, final Screen previousScreen) {
        camera = new OrthographicCamera();
        skin = new Skin(Gdx.files.internal("craftacular/skin/craftacular-ui.json"));
        ScalingViewport viewport = new ScalingViewport(Scaling.fit, 1280, 720, camera);
        viewport.apply();
        batch = new SpriteBatch();
        stage = new Stage(viewport);
        table = new Table();
        table.left();
        table.top(); // Does this work?
        table.setFillParent(true);

        // Rows of the table are seperated using table.row(), table.column also does the
        // obvious, see {@Link Slideshow.buildTable()} for example
        TextButton exit = new TextButton("Exit", skin);
        exit.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(previousScreen);
            }
        });
        table.add(exit).width(200).height(50);
        table.row();
        TextButton cookSpeed = new TextButton("cookSpeed boost", skin);
        cookSpeed.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (CookingComponent.COOKING_TIME_BASE > 600)
                    CookingComponent.COOKING_TIME_BASE -= 300f;
            }
        });
        table.add(cookSpeed);
        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.getViewport().apply();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(stage.getViewport().getCamera().combined);
        batch.begin();
        batch.end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getViewport().apply();
    }

    public void dispose() {
        skin.dispose();
        stage.dispose();
    }

    @Override
    public void hide() {
    }

}
