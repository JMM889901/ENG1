package com.devcharles.piazzapanic.componentsystems;

import static org.junit.Assert.assertArrayEquals;

import org.junit.*;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;
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
        new testEnvironment();
        // Initialise wider systems.
        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(new KeyboardInput(), null);// Do I need to
        // add a new PooledEngine here?
        PlayerComponent testPlayerComponent = new PlayerComponent();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        engine.addSystem(testPlayerControlSystem);
        // Initialise foods (and the entity factory to make said food).
        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        FoodComponent testFoodType = new FoodComponent();
        testFoodType.type = FoodType.unformedPatty; // From the FoodType: unformedPatty(1)
        Entity testFoodEntity = testEntityFactory.createFood(testFoodType.type);
        // Initialise a chef.
        Entity chef = testEntityFactory.createCook(0, 0);
        chef.add(testPlayerComponent);
        engine.update(1);
        // TODO - add a countertop to pick the item up from.
        testPlayerControlSystem.playerComponent.pickUp = true; // Set the pickUp flag to false, allows chef to pickup
        ControllableComponent testControllableComponent = new ControllableComponent();
        // We need to make the chef actually pick something up! the testFoodEntity we
        // created earlier.
        testPlayerControlSystem.processEntity_test(testFoodEntity, 0);
        testControllableComponent.currentFood.init(engine);
        testControllableComponent.currentFood.pushItem(testFoodEntity, chef);
        Assert.assertEquals(testControllableComponent.currentFood.pop(), testFoodEntity);
    }

    @Test
    public void testPlayerPutDownItem() {

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
