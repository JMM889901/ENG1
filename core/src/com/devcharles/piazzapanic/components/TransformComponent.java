package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Component for things that have, in their own right, a position, scale, and rotation.
 * (Everything with ItemComponent has this, for example.)
 */
public class TransformComponent implements Component {
    public final Vector3 position = new Vector3();
    public final Vector2 scale = new Vector2(1,1);
    public float rotation = 0.0f;
    public boolean isHidden = false;
    public boolean isMoving = false;
}
