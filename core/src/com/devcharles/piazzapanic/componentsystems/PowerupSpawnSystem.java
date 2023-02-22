package com.devcharles.piazzapanic.componentsystems;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.components.B2dBodyComponent;
import com.devcharles.piazzapanic.components.Powerups.PowerupSpawnControllerComponent;
import com.devcharles.piazzapanic.components.Powerups.PowerupSpawnerComponent;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;

import box2dLight.RayHandler;

public class PowerupSpawnSystem extends EntitySystem {

    public float timeLastSpawned;

    ImmutableArray<Entity> spawners;

    // public static ArrayList<Entity> powerUps;
    private final EntityFactory factory;
    private final World world;

    public PowerupSpawnSystem(Engine engine, EntityFactory factory, World world) {
        spawners = engine.getEntitiesFor(Family.all(PowerupSpawnerComponent.class).get());
        this.factory = factory;
        this.world = world;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeLastSpawned += deltaTime;

        // System.out.println("TEST");
        if (getPowerupSpawnChanceFrame(deltaTime) >= Math.random()) {
            // System.out.println(entity);
            Entity spawner = spawners.get(ThreadLocalRandom.current().nextInt(0, spawners.size()));
            PowerupSpawnerComponent spawnerComponent = spawner.getComponent(PowerupSpawnerComponent.class);
            spawnerComponent.createPowerup(factory);
            timeLastSpawned = 0;
        }
    }

    public float getPowerupSpawnChanceFrame(float delta) {
        return ((timeLastSpawned / getPowerupSpawnTime()) * delta) / 10; // probably worth making the chance exponential
    }

    public int getPowerupSpawnTime() {
        return 5;// Temporary for if i decide to move this to an external class and such for
                 // upgrades
    }

}
