package azmalent.potiondescriptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {
    public static class Config {
        public boolean sneakingRequired = true;
        public boolean sneakingMessageEnabled = true;
        public boolean showSourceMod = true;
    }

    public static Config config = new Config();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIRECTORY = new File("config");
    private static final File CONFIG_FILE = new File(CONFIG_DIRECTORY, PotionDescriptions.MODID + ".json");

    public static void init() {
        try {
            initConfigFile();
            readConfig();
            writeConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initConfigFile() throws IOException {
        if (!CONFIG_DIRECTORY.exists()) {
            CONFIG_DIRECTORY.mkdir();
        }

        if (!CONFIG_FILE.exists()) {
            CONFIG_FILE.createNewFile();
            writeConfig();
        }
    }

    public static void readConfig() {
        try {
            config = gson.fromJson(new FileReader(CONFIG_FILE), Config.class);
            if (config == null) {
                throw new IllegalStateException("Null configuration");
            }
        } catch (JsonSyntaxException e) {
            System.err.println("Invalid configuration!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeConfig() {
        try {
            String json = gson.toJson(config);

            FileWriter writer = new FileWriter(CONFIG_FILE, false);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
