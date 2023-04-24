package com.devcharles.piazzapanic.componentsystems;

import org.junit.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.componentsystems.CarryItemsSystem;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;

// "You look like you've been to a funeral."
// "I tried, but the bugger weren't dead yet."

public class testCarryItemsSystem {
    @Test
    /**
     * Tests if an item follows the player it is being carried by.
     */
    public void testPlayerBringsItem() {
        // Initialise environment
        new testEnvironment();
        PooledEngine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory entityFactory = new EntityFactory(engine, world);

        // Environments required to move the player (and hopefully the carried entity, too).
        KeyboardInput input = new KeyboardInput();
        PlayerControlSystem testPlayerControlSystem = new PlayerControlSystem(input, engine);
        
        // We are testing this.
        CarryItemsSystem testCarryItemsSystem = new CarryItemsSystem();

        engine.addSystem(testPlayerControlSystem);
        engine.addSystem(testCarryItemsSystem);


        // Actually test the thing:

        // Create the player.
        Entity testCook = entityFactory.createCook(0, 0);  // EntityFactory automatically gives this a bunch of components.
        PlayerComponent testPlayerComponent = new PlayerComponent();  // This component just means *this* cook is being controlled.

        // Create food item to carry.
        Entity testIngredient = entityFactory.createFood(FoodType.tomato);

        // Carry the food item.
        // This is emulating some of the code in StationSystem.java, processEntity().
        ControllableComponent controllable = Mappers.controllable.get(testCook);
        controllable.currentFood.pushItem(testIngredient, testCook);  // It is poor software architecture to have to supply testCook twice, but that's how it works.

        // A quick sanity check on the above note on dodgy architecture.
        Assert.assertTrue(Mappers.item.get(testIngredient).holderTransform == Mappers.transform.get(testCook));

        // Move the player.
        TransformComponent transform = Mappers.transform.get(testCook);
        transform.position.x += 5f;  // Just move it 5 to the right.
        testCarryItemsSystem.update(0.1f);  // This is the line that should move the item.

        float dx = Mappers.transform.get(testIngredient).position.x - Mappers.transform.get(testCook).position.x;
        float dy = Mappers.transform.get(testIngredient).position.y - Mappers.transform.get(testCook).position.y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        Assert.assertTrue(distance < 1.01f);  // The item should be within 1 unit of the player.

        /*
         * Basically, what I'm trying to do is test that a player is followed round by an object it carries.
         * 
         * That means create a player
         * create an object
         * get the player to carry the object
         * 
         * move the player
         *  (and then tick the player system or entity system.)
         * test if the object has moved.
         */

        Assert.assertTrue(true);
    }


}
