package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;

/**
 * This is just given to entities that can be placed on workstations etc. as items (I think).
 */
public class ItemComponent implements Component {
    public TransformComponent holderTransform;
}
