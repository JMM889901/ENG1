package main.com.devcharles.piazzapanic.components.Powerups;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

public class orderBoostComponent implements Component, Poolable {
    public boolean used = false;

    public void reset() {
        used = false;
    }
}
