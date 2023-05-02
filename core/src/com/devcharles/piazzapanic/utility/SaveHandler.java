package com.devcharles.piazzapanic.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.B2dBodyComponent;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.ItemComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.components.Powerups.PowerupComponent;
import com.devcharles.piazzapanic.components.Powerups.cookBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.cutBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.speedBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.timeFreezeBoostComponent;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.componentsystems.PlayerControlSystem;
import com.devcharles.piazzapanic.scene2d.Hud;
import com.devcharles.piazzapanic.utility.saveStructure.BoostData;
import com.devcharles.piazzapanic.utility.saveStructure.CookData;
import com.devcharles.piazzapanic.utility.saveStructure.CustomerData;
import com.devcharles.piazzapanic.utility.saveStructure.FoodData;
import com.devcharles.piazzapanic.utility.saveStructure.SaveData;

/**
 * 
 * We need to save:
 * - Money
 * - Reputation
 * - Stations unlocked.
 * - Customers and what they want
 * - Game time
 * - What's on each station.
 * - How processed each item in a station is.
 * 
 * Core implementation of FR_SAVE_FILES.
 */
public class SaveHandler {
    public final static String SAVE_FILE = "save1.json";

    /**
     * Save key elements of game state to a file.
     * @param filename Which file to save to.
     * @param engine The engine with all game systems in.
     * @param world The Box2D world.
     * @param hud Display and global values such as money, customer reputation.
     * @param affectFiles Set to false to do a "dry run".
     * @return JSON formatted string that was written in to the file.
     */
    public static String save(String filename, Engine engine, World world, Hud hud, boolean affectFiles) {

        SaveData saveData = new SaveData();

        // Global values.

        saveData.money = Hud.getMoney();
        saveData.reputation = Hud.reputation[0];
        saveData.difficulty = CustomerAISystem.getDifficulty();
        saveData.maxCustomers = CustomerAISystem.getMaxCustomers();
        saveData.gameTime = hud.customerTimer;

        // Customers.

        ImmutableArray<Entity> customers = engine.getEntitiesFor(Family.all(CustomerComponent.class).get());

        saveData.customers = new CustomerData[customers.size()];

        for (int i = 0; i < customers.size(); i++) {
            Entity customer = customers.get(i);
            CustomerData customerData = new CustomerData();
            CustomerComponent customerComponent = customer.getComponent(CustomerComponent.class);
            TransformComponent transformComponent = customer.getComponent(TransformComponent.class);
            AIAgentComponent aiAgentComponent = customer.getComponent(AIAgentComponent.class);

            FoodData foodData = new FoodData();
            foodData.type = customerComponent.order;

            customerData.x = transformComponent.position.x;
            customerData.y = transformComponent.position.y;
            customerData.patience = customerComponent.timer.peakElapsed();
            customerData.objective = aiAgentComponent.currentObjective;
            customerData.order = foodData;

            saveData.customers[i] = customerData;
        }

        // Chefs/cooks.

        ImmutableArray<Entity> cooks = engine.getEntitiesFor(Family.all(ControllableComponent.class).get());

        saveData.cooks = new CookData[cooks.size()];

        for (int i = 0; i < cooks.size(); i++) {

            Entity cook = cooks.get(i);
            CookData cookData = new CookData();
            ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class);
            TransformComponent transformComponent = cook.getComponent(TransformComponent.class);

            cookData.x = transformComponent.position.x;
            cookData.y = transformComponent.position.y;

            cookData.inventory = new FoodData[controllableComponent.currentFood.size()];
            int j = controllableComponent.currentFood.size() - 1; // A bit scuffed but you can only peak through a Deque
                                                                  // with a for each loop, so we need a separate
                                                                  // increment.
            for (Entity currentFood : controllableComponent.currentFood) {
                FoodData foodData = new FoodData();
                foodData.type = currentFood.getComponent(FoodComponent.class).type;
                cookData.inventory[j] = foodData;
                j--;
            }

            ArrayList<BoostData> boostsData = new ArrayList<BoostData>();
            cookBoostComponent cookboost = cook.getComponent(cookBoostComponent.class);
            if (cookboost != null) {
                BoostData cookBoostData = new BoostData();
                cookBoostData.type = PowerupComponent.powerupType.cookBoost;
                cookBoostData.time = cookboost.timeHad;
                boostsData.add(cookBoostData);
            }
            cutBoostComponent cutboost = cook.getComponent(cutBoostComponent.class);
            if (cutboost != null) {
                BoostData cutBoostData = new BoostData();
                cutBoostData.type = PowerupComponent.powerupType.cutBoost;
                cutBoostData.time = cutboost.timeHad;
                boostsData.add(cutBoostData);
            }
            speedBoostComponent speedboost = cook.getComponent(speedBoostComponent.class);
            if (speedboost != null) {
                BoostData speedBoostData = new BoostData();
                speedBoostData.type = PowerupComponent.powerupType.speedBoost;
                speedBoostData.time = speedboost.timeHad;
                boostsData.add(speedBoostData);
            }
            timeFreezeBoostComponent timefreezeboost = cook.getComponent(timeFreezeBoostComponent.class);
            if (timefreezeboost != null) {
                BoostData timeFreezeBoostData = new BoostData();
                timeFreezeBoostData.type = PowerupComponent.powerupType.timeFreezeBoost;
                timeFreezeBoostData.time = timefreezeboost.timeHad;
                boostsData.add(timeFreezeBoostData);
            }
            cookData.boosts = boostsData.toArray(new BoostData[boostsData.size()]);

            cookData.active = cook.getComponent(PlayerComponent.class) != null;
            /*if (cookData.active) {
                engine.getSystem(PlayerControlSystem.class).playerComponent = cooks.get(0)
                        .getComponent(PlayerComponent.class);
                engine.getSystem(PlayerControlSystem.class).hasInitComponent = true;
            }*/

            saveData.cooks[i] = cookData;
        }

        // Take the structured data and now save it.

        Json json = new Json();
        String saveText = json.prettyPrint(saveData);


        if(affectFiles) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
                writer.write(saveText);
                writer.close();
                System.out.println("Saved to " + filename);
            } catch (Exception e) {
                System.out.println("Error saving to " + filename);
            }
        }

        return saveText;
    }

    /**
     * Complete loading actions from a json String.
     * @param saveText JSON formatted string with save data.
     * @param engine The engine with the game systems in.
     * @param entityFactory Produce entities from this.
     * @param world The Box2D world.
     * @param hud Display and global values such as money, customer reputation.
     */
    public static void loadFromString(String saveText, Engine engine, EntityFactory entityFactory, World world, Hud hud) {
        Json json = new Json();
        SaveData saveData = json.fromJson(SaveData.class, saveText);

        // Global values.

        hud.initMoney(saveData.money);
        // hud.addMoney(saveData.money);
        Hud.reputation[0] = saveData.reputation;
        CustomerAISystem.setDifficulty(saveData.difficulty);
        CustomerAISystem
                .setMaxCustomers(saveData.maxCustomers == -1 ? (int) Double.POSITIVE_INFINITY : saveData.maxCustomers);
        hud.customerTimer = saveData.gameTime;

        CustomerAISystem customerAISystem = engine.getSystem(CustomerAISystem.class);
        customerAISystem.firstSpawn = false;

        // Customers.

        // Clear out existing customers (and also consequently their orders).
        for (Entity customer : engine.getEntitiesFor(Family.all(CustomerComponent.class).get())) {
            engine.getSystem(CustomerAISystem.class).customers.remove(customer);
            engine.removeEntity(Mappers.customer.get(customer).food);
            world.destroyBody(Mappers.b2body.get(customer).body);
            engine.removeEntity(customer);
        }

        // Create new customers.
        for (CustomerData customerData : saveData.customers) {
            Entity customer = entityFactory.createCustomer(new Vector2(customerData.x, customerData.y));

            CustomerComponent customerComponent = customer.getComponent(CustomerComponent.class);
            AIAgentComponent aiAgentComponent = customer.getComponent(AIAgentComponent.class);

            customerComponent.timer.setElapsed(customerData.patience);
            engine.getSystem(CustomerAISystem.class).makeItGoThere(aiAgentComponent, customerData.objective);
            customerComponent.order = customerData.order.type;

            // Code duplicated from CustomerAISystem.spawnCustomer().
            customerAISystem.customers.add(customer);
            customerAISystem.numOfCustomerTotal++;

            if (customerData.objective != -1) {
                customerAISystem.numActiveCustomers++;
            }

            customerComponent.timer.start();
            customerComponent.timer.setElapsed(customerData.patience);

            customerAISystem.makeItGoThere(aiAgentComponent, customerData.objective);
        }

        // Cooks.

        // Make the existing 3 cooks match the first 3 of the save data - it's a bit
        // scuffed but I'm a bit scared to be the first to write code to *delete* cooks.
        int i = 0;
        for (Entity cook : engine.getEntitiesFor(Family.all(ControllableComponent.class).get())) {
            cook.remove(PlayerComponent.class); // Just make sure you don't get lots of cooks controllable
                                                // simultaneously.

            CookData cookData = saveData.cooks[i];

            TransformComponent transformComponent = cook.getComponent(TransformComponent.class);
            ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class);
            B2dBodyComponent b2dBodyComponent = cook.getComponent(B2dBodyComponent.class);

            transformComponent.position.set(cookData.x, cookData.y, transformComponent.position.z);
            b2dBodyComponent.body.setTransform(transformComponent.position.x, transformComponent.position.y, b2dBodyComponent.body.getAngle());


            for (FoodData foodData : cookData.inventory) {
                Entity food = entityFactory.createFood(foodData.type);
                ItemComponent itemComponent = new ItemComponent();
                itemComponent.holderTransform = cook.getComponent(TransformComponent.class);
                food.add(itemComponent);

                controllableComponent.currentFood.pushItem(food, cook);
            }

            for (BoostData boostData : cookData.boosts) {
                switch (boostData.type) {
                    case cookBoost:
                        cookBoostComponent cookboost = new cookBoostComponent();
                        cookboost.timeHad = boostData.time;
                        cook.add(cookboost);
                        break;
                    case cutBoost:
                        cutBoostComponent cutboost = new cutBoostComponent();
                        cutboost.timeHad = boostData.time;
                        cook.add(cutboost);
                        break;
                    case speedBoost:
                        speedBoostComponent speedboost = new speedBoostComponent();
                        speedboost.timeHad = boostData.time;
                        cook.add(speedboost);
                        break;
                    case timeFreezeBoost:
                        timeFreezeBoostComponent timefreezeboost = new timeFreezeBoostComponent();
                        timefreezeboost.timeHad = boostData.time;
                        cook.add(timefreezeboost);
                        break;
                    case orderBoost:
                        System.out.println(
                                "Whatthefuck.                                (Somehow tried to load in a chef with an active orderboost???)");
                }
            }

            if (cookData.active) {
                cook.add(new PlayerComponent());
            }
            i++;
        } // I LOVE CODE DUPLICATION.
        for (; i < saveData.cooks.length; i++) {
            CookData cookData = saveData.cooks[i];
            Entity cook = entityFactory.createCook((int) cookData.x, (int) cookData.y);

            TransformComponent transformComponent = cook.getComponent(TransformComponent.class);
            ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class);
            B2dBodyComponent b2dBodyComponent = cook.getComponent(B2dBodyComponent.class);

            transformComponent.position.set(cookData.x, cookData.y, transformComponent.position.z);
            b2dBodyComponent.body.setTransform(transformComponent.position.x, transformComponent.position.y, b2dBodyComponent.body.getAngle());

            for (FoodData foodData : cookData.inventory) {
                Entity food = entityFactory.createFood(foodData.type);
                ItemComponent itemComponent = new ItemComponent();
                itemComponent.holderTransform = cook.getComponent(TransformComponent.class);
                food.add(itemComponent);

                controllableComponent.currentFood.pushItem(food, cook);
            }

            for (BoostData boostData : cookData.boosts) {
                switch (boostData.type) {
                    case cookBoost:
                        cookBoostComponent cookboost = new cookBoostComponent();
                        cookboost.timeHad = boostData.time;
                        cook.add(cookboost);
                        break;
                    case cutBoost:
                        cutBoostComponent cutboost = new cutBoostComponent();
                        cutboost.timeHad = boostData.time;
                        cook.add(cutboost);
                        break;
                    case speedBoost:
                        speedBoostComponent speedboost = new speedBoostComponent();
                        speedboost.timeHad = boostData.time;
                        cook.add(speedboost);
                        break;
                    case timeFreezeBoost:
                        timeFreezeBoostComponent timefreezeboost = new timeFreezeBoostComponent();
                        timefreezeboost.timeHad = boostData.time;
                        cook.add(timefreezeboost);
                        break;
                    case orderBoost:
                        System.out.println(
                                "Whatthefuck.                                (Somehow tried to load in a chef with an active orderboost???)");
                }
            }

            if (cookData.active) {
                cook.add(new PlayerComponent());
            }
        }
    }

    /**
     * Load key elements of game state from a file.
     * @param filename The file to load from.
     * @param engine The engine with the game systems in.
     * @param entityFactory Produce entities from this.
     * @param world The Box2D world.
     * @param hud Display and global values such as money, customer reputation.
     * @return The text loaded from the file.
     */
    public static void load(String filename, Engine engine, EntityFactory entityFactory, World world, Hud hud) {
        String saveText = "";

        // Read structured data from the save file.

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String curLine;
            curLine = reader.readLine();
            while (curLine != null) {
                saveText += curLine + "\n";
                curLine = reader.readLine();
            }
            reader.close();
            System.out.println("Loaded from " + filename);
        } catch (Exception e) {
            System.out.println("Error loading from " + filename);
        }
        
        loadFromString(saveText, engine, entityFactory, world, hud);
    }
}
