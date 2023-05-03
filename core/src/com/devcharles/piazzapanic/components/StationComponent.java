package com.devcharles.piazzapanic.components;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.utility.Station.StationType;
import com.devcharles.piazzapanic.utility.Station.itemDisplayDir;

/**
 * Component given to all stations placed around the level.
 * <p>
 * Contains some miscellaneous information about behaviour and rendering.
 */
public class StationComponent implements Component {
    public Entity interactingCook = null;  // Which cook is bumping into this station.

    // Whether it's an oven, countertop etc.
    public StationType type;
    
    // Which way the "front" of the station is facing, this only affects where abouts food is rendered on top of the station.
    // Needed for effective implementation of FR_COUNTER
    public itemDisplayDir direction;

    // "List" of food in the station's inventory.
    public ArrayList<Entity> food = new ArrayList<Entity>(Arrays.asList(new Entity[] { null, null, null, null }));
    
    // This is only used for ingredient stations.
    public FoodType ingredient = null;
}
