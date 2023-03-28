package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.devcharles.piazzapanic.utility.GdxTimer;

/**
 * Component for a food/ingredient that can be cooked/processed.
 * <p>
 * This component is removed when the food is cooked or processed.
 * <p>
 * See {@link com.devcharles.piazzapanic.componentsystems.StationSystem#stationTick(StationComponent,
 * float) StationSystem.java} for details.
 */
public class CookingComponent implements Component, Poolable {
    public GdxTimer timer = new GdxTimer(5000, false, false);  // We may want to tweak this number for difficulty levels.
    /**
     * If patty is flipped, onion is chopped, etc.
     */
    public boolean processed = false;

    @Override
    public void reset() {
        timer.stop();
        timer.reset();
        processed = false;
    }
}
