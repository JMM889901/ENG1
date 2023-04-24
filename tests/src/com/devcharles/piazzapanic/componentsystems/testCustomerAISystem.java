package com.devcharles.piazzapanic.componentsystems;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.*;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.GdxTimer;
import com.devcharles.piazzapanic.utility.Station;
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

    @Test
    public void testMoneyReward() {
        new testEnvironment();
        Engine engine = new PooledEngine();
        Integer[] money = { 0 };
        Hud.money = money;
        EntityFactory factory = new EntityFactory((PooledEngine) engine);
        CustomerAISystem system = new CustomerAISystem(null, null, factory, null, null);
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
}
