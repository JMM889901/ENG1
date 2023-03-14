package com.devcharles.piazzapanic.componentsystems;
import org.junit.*;

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



public class testPlayerControlSystem {
    @Test
    public void testPlayerPickUpItem(){
        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(new KeyboardInput(), null); // Do I need to add a new Pooledengine here?
        PlayerComponent testPlayerComponent = new PlayerComponent();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);

        EntityFactory testEntityFactory = new EntityFactory(engine, world);
        
        FoodComponent testFoodType = new FoodComponent();
        testFoodType.type = FoodType.unformedPatty; // From the FoodType: unformedPatty(1)
        Entity testFoodEntity = testEntityFactory.createFood(testFoodType.type);
        testPlayerControlSystem.playerComponent.pickUp = false; // Set the pickUp flag to false so it can pick something up
        testPlayerControlSystem.processEntity(testFoodEntity, 0);
        ControllableComponent testControllableComponent = new ControllableComponent();
        Assert.assertEquals(testControllableComponent.currentFood.pop(), testFoodType);
    }
}
