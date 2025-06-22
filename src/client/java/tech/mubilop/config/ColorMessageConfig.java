package tech.mubilop.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import tech.mubilop.ColorMessage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ColorMessageConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("colormessage.json").toFile();
    
    private static ColorMessageConfig instance;
    
    private String messagePrefix = "&a"; // Default green color
    private boolean enableForNormalChat = true;
    private boolean enableForGCommand = true;
    private boolean enableForLCommand = true;
    
    // New properties
    private String selectedColor = "GREEN";
    private boolean colorCyclingEnabled = false;
    private List<String> cycleColors = new ArrayList<>(Arrays.asList("RED", "GOLD", "YELLOW", "GREEN", "AQUA", "BLUE", "LIGHT_PURPLE"));
    private int cycleInterval = 5; // seconds
    private Map<String, ServerConfig> serverConfigs = new HashMap<>();
    private List<ColorProfile> colorProfiles = new ArrayList<>();
    
    // Default color profiles
    {
        colorProfiles.add(new ColorProfile("Rainbow", Arrays.asList("RED", "GOLD", "YELLOW", "GREEN", "AQUA", "BLUE", "LIGHT_PURPLE")));
        colorProfiles.add(new ColorProfile("Fire", Arrays.asList("RED", "GOLD", "YELLOW")));
        colorProfiles.add(new ColorProfile("Ocean", Arrays.asList("BLUE", "DARK_BLUE", "AQUA")));
    }
    
    public static ColorMessageConfig getInstance() {
        if (instance == null) {
            instance = loadConfig();
        }
        return instance;
    }
    
    public String getMessagePrefix() {
        return messagePrefix;
    }
    
    public void setMessagePrefix(String messagePrefix) {
        this.messagePrefix = messagePrefix;
        saveConfig();
    }
    
    public boolean isEnabledForNormalChat() {
        return enableForNormalChat;
    }
    
    public void setEnabledForNormalChat(boolean enableForNormalChat) {
        this.enableForNormalChat = enableForNormalChat;
        saveConfig();
    }
    
    public boolean isEnabledForGCommand() {
        return enableForGCommand;
    }
    
    public void setEnabledForGCommand(boolean enableForGCommand) {
        this.enableForGCommand = enableForGCommand;
        saveConfig();
    }
    
    public boolean isEnabledForLCommand() {
        return enableForLCommand;
    }
    
    public void setEnabledForLCommand(boolean enableForLCommand) {
        this.enableForLCommand = enableForLCommand;
        saveConfig();
    }
    
    public String getSelectedColor() {
        return selectedColor;
    }
    
    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
        this.messagePrefix = getColorCode(selectedColor);
        saveConfig();
    }
    
    public boolean isColorCyclingEnabled() {
        return colorCyclingEnabled;
    }
    
    public void setColorCyclingEnabled(boolean colorCyclingEnabled) {
        this.colorCyclingEnabled = colorCyclingEnabled;
        saveConfig();
    }
    
    public List<String> getCycleColors() {
        return cycleColors;
    }
    
    public void setCycleColors(List<String> cycleColors) {
        this.cycleColors = cycleColors;
        saveConfig();
    }
    
    public int getCycleInterval() {
        return cycleInterval;
    }
    
    public void setCycleInterval(int cycleInterval) {
        this.cycleInterval = cycleInterval;
        saveConfig();
    }
    
    public ServerConfig getServerConfig(String serverAddress) {
        if (!serverConfigs.containsKey(serverAddress)) {
            serverConfigs.put(serverAddress, new ServerConfig());
        }
        return serverConfigs.get(serverAddress);
    }
    
    public Map<String, ServerConfig> getServerConfigs() {
        return serverConfigs;
    }
    
    public List<ColorProfile> getColorProfiles() {
        return colorProfiles;
    }
    
    public void addColorProfile(ColorProfile profile) {
        colorProfiles.add(profile);
        saveConfig();
    }
    
    public void removeColorProfile(String name) {
        colorProfiles.removeIf(profile -> profile.getName().equals(name));
        saveConfig();
    }
    
    public String getColorCode(String colorName) {
        switch (colorName) {
            case "BLACK": return "&0";
            case "DARK_BLUE": return "&1";
            case "DARK_GREEN": return "&2";
            case "DARK_AQUA": return "&3";
            case "DARK_RED": return "&4";
            case "DARK_PURPLE": return "&5";
            case "GOLD": return "&6";
            case "GRAY": return "&7";
            case "DARK_GRAY": return "&8";
            case "BLUE": return "&9";
            case "GREEN": return "&a";
            case "AQUA": return "&b";
            case "RED": return "&c";
            case "LIGHT_PURPLE": return "&d";
            case "YELLOW": return "&e";
            case "WHITE": return "&f";
            default: return "&a";
        }
    }
    
    private static ColorMessageConfig loadConfig() {
        ColorMessageConfig config = new ColorMessageConfig();
        
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ColorMessageConfig.class);
                ColorMessage.LOGGER.info("Loaded ColorMessage config");
            } catch (IOException e) {
                ColorMessage.LOGGER.error("Failed to load ColorMessage config", e);
            }
        } else {
            saveConfig(config);
        }
        
        return config;
    }
    
    private static void saveConfig(ColorMessageConfig config) {
        try {
            if (!CONFIG_FILE.exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
                CONFIG_FILE.createNewFile();
            }
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(config, writer);
                ColorMessage.LOGGER.info("Saved ColorMessage config");
            }
        } catch (IOException e) {
            ColorMessage.LOGGER.error("Failed to save ColorMessage config", e);
        }
    }
    
    public void saveConfig() {
        saveConfig(this);
    }
    
    public static class ServerConfig {
        private String messagePrefix = "";
        private boolean enableForNormalChat = true;
        private boolean enableForGCommand = true;
        private boolean enableForLCommand = true;
        private String selectedColor = "";
        private boolean colorCyclingEnabled = false;
        
        public String getMessagePrefix() {
            return messagePrefix;
        }
        
        public void setMessagePrefix(String messagePrefix) {
            this.messagePrefix = messagePrefix;
        }
        
        public boolean isEnabledForNormalChat() {
            return enableForNormalChat;
        }
        
        public void setEnabledForNormalChat(boolean enableForNormalChat) {
            this.enableForNormalChat = enableForNormalChat;
        }
        
        public boolean isEnabledForGCommand() {
            return enableForGCommand;
        }
        
        public void setEnabledForGCommand(boolean enableForGCommand) {
            this.enableForGCommand = enableForGCommand;
        }
        
        public boolean isEnabledForLCommand() {
            return enableForLCommand;
        }
        
        public void setEnabledForLCommand(boolean enableForLCommand) {
            this.enableForLCommand = enableForLCommand;
        }
        
        public String getSelectedColor() {
            return selectedColor;
        }
        
        public void setSelectedColor(String selectedColor) {
            this.selectedColor = selectedColor;
        }
        
        public boolean isColorCyclingEnabled() {
            return colorCyclingEnabled;
        }
        
        public void setColorCyclingEnabled(boolean colorCyclingEnabled) {
            this.colorCyclingEnabled = colorCyclingEnabled;
        }
    }
    
    public static class ColorProfile {
        private String name;
        private List<String> colors;
        
        public ColorProfile(String name, List<String> colors) {
            this.name = name;
            this.colors = colors;
        }
        
        public String getName() {
            return name;
        }
        
        public List<String> getColors() {
            return colors;
        }
    }
}
