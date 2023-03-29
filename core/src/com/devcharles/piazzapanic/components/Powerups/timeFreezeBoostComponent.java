package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Given to players while the rest of time stops ticking around them.
 * Given in {@link componentsystems.PowerupSpawnSystem}
 */
public class timeFreezeBoostComponent implements Component, Poolable {
    public static float timeMax = 5;
    public float timeHad = 0;

    public void reset() {
        timeHad = 0;
    }
}
