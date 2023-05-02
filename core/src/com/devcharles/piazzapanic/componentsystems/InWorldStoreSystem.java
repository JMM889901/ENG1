package com.devcharles.piazzapanic.componentsystems;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.MapLoader;

/*
 * Responsible for spawning entities as per store purchasses to fulfill more cooks subrequirement of FR_INVESTMENT
 */
public class InWorldStoreSystem {
    // Ok i will concede this doesnt technically belong here but like, cope, seethe
    // and mald
    PooledEngine engine;
    EntityFactory factory;
    World world;
    MapLoader mapLoader;

    public InWorldStoreSystem(PooledEngine engine, EntityFactory factory, World world, MapLoader mapLoader) {
        this.engine = engine;
        this.factory = factory;
        this.world = world;
        this.mapLoader = mapLoader;
    }

    public void SpawnNewCook() {
        Vector2 location = mapLoader.GetCookSpawns()
                .get(ThreadLocalRandom.current().nextInt(0, mapLoader.GetCookSpawns().size()));
        // :skull:
        factory.createCook((int) location.x, (int) location.y);
    }
}
