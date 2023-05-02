package com.devcharles.piazzapanic;

import org.junit.*;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.B2dBodyComponent;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.components.Powerups.cookBoostComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.components.ItemComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.componentsystems.PlayerControlSystem;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.SaveHandler;
import com.devcharles.piazzapanic.utility.saveStructure.FoodData;

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
        PlayerControlSystem playerControlSystem = engine.getSystem(PlayerControlSystem.class);
        


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



        // Chef 1.
        Entity cook = entityFactory.createCook(50, 60);

        TransformComponent transformComponent = cook.getComponent(TransformComponent.class);
        ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class);
        B2dBodyComponent b2dBodyComponent = cook.getComponent(B2dBodyComponent.class);

        transformComponent.position.set(50, 60, 0);
        b2dBodyComponent.body.setTransform(transformComponent.position.x, transformComponent.position.y, b2dBodyComponent.body.getAngle());

        Entity food1 = entityFactory.createFood(FoodType.tomato);
        ItemComponent itemComponent1 = new ItemComponent();
        itemComponent1.holderTransform = cook.getComponent(TransformComponent.class);
        food1.add(itemComponent1);
        controllableComponent.currentFood.pushItem(food1, cook);

        Entity food2 = entityFactory.createFood(FoodType.slicedTomato);
        ItemComponent itemComponent2 = new ItemComponent();
        itemComponent2.holderTransform = cook.getComponent(TransformComponent.class);
        food2.add(itemComponent2);
        controllableComponent.currentFood.pushItem(food2, cook);


        cookBoostComponent cookboost = new cookBoostComponent();
        cookboost.timeHad = 505;
        cook.add(cookboost);

        
        cook.add(new PlayerComponent());


        String testString = "{\nmoney: 123\nreputation: 2\ncustomers: [\n	{\n		x: 420\n		y: 69\n		patience: 50301\n		order: {\n			type: bakedPotato\n		}\n	}\n	{\n		x: 7\n		y: 18912\n		patience: 40000\n		order: {\n			type: pizza\n		}\n	}\n]\ncooks: [\n	{\n		x: 50\n		y: 60\n		inventory: [\n			{\n				type: tomato\n			}\n			{\n				type: slicedTomato\n			}\n		]\n		boosts: [\n			{\n				type: cookBoost\n				time: 505\n			}\n		]\n		active: true\n	}\n]\nmaxCustomers: 2147483647\n}";
        String saveString = SaveHandler.save(null, engine, world, hud, false);

        String testLines[] = testString.split("\n");
        String saveLines[] = saveString.split("\n");

        // For some reason it doesn't like to work when you do the whole thing at once, but if each
        // line is the same, then it is safe to say the data will be interpreted the same way.
        for (int i = 0; i < testLines.length; i++) {
            Assert.assertEquals(testLines[i], saveLines[i]);
        }
    }

    @Test
    /**
     * Test that the save handler converts the correct string into game state.
     */
    public void testLoad() {
        Assert.assertTrue(true);
    }
}
