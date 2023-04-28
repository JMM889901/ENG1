package com.devcharles.piazzapanic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.MapLoader;

public class testEnvironment extends HeadlessApplication {
    public Engine engine;
    public World world;
    public EntityFactory factory;
    public MapLoader loader;

    /**
     * Create a test environment for testing components and systems
     * Empty constructor creates the application listner, engine and factory
     * for testing
     * Also strips the map of lighting then builds the world
     */
    public testEnvironment() {

        super(new ApplicationListener() {

            @Override
            public void create() {

            }

            @Override
            public void resize(int width, int height) {

            }

            @Override
            public void render() {

            }

            @Override
            public void pause() {

            }

            @Override
            public void resume() {

            }

            @Override
            public void dispose() {

            }

        });
        Gdx.gl = new emptyGL20();
        engine = new PooledEngine();
        world = new World(new Vector2(0, 0), true);
        factory = new EntityFactory((PooledEngine) engine, world);

        loader = new MapLoader(null, null, factory);
        // Strip lighting from world because no gl20
        MapObjects objects = loader.map.getLayers().get("MapObjects").getObjects();
        for (MapObject mapObject : objects) {
            MapProperties properties = mapObject.getProperties();
            if (properties.containsKey("lightID")) {
                properties.remove("lightID");
            }
        }
    }

}
