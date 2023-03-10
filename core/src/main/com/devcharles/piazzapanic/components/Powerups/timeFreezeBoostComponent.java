package main.com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/*
 * Searched for in Unimplemented
 * Given in {@link componentsystems.PowerupSpawnSystem}
 */
public class timeFreezeBoostComponent implements Component, Poolable {
    public static float timeMax = 5;
    public float timeHad = 0;

    public void reset() {
        timeHad = 0;
    }
}
