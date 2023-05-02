package com.devcharles.piazzapanic.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.devcharles.piazzapanic.GameScreen;
import com.devcharles.piazzapanic.componentsystems.CustomerAISystem;
import com.devcharles.piazzapanic.scene2d.Hud;
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

    public static void save(String filename, World world, Hud hud) {

        SaveData saveData = new SaveData();

        // Get data from passed in world and hud.

        saveData.money = Hud.getMoney();
        saveData.reputation = Hud.reputation[0];
        saveData.difficulty = CustomerAISystem.getDifficulty();
        saveData.maxCustomers = CustomerAISystem.getMaxCustomers();

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

    public static void load(String filename, World world, Hud hud) {
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

        saveData.money = 1000;

        hud.initMoney(saveData.money);
        //hud.addMoney(saveData.money);
        Hud.reputation[0] = saveData.reputation;
        CustomerAISystem.setDifficulty(saveData.difficulty);
        if (saveData.maxCustomers != -1)
            CustomerAISystem.setMaxCustomers(saveData.maxCustomers);// Leave it at infinite if it's -1.

    }
}
