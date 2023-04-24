package com.devcharles.piazzapanic.componentsystems;

import org.junit.*;
import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.utility.GdxTimer;
import com.devcharles.piazzapanic.testEnvironment;

public class testCustomerAISystem {
    @Test
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
}
