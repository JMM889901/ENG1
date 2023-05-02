package com.devcharles.piazzapanic.componentsystems;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.*;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.scene2d.TestHud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.GdxTimer;
import com.devcharles.piazzapanic.utility.MapLoader;
import com.devcharles.piazzapanic.utility.Station;

import box2dLight.RayHandler;

import com.devcharles.piazzapanic.emptyGL20;
import com.devcharles.piazzapanic.testEnvironment;

public class testCustomerAISystem {
    @Test
    /**
     * Test that when an order isn't fulfilled within the time limit
     * the reputation decreases by 1
     */
    public void testReputationDamage() {
        new testEnvironment();
        Engine engine = new PooledEngine();
        Integer[] reputation = { 4 };
        CustomerAISystem system = new CustomerAISystem(null, null, null, null, reputation);
        engine.addSystem(system);
        Entity customer = new Entity();
        CustomerComponent component = new CustomerComponent();
        component.timer.start();
        customer.add(component);
        customer.add(new AIAgentComponent());
        system.forceTick(customer, 1.0f); // Delay of 90000 so should not lose reputation
        assert (reputation[0] == 4);
        system.forceTick(customer, 100000f); // Should lose reputation
        assert (reputation[0] == 3);
        system.forceTick(customer, 100000f);// Should only lose reputation once per customer
        assert (reputation[0] == 3);
    }

    /** 
     * Test that the player gets money for successfully fulfilling an order.
     */
    @Test
    public void testMoneyReward() {
        new testEnvironment();
        Engine engine = new PooledEngine();
        Integer[] money = { 0 };
        Hud.setMoneyReference_TEST(money);
        Hud hud = new Hud(null, null, null, null, money, null, null);
        EntityFactory factory = new EntityFactory((PooledEngine) engine);
        CustomerAISystem system = new CustomerAISystem(null, null, factory, hud, null);
        engine.addSystem(system);
        Entity customer = new Entity();
        CustomerComponent component = new CustomerComponent();
        customer.add(new AIAgentComponent());
        FoodType[] s = new FoodType[Station.serveRecipes.values().size()];
        s = Station.serveRecipes.values().toArray(s);

        int orderIndex = ThreadLocalRandom.current().nextInt(0, s.length);

        component.order = FoodType.from(s[orderIndex].getValue());
        customer.add(component);
        engine.addEntity(customer);
        system.autoFulfillOrder(customer);
        assert (money[0] > 0); // Should give money for order
    }

    /** 
     * Test that the AI pathfinding works properly
     */
    @Test
    public void testAiPathing() {
        // Gdx setup
        new testEnvironment();
        // Create engines and such
        Engine engine = new PooledEngine();
        World world = new World(new Vector2(0, 0), true);
        EntityFactory factory = new EntityFactory((PooledEngine) engine, world);

        MapLoader loader = new MapLoader(null, null, factory);
        // Strip lighting from world because no gl20
        MapObjects objects = loader.map.getLayers().get("MapObjects").getObjects();
        for (MapObject mapObject : objects) {
            MapProperties properties = mapObject.getProperties();
            if (properties.containsKey("lightID")) {
                properties.remove("lightID");
            }
        }
        // Build world
        loader.buildCollisions(world);

        loader.buildFromObjects(engine, null);
        CustomerAISystem system = new CustomerAISystem(loader.getObjectives(), null, factory, null, null);
        engine.addSystem(system);

        Entity customer = factory.createCustomer(new Vector2(0, 0));
        AIAgentComponent AiComponent = customer.getComponent(AIAgentComponent.class);
        customer.add(AiComponent);
        assert (AiComponent.currentObjective == 0);
        system.makeItGoThere(AiComponent, 1);
        assert (AiComponent.currentObjective == 1);
        assert (AiComponent.steeringBody.getOrientation() != 0);
    }

    /**
     * Test that the customer can take the order. 
     */
    @Test
    public void testCustomerTakesOrder() {
        // Create environment
        testEnvironment env = new testEnvironment();
        env.loader.buildFromObjects(env.engine, null);// Build objectives
        // Create ai system
        CustomerAISystem system = new CustomerAISystem(env.loader.getObjectives(), env.world, env.factory,
                new TestHud(), null);
        env.engine.addSystem(system);

        // Create customer and set order
        Entity customer = env.factory.createCustomer(new Vector2(0, 0));
        CustomerComponent component = customer.getComponent(CustomerComponent.class);
        component.order = FoodType.bakedPotato;

        // Create chef and give him a baked potato
        Entity chef = env.factory.createCook(0, 1);
        Entity food = env.factory.createFood(FoodType.bakedPotato);
        ControllableComponent player = chef.getComponent(ControllableComponent.class);
        player.currentFood.push(food);

        // get playercomponent
        PlayerComponent playerComponent = env.engine.createComponent(PlayerComponent.class);
        chef.add(playerComponent);
        playerComponent.giveToCustomer = true;

        // Make customer take order
        component.interactingCook = chef;
        env.engine.update(0.1f);

        assertTrue(player.currentFood.isEmpty());
        assertTrue(component.order == null);
        assertTrue(component.food == food);

    }

    /**
     * Test that the cusotmer can reject an incorrect order.
     */
    @Test
    public void testCustomerRejectsOrder() {
        // Create environment
        testEnvironment env = new testEnvironment();
        env.loader.buildFromObjects(env.engine, null);// Build objectives
        // Create ai system
        CustomerAISystem system = new CustomerAISystem(env.loader.getObjectives(), env.world, env.factory,
                new TestHud(), null);
        env.engine.addSystem(system);

        // Create customer and set order
        Entity customer = env.factory.createCustomer(new Vector2(0, 0));
        CustomerComponent component = customer.getComponent(CustomerComponent.class);
        component.order = FoodType.bakedPotato;

        // Create chef and give him a baked potato
        Entity chef = env.factory.createCook(0, 1);
        Entity food = env.factory.createFood(FoodType.burger);
        ControllableComponent player = chef.getComponent(ControllableComponent.class);
        player.currentFood.push(food);

        // get playercomponent
        PlayerComponent playerComponent = env.engine.createComponent(PlayerComponent.class);
        chef.add(playerComponent);
        playerComponent.giveToCustomer = true;

        // Make customer take order
        component.interactingCook = chef;
        env.engine.update(0.1f);

        assertTrue(!player.currentFood.isEmpty());
        assertTrue(component.order != null);
        assertTrue(component.food == null);
    }

    /**
     * Test that gamers can change the difficulty settings. 
     */
    @Test
    public void testDifficulty() {
        // Create environment
        testEnvironment env = new testEnvironment();
        env.loader.buildFromObjects(env.engine, null);// Build objectives
        // Create ai system
        CustomerAISystem system = new CustomerAISystem(env.loader.getObjectives(), env.world, env.factory,
                new TestHud(), new Integer[] { 0 });
        env.engine.addSystem(system);
        // Set difficulty
        CustomerAISystem.setDifficulty(0);

        assertTrue(CustomerAISystem.MaxGroupSize == 1);
        assertTrue(CustomerAISystem.SpawnTime == 30000);
        assertTrue(CustomerAISystem.SpawnRampTime == 300);

        CustomerAISystem.setDifficulty(1);

        assertTrue(CustomerAISystem.MaxGroupSize == 2);
        assertTrue(CustomerAISystem.SpawnTime == 20000);
        assertTrue(CustomerAISystem.SpawnRampTime == 200);

        CustomerAISystem.setDifficulty(2);

        assertTrue(CustomerAISystem.MaxGroupSize == 3);
        assertTrue(CustomerAISystem.SpawnTime == 10000);
        assertTrue(CustomerAISystem.SpawnRampTime == 100);
    }
}
