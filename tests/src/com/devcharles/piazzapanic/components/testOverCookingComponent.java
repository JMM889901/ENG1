package com.devcharles.piazzapanic.components;

import static org.junit.Assert.assertTrue;

import org.junit.*;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.devcharles.piazzapanic.testEnvironment;
import com.devcharles.piazzapanic.components.FoodComponent.FoodType;
import com.devcharles.piazzapanic.componentsystems.StationSystem;
import com.devcharles.piazzapanic.utility.EntityFactory;
import com.devcharles.piazzapanic.utility.Mappers;
import com.devcharles.piazzapanic.utility.Station.StationType;

public class testOverCookingComponent {
    @Test
    /**
     * Check foods become spoiled if processing too long.
     */
    public void testOverCooking() {
        // Initialise environment
        testEnvironment environment = new testEnvironment();
        PooledEngine engine = (PooledEngine) environment.engine;
        StationSystem stationSystem = new StationSystem(null, environment.factory);
        EntityFactory entityFactory = environment.factory;
        engine.addSystem(stationSystem);

        // Create 2 foods that each require a different type of cooking.
        Entity testPattySuccess = entityFactory.createFood(FoodType.formedPatty); // Grill.
        Entity testPattySpoil = entityFactory.createFood(FoodType.formedPatty);

        Entity testPotatoSuccess = entityFactory.createFood(FoodType.potato); // Oven.
        Entity testPotatoSpoil = entityFactory.createFood(FoodType.potato);

        Entity testTomatoSuccess = entityFactory.createFood(FoodType.tomato); // CuttingBoard
        Entity testTomatoSpoil = entityFactory.createFood(FoodType.tomato);

        // Create a chef to put things on the station.
        Entity testCook = entityFactory.createCook(0, 0);
        ControllableComponent chefComponent = Mappers.controllable.get(testCook);
        chefComponent.currentFood.pushItem(testPattySuccess, testCook);
        chefComponent.currentFood.pushItem(testPattySpoil, testCook);
        chefComponent.currentFood.pushItem(testPotatoSuccess, testCook);
        chefComponent.currentFood.pushItem(testPotatoSpoil, testCook);
        chefComponent.currentFood.pushItem(testTomatoSuccess, testCook);
        chefComponent.currentFood.pushItem(testTomatoSpoil, testCook);

        // Create stations to process food.
        Entity testOvenSuccess = entityFactory.createStation(StationType.oven, new Vector2(1, 0), null, false);
        Entity testOvenSpoil = entityFactory.createStation(StationType.oven, new Vector2(2, 0), null, false);
        Entity testGrillSuccess = entityFactory.createStation(StationType.grill, new Vector2(3, 0), null, false);
        Entity testGrillSpoil = entityFactory.createStation(StationType.grill, new Vector2(4, 0), null, false);
        Entity testCutSuccess = entityFactory.createStation(StationType.cutting_board, new Vector2(5, 0), null, false);
        Entity testCutSpoil = entityFactory.createStation(StationType.cutting_board, new Vector2(6, 0), null, false);

        StationComponent ovenSuccessComponent = Mappers.station.get(testOvenSuccess);
        StationComponent ovenSpoilComponent = Mappers.station.get(testOvenSpoil);
        StationComponent grillSuccessComponent = Mappers.station.get(testGrillSuccess);
        StationComponent grillSpoilComponent = Mappers.station.get(testGrillSpoil);
        StationComponent cutSuccessComponent = Mappers.station.get(testCutSuccess);
        StationComponent cutSpoilComponent = Mappers.station.get(testCutSpoil);

        // Force set the interacting cook as our test cook
        ovenSpoilComponent.interactingCook = testCook;
        grillSpoilComponent.interactingCook = testCook;
        ovenSuccessComponent.interactingCook = testCook;
        grillSuccessComponent.interactingCook = testCook;
        cutSpoilComponent.interactingCook = testCook;
        cutSuccessComponent.interactingCook = testCook;

        // This is used lots of times below, it gets overwritten each time so no worry
        // about leaked references.
        CookingComponent foodCookingComponent;

        // Run the order of putting food on and using stations.

        stationSystem.processStation(chefComponent, cutSpoilComponent);
        stationSystem.processStation(chefComponent, cutSuccessComponent);
        stationSystem.processStation(chefComponent, ovenSpoilComponent);
        stationSystem.processStation(chefComponent, ovenSuccessComponent);
        stationSystem.processStation(chefComponent, grillSpoilComponent);
        stationSystem.processStation(chefComponent, grillSuccessComponent);

        // Check the food timers are initialised correctly
        Assert.assertTrue(Mappers.cooking.get(ovenSpoilComponent.food.get(0)).timer.peakElapsed() == 0);
        Assert.assertTrue(Mappers.cooking.get(ovenSuccessComponent.food.get(0)).timer.peakElapsed() == 0);
        Assert.assertTrue(Mappers.cooking.get(grillSpoilComponent.food.get(0)).timer.peakElapsed() == 0);
        Assert.assertTrue(Mappers.cooking.get(grillSuccessComponent.food.get(0)).timer.peakElapsed() == 0);
        Assert.assertTrue(Mappers.cooking.get(cutSpoilComponent.food.get(0)).timer.peakElapsed() == 0);
        Assert.assertTrue(Mappers.cooking.get(cutSuccessComponent.food.get(0)).timer.peakElapsed() == 0);

        // Tick the stations to start cooking the food.
        engine.update(5.1f);
        // Interact with station as needed
        stationSystem.interactStation(ovenSpoilComponent);
        stationSystem.interactStation(ovenSuccessComponent);
        stationSystem.interactStation(grillSpoilComponent);
        stationSystem.interactStation(grillSuccessComponent);
        stationSystem.interactStation(cutSpoilComponent);
        stationSystem.interactStation(cutSuccessComponent);

        // Quick sanity check to make sure the food is ready to start the second stage
        // of processing.
        foodCookingComponent = Mappers.cooking.get(ovenSuccessComponent.food.get(0));
        Assert.assertTrue(foodCookingComponent.processed);
        Assert.assertTrue(foodCookingComponent.timer.peakElapsed() == 0);
        // Finish cooking the food.

        // stationSystem.stationTick(ovenSpoilComponent, 5f);
        // stationSystem.stationTick(ovenSuccessComponent, 5f);
        // stationSystem.stationTick(grillSpoilComponent, 5f);
        // stationSystem.stationTick(grillSuccessComponent, 5f);

        engine.update(5);

        // Tick twice to give the OverCookingComponent a chance
        // to check whether the food spoils.
        engine.update(0.1f);// starts overcooking after 5 seconds

        // Test that the OverCookingComponent is added to the food

        assertTrue(testPotatoSpoil.getComponent(OvercookingComponent.class) != null);
        assertTrue(testPattySpoil.getComponent(OvercookingComponent.class) != null);
        assertTrue(testPotatoSuccess.getComponent(OvercookingComponent.class) != null);
        assertTrue(testPattySuccess.getComponent(OvercookingComponent.class) != null);
        assertTrue(testTomatoSuccess.getComponent(OvercookingComponent.class) != null);
        assertTrue(testTomatoSpoil.getComponent(OvercookingComponent.class) != null);

        // Pickup 2 of the food items
        stationSystem.stationPickup(ovenSuccessComponent, chefComponent);
        stationSystem.stationPickup(grillSuccessComponent, chefComponent);
        stationSystem.stationPickup(cutSuccessComponent, chefComponent);

        // Tick the stations

        engine.update(5);
        engine.update(0.1f);

        // Check that they are overcooked

        assertTrue(testPotatoSpoil.getComponent(TintComponent.class) != null);
        assertTrue(testPattySpoil.getComponent(TintComponent.class) != null);
        assertTrue(testPotatoSuccess.getComponent(TintComponent.class) == null);
        assertTrue(testPattySuccess.getComponent(TintComponent.class) == null);
        assertTrue(testTomatoSuccess.getComponent(TintComponent.class) == null);
        assertTrue(testTomatoSpoil.getComponent(TintComponent.class) != null);

        // Pick up the remaining food items
        stationSystem.stationPickup(ovenSpoilComponent, chefComponent);
        stationSystem.stationPickup(grillSpoilComponent, chefComponent);

        engine.update(5);
        engine.update(0.1f);
        // Check that they are still spoiled

        assertTrue(testPotatoSpoil.getComponent(TintComponent.class) != null);
        assertTrue(testPattySpoil.getComponent(TintComponent.class) != null);
        assertTrue(testTomatoSpoil.getComponent(TintComponent.class) != null);
        assertTrue(testPotatoSuccess.getComponent(TintComponent.class) == null);
        assertTrue(testPattySuccess.getComponent(TintComponent.class) == null);
        assertTrue(testTomatoSuccess.getComponent(TintComponent.class) == null);

        // Assert.assertTrue(false);
    }
}
