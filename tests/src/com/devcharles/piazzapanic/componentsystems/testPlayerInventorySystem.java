package com.devcharles.piazzapanic.componentsystems;

import static org.junit.Assert.assertArrayEquals;

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

    @Test
    public void testChefRememberItems() {
        testEnvironment environment = new testEnvironment();
        // Initialise wider systems.
        PooledEngine engine = (PooledEngine) environment.engine;

        KeyboardInput input = new KeyboardInput();
        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(input, environment.engine);
        engine.addSystem(testPlayerControlSystem);

        // Initialise foods (and the entity factory to make said food).
        EntityFactory testEntityFactory = environment.factory;
        FoodComponent testFoodType = new FoodComponent();

        testFoodType.type = FoodType.unformedPatty; // From the FoodType: unformedPatty(1)
        Entity testFoodEntity = testEntityFactory.createFood(testFoodType.type);

        // Initialise a chef.
        Vector2 spawn = environment.spawnPoints.get(0);
        Entity chef = testEntityFactory.createCook((int) spawn.x, (int) spawn.y);
        spawn = environment.spawnPoints.get(1);
        Entity chef2 = testEntityFactory.createCook((int) spawn.x, (int) spawn.y);

        PlayerComponent testPlayerComponent = new PlayerComponent();
        chef.add(testPlayerComponent);
        engine.update(1);// Engine needs to tick so playercomponent is detected by systems
        testPlayerControlSystem.playerComponent.pickUp = true; // Set the pickUp flag to false, allows chef to pickup
        ControllableComponent testControllableComponent = chef.getComponent(ControllableComponent.class);
        // We need to make the chef actually pick something up! the testFoodEntity we
        // created earlier.

        testPlayerControlSystem.processEntity_test(testFoodEntity, 0);
        testControllableComponent.currentFood.init(engine);
        testControllableComponent.currentFood.pushItem(testFoodEntity, chef);

        input.changeCooks = true; // Change the chef to chef2
        engine.update(1);

        assert (testControllableComponent.currentFood.pop() == testFoodEntity); // chef remembers the food he picked up
                                                                                // when not selected
        testControllableComponent = chef2.getComponent(ControllableComponent.class);
        assert (testControllableComponent.currentFood.size() == 0); // chef2 does not remember the food chef1 picked
                                                                    // up
    }
}
