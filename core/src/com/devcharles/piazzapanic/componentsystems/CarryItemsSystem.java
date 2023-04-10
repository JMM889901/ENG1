package com.devcharles.piazzapanic.componentsystems;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector3;
import com.devcharles.piazzapanic.components.ItemComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.WalkAnimator;
import com.devcharles.piazzapanic.utility.WalkAnimator.Direction;

/**
 * This is just a system that runs every frame to update the position of carried items to follow
 * their carrier.
 */
public class CarryItemsSystem extends IteratingSystem {

    public CarryItemsSystem() {
        super(Family.all(ItemComponent.class, TransformComponent.class).get());
    }

    Map<Direction, Vector3> dirToVector = new HashMap<Direction, Vector3>() {
        {
            put(Direction.down, new Vector3(0, -0.5f, 0));
            put(Direction.up, new Vector3(0, 0.5f, 1));
            put(Direction.left, new Vector3(-1, 0, 1));
            put(Direction.right, new Vector3(1, 0, 1));
        }
    };

    /**
     * Processing an item being carried (this will have ItemComponent and TransformComponent) just
     * involves setting its position and location to wherever the thing carrying it is.
     * 
     * @param entity An entity being carried.
     */
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        // We have an item being carried around (ie entity).
        
        ItemComponent item = Mappers.item.get(entity);  // Get the item-y stuff (including data on who's carrying it).
        TransformComponent transform = Mappers.transform.get(entity);

        // item.holderTransform is the transform data about the holder (ie holder's location and rotation).
        //                                                         vvvvvvvvvvvvvvvvvvvv
        Direction cookDirection = WalkAnimator.rotationToDirection(item.holderTransform.rotation);
        // Get the carrier's rotation (float) then converts it with .rotationToDirection() to (up,
        // down, left, right).

        Vector3 directionVector = dirToVector.get(cookDirection).cpy();
        // (up, down, left, right) to a direction vector, as written just below the constructor, in this file.

        transform.position.set(item.holderTransform.position.cpy().add(directionVector.scl(1)));
    }

}
