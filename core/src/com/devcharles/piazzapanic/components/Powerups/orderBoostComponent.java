package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Given to players when they collect an order boost powerup. If there is a waiting customer, it
 * will be used up instantly.
 */
public class orderBoostComponent implements Component, Poolable {
    public boolean used = false;

    public void reset() {
        used = false;
    }
}
