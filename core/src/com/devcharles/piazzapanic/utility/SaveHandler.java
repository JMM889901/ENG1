package com.devcharles.piazzapanic.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.devcharles.piazzapanic.components.AIAgentComponent;
import com.devcharles.piazzapanic.components.ControllableComponent;
import com.devcharles.piazzapanic.components.CustomerComponent;
import com.devcharles.piazzapanic.components.FoodComponent;
import com.devcharles.piazzapanic.components.PlayerComponent;
import com.devcharles.piazzapanic.components.TransformComponent;
import com.devcharles.piazzapanic.components.Powerups.PowerupComponent;
import com.devcharles.piazzapanic.components.Powerups.cookBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.cutBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.speedBoostComponent;
import com.devcharles.piazzapanic.components.Powerups.timeFreezeBoostComponent;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
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
 */
public class SaveHandler {
    public final static String SAVE_FILE = "save1.json";

    public static void save(String filename, Engine engine, World world, Hud hud) {

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

        for(int i = 0; i < cooks.size(); i++) {
            Entity cook = cooks.get(i);
            CookData cookData = new CookData();
            ControllableComponent controllableComponent = cook.getComponent(ControllableComponent.class);
            TransformComponent transformComponent = cook.getComponent(TransformComponent.class);

            cookData.x = transformComponent.position.x;
            cookData.y = transformComponent.position.y;

            cookData.inventory = new FoodData[controllableComponent.currentFood.size()];
            int j = controllableComponent.currentFood.size() - 1;  // A bit scuffed but you can only peak through a Deque with a for each loop, so we need a separate increment.
            for(Entity currentFood : controllableComponent.currentFood) {
                FoodData foodData = new FoodData();
                foodData.type = currentFood.getComponent(FoodComponent.class).type;
                cookData.inventory[j] = foodData;
                j--;
            }

            ArrayList<BoostData> boostsData = new ArrayList<BoostData>();
            cookBoostComponent cookBoost = cook.getComponent(cookBoostComponent.class);
            if(cookBoost != null) {
                BoostData cookBoostData = new BoostData();
                cookBoostData.type = PowerupComponent.powerupType.cookBoost;
                cookBoostData.time = cookBoost.timeHad;
                boostsData.add(cookBoostData);
            }
            cutBoostComponent cutBoost = cook.getComponent(cutBoostComponent.class);
            if(cutBoost != null) {
                BoostData cutBoostData = new BoostData();
                cutBoostData.type = PowerupComponent.powerupType.cutBoost;
                cutBoostData.time = cutBoost.timeHad;
                boostsData.add(cutBoostData);
            }
            speedBoostComponent speedBoost = cook.getComponent(speedBoostComponent.class);
            if(speedBoost != null) {
                BoostData speedBoostData = new BoostData();
                speedBoostData.type = PowerupComponent.powerupType.speedBoost;
                speedBoostData.time = speedBoost.timeHad;
                boostsData.add(speedBoostData);
            }
            timeFreezeBoostComponent timeFreezeBoost = cook.getComponent(timeFreezeBoostComponent.class);
            if(timeFreezeBoost != null) {
                BoostData timeFreezeBoostData = new BoostData();
                timeFreezeBoostData.type = PowerupComponent.powerupType.timeFreezeBoost;
                timeFreezeBoostData.time = timeFreezeBoost.timeHad;
                boostsData.add(timeFreezeBoostData);
            }
            cookData.boosts = boostsData.toArray(new BoostData[boostsData.size()]);

            cookData.active = cook.getComponent(PlayerComponent.class) != null;

            saveData.cooks[i] = cookData;
        }

        

        // Take the structured data and now save it.

        Json json = new Json();
        String saveText = json.prettyPrint(saveData);

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(saveText);
            writer.close();
            System.out.println("Saved to " + filename);
        } catch (Exception e) {
            System.out.println("Error saving to " + filename);
        }
    }

    public static void load(String filename, Engine engine, EntityFactory entityFactory, World world, Hud hud) {
        Json json = new Json();
        SaveData saveData = new SaveData();
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

        saveData = json.fromJson(SaveData.class, saveText);

        // Set values in the world and hud based on the structured data.

        hud.initMoney(saveData.money);
        // hud.addMoney(saveData.money);
        Hud.reputation[0] = saveData.reputation;
        CustomerAISystem.setDifficulty(saveData.difficulty);
        CustomerAISystem
                .setMaxCustomers(saveData.maxCustomers == -1 ? (int) Double.POSITIVE_INFINITY : saveData.maxCustomers);
        hud.customerTimer = saveData.gameTime;

        CustomerAISystem customerAISystem = engine.getSystem(CustomerAISystem.class);
        customerAISystem.firstSpawn = false;

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

            if(customerData.objective != -1) {
                customerAISystem.numActiveCustomers++;
            }
            
            customerComponent.timer.start();
            customerComponent.timer.setElapsed(customerData.patience);

            customerAISystem.makeItGoThere(aiAgentComponent, customerData.objective);
        }
    }
}
