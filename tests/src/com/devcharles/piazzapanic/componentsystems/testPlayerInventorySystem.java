package com.devcharles.piazzapanic.componentsystems;

import org.junit.*;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.StationComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.math.Vector2;

public class testPlayerInventorySystem {
    @Test
    /**
     * Tests if a player can pick up an item from a countertop
     */
    public void testPlayerPickUpItem() {
        // Set up environment.
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        KeyboardInput keyboardInput = new KeyboardInput();

        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(keyboardInput, null);// Do I need to
        StationSystem testStationSystem = new StationSystem(keyboardInput, testEntityFactory);
        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testStationSystem);

        // Create entities.
        Entity chef = testEntityFactory.createCook(0, 0);  // Chef.
        PlayerComponent testPlayerComponent = new PlayerComponent();
        ControllableComponent testControllableComponent = Mappers.controllable.get(chef);
        chef.add(testPlayerComponent);  // Make our chef the active "player".

        Entity testTater = testEntityFactory.createFood(FoodType.potato);  // Potato.

        Entity testCountertop = testEntityFactory.createStation(StationType.counter, new Vector2(0, 0), null, false);  // Station.
        StationComponent countertopComponent = Mappers.station.get(testCountertop);
        countertopComponent.food.set(0, testTater);
        
        // Brief sanity check to make sure the countertop has a potato.
        Assert.assertTrue(testControllableComponent.currentFood.isEmpty());
        Assert.assertFalse(countertopComponent.food.get(0) == null);

        // Pick up the potato.
        testPlayerComponent.pickUp = true;
        countertopComponent.interactingCook = chef;  // This is so that PlayerControlSystem.processEntity() processes the chef made above. 
        testStationSystem.update(1);
        testStationSystem.processEntity(testCountertop, 1);

        Assert.assertTrue(countertopComponent.food.get(0) == null);
        Assert.assertTrue(testControllableComponent.currentFood.peek() == testTater);
    }

    @Test
    public void testPlayerPutDownItem() {
        // TODO, should be quite similar to above. I will do this - Joss
        Assert.assertTrue(false);
    }

}
