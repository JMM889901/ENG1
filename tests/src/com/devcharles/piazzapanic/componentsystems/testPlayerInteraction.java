package com.devcharles.piazzapanic.componentsystems;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.Station.StationType;

public class testPlayerInteraction {
    @Test
    /**
     * Test that a player can interact with a Food Station successfully
     */
    public void testPlayerInteractFoodStation() {
        // Create environment
        testEnvironment env = new testEnvironment();
        // Create systems
        KeyboardInput keyboardInput = new KeyboardInput();
        StationSystem stationSystem = new StationSystem(keyboardInput, env.factory);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(keyboardInput, env.engine);
        env.engine.addSystem(playerControlSystem);
        env.engine.addSystem(stationSystem);
        // Create cook
        Entity cook = env.factory.createCook(0, 0);
        PlayerComponent playerComponent = new PlayerComponent(); // Controls for active player
        ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class); // inventory
        cook.add(playerComponent);

        // Create food station
        Entity foodStation = env.factory.createStation(StationType.ingredient, new Vector2(0, 0), FoodType.lettuce,
                false);
        StationComponent station = foodStation.getComponent(StationComponent.class);

        // Interact with food station
        station.interactingCook = cook;
        keyboardInput.pickUp = true;

        env.engine.update(0.1f);

        assertTrue(
                controllableComponent.currentFood.getLast().getComponent(FoodComponent.class).type == FoodType.lettuce);

    }

    /**
     * Test that a player can pick up or put down food on a counter
     */
    @Test
    public void testPlayerInteractCounter() {
        // Counters can store food and have food picked up

        // Create environment
        testEnvironment env = new testEnvironment();
        // Create systems
        KeyboardInput keyboardInput = new KeyboardInput();
        StationSystem stationSystem = new StationSystem(keyboardInput, env.factory);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(keyboardInput, env.engine);
        env.engine.addSystem(playerControlSystem);
        env.engine.addSystem(stationSystem);
        // Create cook
        Entity cook = env.factory.createCook(0, 0);
        PlayerComponent playerComponent = new PlayerComponent(); // Controls for active player
        ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class); // inventory
        cook.add(playerComponent);

        // Give cook food
        Entity food = env.factory.createFood(FoodType.lettuce);
        controllableComponent.currentFood.add(food);

        // Create counter
        Entity counter = env.factory.createStation(StationType.counter, new Vector2(0, 0), null,
                false);
        StationComponent station = counter.getComponent(StationComponent.class);

        // Place on counter
        station.interactingCook = cook;
        keyboardInput.putDown = true;

        env.engine.update(0.1f);

        assertTrue(controllableComponent.currentFood.isEmpty());
        assertTrue(station.food.get(0).getComponent(FoodComponent.class).type == FoodType.lettuce);

        // Pick up from counter
        keyboardInput.putDown = false;
        keyboardInput.pickUp = true;

        env.engine.update(0.1f);

        assertTrue(
                controllableComponent.currentFood.getLast().getComponent(FoodComponent.class).type == FoodType.lettuce);
        assertTrue(station.food.get(0) == null);

    }

    /**
     * Test that a plate combines food into a meal
     */
    @Test
    public void testPlayerServingStation() {
        // Plate compiles food into a meal

        // Create environment
        testEnvironment env = new testEnvironment();
        // Create systems
        KeyboardInput keyboardInput = new KeyboardInput();
        StationSystem stationSystem = new StationSystem(keyboardInput, env.factory);
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(keyboardInput, env.engine);
        env.engine.addSystem(playerControlSystem);
        env.engine.addSystem(stationSystem);

        // Create cook
        Entity cook = env.factory.createCook(0, 0);
        PlayerComponent playerComponent = new PlayerComponent(); // Controls for active player
        ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class); // inventory
        cook.add(playerComponent);

        // Give cook food
        Entity food1 = env.factory.createFood(FoodType.lettuce);
        controllableComponent.currentFood.push(food1);// This should not be used, only checks in order of most recent
        Entity food2 = env.factory.createFood(FoodType.grilledPatty);
        controllableComponent.currentFood.push(food2);
        Entity food3 = env.factory.createFood(FoodType.toastedBuns);
        controllableComponent.currentFood.push(food3);

        // Create plate
        Entity plate = env.factory.createStation(StationType.serve, new Vector2(0, 0), null,
                false);
        StationComponent station = plate.getComponent(StationComponent.class);

        // Place on plate
        station.interactingCook = cook;
        keyboardInput.putDown = true;

        env.engine.update(0.1f);

        assertTrue(
                controllableComponent.currentFood.getFirst().getComponent(FoodComponent.class).type == FoodType.burger);
        assertTrue(controllableComponent.currentFood.getLast()
                .getComponent(FoodComponent.class).type == FoodType.lettuce);// Should not be removed

    }
}
