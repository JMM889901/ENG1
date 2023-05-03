package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Component for things that interact with physics.
 */
public class B2dBodyComponent implements Component  {
    public Body body;
}
