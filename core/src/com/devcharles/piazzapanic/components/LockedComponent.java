package com.devcharles.piazzapanic.components;

import java.util.ArrayList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.devcharles.piazzapanic.utility.Station.StationType;

public class LockedComponent implements Component {

    public static ArrayList<Entity> lockedStations = new ArrayList<Entity>();
    // Dummy compoenent to lock a station entity from being used
}
