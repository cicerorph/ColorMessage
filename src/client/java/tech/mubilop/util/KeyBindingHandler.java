package tech.mubilop.util;

import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import tech.mubilop.ColorMessage;
import tech.mubilop.config.ColorMessageConfig;
import tech.mubilop.screen.ColorMessageConfigScreen;

public class KeyBindingHandler {
    private static KeyBinding openConfigKey;
    private static KeyBinding toggleCyclingKey;
    private static KeyBinding nextColorKey;
    private static KeyBinding prevColorKey;
    private static boolean initialized = false;
    
    public static void init() {
        if (initialized) return;
        
        // Register keybindings
        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.colormessage.openconfig",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            "key.categories.colormessage"
        ));
        
        toggleCyclingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.colormessage.togglecycling",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_C,
            "key.categories.colormessage"
        ));
        
        nextColorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.colormessage.nextcolor",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_BRACKET,
            "key.categories.colormessage"
        ));
        
        prevColorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.colormessage.prevcolor",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_BRACKET,
            "key.categories.colormessage"
        ));
        
        // Register tick event for keybindings
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openConfigKey.wasPressed() && client.player != null) {
                client.setScreen(new ColorMessageConfigScreen(client.currentScreen));
            }
            
            if (toggleCyclingKey.wasPressed() && client.player != null) {
                ColorMessageConfig config = ColorMessageConfig.getInstance();
                config.setColorCyclingEnabled(!config.isColorCyclingEnabled());
                config.saveConfig();
                
                if (client.player != null) {
                    client.player.sendMessage(
                        net.minecraft.text.Text.literal("Color cycling " + 
                            (config.isColorCyclingEnabled() ? "enabled" : "disabled")), 
                        true);
                }
                
                if (!config.isColorCyclingEnabled()) {
                    ColorCyclingHandler.reset();
                }
            }
            
            if (nextColorKey.wasPressed() && client.player != null) {
                cycleColor(client, true);
            }
            
            if (prevColorKey.wasPressed() && client.player != null) {
                cycleColor(client, false);
            }
        });
        
        initialized = true;
        ColorMessage.LOGGER.info("KeyBinding handler initialized");
    }
    
    private static void cycleColor(MinecraftClient client, boolean forward) {
        ColorMessageConfig config = ColorMessageConfig.getInstance();
        List<String> availableColors = Arrays.asList(
            "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", 
            "GOLD", "GRAY", "DARK_GRAY", "BLUE", "GREEN", "AQUA", 
            "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
        );
        
        String currentColor = config.getSelectedColor();
        int currentIndex = availableColors.indexOf(currentColor);
        
        if (currentIndex == -1) currentIndex = 0;
        
        int newIndex;
        if (forward) {
            newIndex = (currentIndex + 1) % availableColors.size();
        } else {
            newIndex = (currentIndex - 1 + availableColors.size()) % availableColors.size();
        }
        
        String newColor = availableColors.get(newIndex);
        config.setSelectedColor(newColor);
        config.setMessagePrefix(config.getColorCode(newColor));
        config.saveConfig();
        
        if (client.player != null) {
            client.player.sendMessage(
                net.minecraft.text.Text.literal("Color set to " + newColor)
                    .setStyle(net.minecraft.text.Style.EMPTY.withColor(getFormattingFromColorName(newColor))), 
                true);
        }
    }
    
    private static net.minecraft.util.Formatting getFormattingFromColorName(String colorName) {
        switch (colorName) {
            case "BLACK": return net.minecraft.util.Formatting.BLACK;
            case "DARK_BLUE": return net.minecraft.util.Formatting.DARK_BLUE;
            case "DARK_GREEN": return net.minecraft.util.Formatting.DARK_GREEN;
            case "DARK_AQUA": return net.minecraft.util.Formatting.DARK_AQUA;
            case "DARK_RED": return net.minecraft.util.Formatting.DARK_RED;
            case "DARK_PURPLE": return net.minecraft.util.Formatting.DARK_PURPLE;
            case "GOLD": return net.minecraft.util.Formatting.GOLD;
            case "GRAY": return net.minecraft.util.Formatting.GRAY;
            case "DARK_GRAY": return net.minecraft.util.Formatting.DARK_GRAY;
            case "BLUE": return net.minecraft.util.Formatting.BLUE;
            case "GREEN": return net.minecraft.util.Formatting.GREEN;
            case "AQUA": return net.minecraft.util.Formatting.AQUA;
            case "RED": return net.minecraft.util.Formatting.RED;
            case "LIGHT_PURPLE": return net.minecraft.util.Formatting.LIGHT_PURPLE;
            case "YELLOW": return net.minecraft.util.Formatting.YELLOW;
            case "WHITE": return net.minecraft.util.Formatting.WHITE;
            default: return net.minecraft.util.Formatting.GREEN;
        }
    }
}
