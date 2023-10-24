package com.hadenwatne.botcore.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hadenwatne.botcore.App;
import com.hadenwatne.botcore.storage.models.BotConfiguration;
import com.hadenwatne.botcore.utility.FileUtility;
import com.hadenwatne.botcore.service.types.LogType;

import java.io.File;
import java.util.Scanner;

public class BotDataStorageService {
    private final String LOCAL_STORAGE_FOLDER = "data";
    private final String LOCAL_STORAGE_FILE = "config.json";
    private final Gson _gson;
    private final DatabaseService _databaseService;
    private BotConfiguration _botConfiguration;

    public BotDataStorageService() {
        App.getLogger().Log(LogType.SYSTEM, "Initializing storage...");
        _gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        loadBotConfig();

        if (_botConfiguration.useDatabase) {
            _databaseService = new DatabaseService(_botConfiguration.databaseConnectionString, _botConfiguration.databaseName);
        } else {
            _databaseService = null;
        }

        App.getLogger().Log(LogType.SYSTEM, "Storage loading finished.");
    }

    public DatabaseService getDatabaseService() {
        return _databaseService;
    }

    public BotConfiguration getBotConfiguration() {
        return _botConfiguration;
    }

    public void writeBotConfiguration() {
        FileUtility.WriteBytesToFile(LOCAL_STORAGE_FOLDER, LOCAL_STORAGE_FILE, _gson.toJson(_botConfiguration).getBytes());
    }

    private void loadBotConfig() {
        File configFile = new File(LOCAL_STORAGE_FOLDER + File.separator + LOCAL_STORAGE_FILE);

        if (configFile.exists()) {
            String configFileData = FileUtility.LoadFileAsString(configFile);
            _botConfiguration = _gson.fromJson(configFileData, BotConfiguration.class);
        } else {
            _botConfiguration = new BotConfiguration();

            getUserInputFirstRun();
            writeBotConfiguration();
        }
    }

    private void getUserInputFirstRun() {
        System.out.println("========== FIRST RUN SETUP ==========");
        System.out.println("Welcome! Let's collect a few pieces of");
        System.out.println("information to get the bot ready.");
        System.out.println();

        _botConfiguration.adminDiscordID = getConsoleInput("What is your Discord User ID?");
        _botConfiguration.botApiToken = getConsoleInput("What is the bot's Discord API Token?");
        _botConfiguration.useDatabase = getConsoleBoolean("Do you want to use a MongoDB database?");

        if (_botConfiguration.useDatabase) {
            _botConfiguration.databaseConnectionString = getConsoleInput("Please paste your MongoDB connection string:");
            _botConfiguration.databaseName = getConsoleInput("What's the name of the database?");
        }

        _botConfiguration.updateGlobalSlashCommands = false;

        System.out.println("========== SETUP COMPLETE ==========");
        System.out.println("You can edit these values later in " + LOCAL_STORAGE_FOLDER + File.separator + LOCAL_STORAGE_FILE);
    }

    private String getConsoleInput(String question) {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println(question);
            String input = in.nextLine();

            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Value cannot be empty!");
            }
        }
    }

    private boolean getConsoleBoolean(String question) {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println(question + " [Y/N]");
            String input = in.nextLine();

            if (!input.isEmpty()) {
                if(input.equalsIgnoreCase("y")) {
                    return true;
                } else if(input.equalsIgnoreCase("n")) {
                    return false;
                } else {
                    System.out.println("Please type \"Y\" or \"N\"!");
                }
            } else {
                System.out.println("Value cannot be empty!");
            }
        }
    }
}
