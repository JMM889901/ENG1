package com.devcharles.piazzapanic.scene2d;

import com.badlogic.ashley.core.Entity;
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
import com.devcharles.piazzapanic.components.LockedComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.TextureComponent;
import com.devcharles.piazzapanic.componentsystems.InWorldStoreSystem;
import com.devcharles.piazzapanic.utility.Station.StationType;

public class StoreScreen extends ApplicationAdapter implements Screen {

    OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private Batch batch;
    private Table table;
    private InWorldStoreSystem inWorldStoreSystem;
    private int CooksToSpawn;
    private Hud hud;

    // Build the store screen
    public StoreScreen(final Game game, final Screen previousScreen, final InWorldStoreSystem inWorldStoreSystem,
            final Hud hud) {
        this.hud = hud;
        this.inWorldStoreSystem = inWorldStoreSystem;
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
                SpawnItems();
            }
        });
        table.add(exit).width(200).height(50);
        table.row();
        TextButton cookSpeed = new TextButton("cookSpeed boost(10)", skin);
        cookSpeed.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (CookingComponent.COOKING_TIME_BASE > 600 && Hud.getMoney() >= 10) {
                    CookingComponent.COOKING_TIME_BASE -= 300f;
                    hud.addMoney(-10);
                }

            }
        });
        table.add(cookSpeed);
        table.row();

        TextButton extraOven = new TextButton("extra oven(15)", skin);
        extraOven.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                unlockStationOfType(StationType.oven, 15);
            }
        });
        table.add(extraOven);
        table.row();

        TextButton extraKnife = new TextButton("extra knife(15)", skin);
        extraKnife.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                unlockStationOfType(StationType.cutting_board, 15);
            }
        });
        table.add(extraKnife);
        table.row();

        TextButton extraGrill = new TextButton("extra grill(15)", skin);
        extraGrill.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                unlockStationOfType(StationType.grill, 15);
            }
        });
        table.add(extraGrill);

        table.row();

        TextButton newCook = new TextButton("Hire cook(15)", skin);
        newCook.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                if (Hud.getMoney() < 15)
                    return;
                hud.addMoney(-15);
                inWorldStoreSystem.SpawnNewCook();
            }
        });
        table.add(newCook);

        stage.addActor(table);
    }

    void SpawnItems() {
        for (int i = 0; i < CooksToSpawn; i++) {
            inWorldStoreSystem.SpawnNewCook();
        }
    }

    void unlockStationOfType(StationType type, int cost) {
        if (Hud.getMoney() < cost)
            return;
        for (Entity station : LockedComponent.lockedStations) {
            StationComponent component = station.getComponent(StationComponent.class);
            if (component.type == type) {
                station.remove(LockedComponent.class);
                station.getComponent(TextureComponent.class).region = null;
                break;
            }
        }
        hud.addMoney(-cost);
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
