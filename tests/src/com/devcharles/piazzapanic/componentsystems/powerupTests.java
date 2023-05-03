package com.devcharles.piazzapanic.componentsystems;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.Powerups.cookBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.cutBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.speedBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.timeFreezeBoostComponent;
import com.devcharles.piazzapanic.input.KeyboardInput;

public class powerupTests {
    @Test
    public void testPowerupSpawnRate() {
        testEnvironment env = new testEnvironment();
        Engine engine = env.engine;
        PowerupSpawnSystem powerupSystem = new PowerupSpawnSystem(engine, env.factory, env.world);
        assertTrue(powerupSystem.getPowerupSpawnChanceFrame(0.1f) == 0);

        powerupSystem.timeLastSpawned += 20f;

        float x = powerupSystem.getPowerupSpawnChanceFrame(0.1f);
        assertTrue(powerupSystem.getPowerupSpawnChanceFrame(0.1f) == 0.01f);
        assertTrue(powerupSystem.getPowerupSpawnChanceFrame(1f) == 0.1f);

        powerupSystem.timeLastSpawned += 20f;

        assertTrue(powerupSystem.getPowerupSpawnChanceFrame(0.1f) == 0.02f);
        assertTrue(powerupSystem.getPowerupSpawnChanceFrame(1f) == 0.2f);
    }

    @Test
    public void testPowerupExpire() {
        testEnvironment env = new testEnvironment();

        PlayerControlSystem system = new PlayerControlSystem(new KeyboardInput(), env.engine);
        env.engine.addSystem(system);

        Entity chef = env.factory.createCook(0, 0);
        chef.add(new PlayerComponent());
        env.engine.update(1);

        chef.add(new cookBoostComponent());
        chef.add(new cutBoostComponent());
        chef.add(new timeFreezeBoostComponent());
        chef.add(new speedBoostComponent());

        env.engine.update(1);

        assertTrue(chef.getComponent(cookBoostComponent.class).timeHad > 0);
        assertTrue(chef.getComponent(cutBoostComponent.class).timeHad > 0);
        assertTrue(chef.getComponent(timeFreezeBoostComponent.class).timeHad > 0);
        assertTrue(chef.getComponent(speedBoostComponent.class).timeHad > 0);

        env.engine.update(10000);
        env.engine.update(0.1f);

        assertTrue(chef.getComponent(cookBoostComponent.class) == null);
        assertTrue(chef.getComponent(cutBoostComponent.class) == null);
        assertTrue(chef.getComponent(timeFreezeBoostComponent.class) == null);
        assertTrue(chef.getComponent(speedBoostComponent.class) == null);
    }
}
