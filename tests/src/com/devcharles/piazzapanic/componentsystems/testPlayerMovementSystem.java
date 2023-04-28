package com.devcharles.piazzapanic.componentsystems;

import static org.junit.Assert.assertTrue;

import org.junit.*;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.B2dBodyComponent;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;

public class testPlayerMovementSystem {
    @Test
    public void PlayerMoveLeftRight() {
        testEnvironment testEnvironment = new testEnvironment();
        EntityFactory entityFactory = testEnvironment.factory;
        Engine engine = testEnvironment.engine;
        World world = testEnvironment.world;
        Vector2 spawn = testEnvironment.spawnPoints.get(0);
        KeyboardInput keyboard = new KeyboardInput();
        // Initialise control system
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(keyboard, engine);
        engine.addSystem(playerControlSystem);
        // Initialise physics system
        PhysicsSystem physicsSystem = new PhysicsSystem(world);
        engine.addSystem(physicsSystem);

        Entity cook = entityFactory.createCook((int) spawn.x, (int) spawn.y);
        PlayerComponent testPlayerComponent = new PlayerComponent();
        cook.add(testPlayerComponent);
        engine.update(1);

        Vector2 cookPosition = cook.getComponent(B2dBodyComponent.class).body.getPosition();
        assertTrue(cookPosition.x == spawn.x && cookPosition.y == spawn.y);

        // Move left
        keyboard.left = true;
        engine.update(1);
        engine.update(1);

        // Move right
        Vector2 newCookPosition = cook.getComponent(B2dBodyComponent.class).body.getPosition();
        assertTrue(!(newCookPosition.x > spawn.x && newCookPosition.y == spawn.y));
        keyboard.left = false;
        keyboard.right = true;
        engine.update(3); // Check that large ticks are the same effect as multiple small ticks
        Vector2 newCookPosition2 = cook.getComponent(B2dBodyComponent.class).body.getPosition();
        assertTrue(newCookPosition2.x < spawn.x && newCookPosition2.y == spawn.y);

        // Would test return to centre but box2d velocity doesnt change instantly so
        // it's not worth it
    }

    @Test
    public void MoveUpDown() {
        testEnvironment testEnvironment = new testEnvironment();
        EntityFactory entityFactory = testEnvironment.factory;
        Engine engine = testEnvironment.engine;
        World world = testEnvironment.world;
        Vector2 spawn = testEnvironment.spawnPoints.get(0);
        KeyboardInput keyboard = new KeyboardInput();
        // Initialise control system
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(keyboard, engine);
        engine.addSystem(playerControlSystem);
        // Initialise physics system
        PhysicsSystem physicsSystem = new PhysicsSystem(world);
        engine.addSystem(physicsSystem);

        Entity cook = entityFactory.createCook((int) spawn.x, (int) spawn.y);
        PlayerComponent testPlayerComponent = new PlayerComponent();
        cook.add(testPlayerComponent);
        engine.update(1);

        Vector2 cookPosition = cook.getComponent(B2dBodyComponent.class).body.getPosition();
        assertTrue(cookPosition.x == spawn.x && cookPosition.y == spawn.y);

        // Move up
        keyboard.up = true;
        engine.update(1);
        engine.update(1);

        // Move down
        Vector2 newCookPosition = cook.getComponent(B2dBodyComponent.class).body.getPosition();
        assertTrue(!(newCookPosition.x == spawn.x && newCookPosition.y < spawn.y));
        keyboard.up = false;
        keyboard.down = true;
        engine.update(3); // Check that large ticks are the same effect as multiple small ticks
        Vector2 newCookPosition2 = cook.getComponent(B2dBodyComponent.class).body.getPosition();
        assertTrue(newCookPosition2.x == spawn.x && newCookPosition2.y > spawn.y);

        // Would test return to centre but box2d velocity doesnt change instantly so
        // it's not worth it
    }
}
