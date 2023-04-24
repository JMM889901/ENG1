package com.devcharles.piazzapanic.componentsystems;

import org.junit.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.componentsystems.CarryItemsSystem;
import com.devcharles.piazzapanic.input.KeyboardInput;
import com.devcharles.piazzapanic.utility.EntityFactory;

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
        
        ////// TODO: IDONTFUCKINGKNOW. I'm still working on this right now.

        // We are testing this.
        CarryItemsSystem testCarryItemsSystem = new CarryItemsSystem();

        // Create the player.
        Entity testCook = entityFactory.createCook(0, 0);
        PlayerComponent testPlayerComponent = new PlayerComponent();

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
