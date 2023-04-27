package com.devcharles.piazzapanic.components;

import com.badlogic.ashley.core.Component;

/**
 * This is just given to entities that can be carried, placed on workstations etc. as items (I
 * think).
 */
public class ItemComponent implements Component {
    // This is some information about an entity carrying this item. I presume it is null if it is
    // not being carried.
    public TransformComponent holderTransform;
}
