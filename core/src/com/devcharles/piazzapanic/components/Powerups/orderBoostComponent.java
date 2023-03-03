package com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class orderBoostComponent implements Component, Poolable {
    // These numbers don't mean much for something that happens instantaneously when picked up.
    public static float timeMax = 1;
    public float timeHad = 0;

    public void reset() {
        timeHad = 0;
    }
}
