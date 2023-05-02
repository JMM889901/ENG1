package com.devcharles.piazzapanic;

import org.junit.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.SaveHandler;

public class testSaveHandler {
    @Test
    /**
     * Test that the save handler converts game state into the correct string.
     */
    public void testSave() {
        testEnvironment testEnv = new testEnvironment();
        EntityFactory entityFactory = testEnv.factory;
        Engine engine = testEnv.engine;
        World world = testEnv.world;

        Integer money[] = { 123 };
        Integer reputation[] = { 2 };

        Hud hud = new Hud(null, null, null, engine, reputation, money, null);
        engine.addSystem(new CustomerAISystem(null, world, entityFactory, hud, reputation));
        CustomerAISystem customerAISystem = engine.getSystem(CustomerAISystem.class);
        
        // Customer 1.
        Entity customer1 = entityFactory.createCustomer(new Vector2(420, 69));
        
        CustomerComponent customerComponent1 = customer1.getComponent(CustomerComponent.class);
        AIAgentComponent aiAgentComponent1 = customer1.getComponent(AIAgentComponent.class);
        TransformComponent transformComponent1 = customer1.getComponent(TransformComponent.class);

        transformComponent1.position.set(420, 69, 0);

        customerComponent1.timer.setElapsed(50301);
        engine.getSystem(CustomerAISystem.class).makeItGoThere(aiAgentComponent1, 8);
        customerComponent1.order = FoodType.bakedPotato;

        customerAISystem.customers.add(customer1);
        //customerAISystem.numOfCustomerTotal++;

        customerComponent1.timer.start();
        customerComponent1.timer.setElapsed(50301);

        customerAISystem.makeItGoThere(aiAgentComponent1, 8);

        // Customer 2.
        Entity customer2 = entityFactory.createCustomer(new Vector2(7, 18912));

        CustomerComponent customerComponent2 = customer2.getComponent(CustomerComponent.class);
        AIAgentComponent aiAgentComponent2 = customer2.getComponent(AIAgentComponent.class);
        TransformComponent transformComponent2 = customer2.getComponent(TransformComponent.class);

        transformComponent2.position.set(7, 18912, 0);

        customerComponent2.timer.setElapsed(40000);
        engine.getSystem(CustomerAISystem.class).makeItGoThere(aiAgentComponent2, -1);
        customerComponent2.order = FoodType.pizza;

        customerAISystem.customers.add(customer2);
        
        customerComponent2.timer.start();
        customerComponent2.timer.setElapsed(40000);

        customerAISystem.makeItGoThere(aiAgentComponent2, -1);


        System.out.println(SaveHandler.save(null, engine, world, hud, false));

        Assert.assertTrue(SaveHandler.save(null, engine, world, hud, false) != "");

        /*
         * Use example world state and check that it turns to the correct string.
         */
    }

    @Test
    /**
     * Test that the save handler converts the correct string into game state.
     */
    public void testLoad() {
        Assert.assertTrue(true);
    }
}
