package com.devcharles.piazzapanic.componentsystems;

import static org.mockito.Mockito.mock;

import org.junit.*;

import com.devcharles.piazzapanic.PiazzaPanic;
import com.devcharles.piazzapanic.emptyGL20;
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
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;

public class testPlayerControlSystem {
    @Test
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
        // testPlayerControlSystem.processEntity_test(chef, 0);
        engine.update(1);
        testPlayerControlSystem.playerComponent.pickUp = true; // Set the pickUp flag to false so it can pick something
                                                               // up

        ControllableComponent testControllableComponent = new ControllableComponent();
        // We need to make the chef actually pick something up! the testFoodEntity we created earlier. 
        testPlayerControlSystem.processEntity_test(testFoodEntity, 0);
        testControllableComponent.currentFood.init(engine);
        testControllableComponent.currentFood.pushItem(testFoodEntity, chef);
        Assert.assertEquals(testControllableComponent.currentFood.pop(), testFoodEntity);
    }
}
