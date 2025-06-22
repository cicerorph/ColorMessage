package tech.mubilop.util;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import tech.mubilop.ColorMessage;
import tech.mubilop.config.ColorMessageConfig;

import java.util.List;

public class ColorCyclingHandler {
    private static int tickCounter = 0;
    private static int currentColorIndex = 0;
    private static boolean initialized = false;
    
    public static void init() {
        if (initialized) return;
        
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ColorMessageConfig config = ColorMessageConfig.getInstance();
            if (!config.isColorCyclingEnabled() || client.player == null) {
                return;
            }
            
            List<String> cycleColors = config.getCycleColors();
            if (cycleColors.isEmpty()) {
                return;
            }
            
            tickCounter++;
            int ticksPerChange = config.getCycleInterval() * 20; // Convert seconds to ticks
            
            if (tickCounter >= ticksPerChange) {
                tickCounter = 0;
                currentColorIndex = (currentColorIndex + 1) % cycleColors.size();
                String newColor = cycleColors.get(currentColorIndex);
                config.setMessagePrefix(config.getColorCode(newColor));
            }
        });
        
        initialized = true;
        ColorMessage.LOGGER.info("Color cycling handler initialized");
    }
    
    public static void reset() {
        tickCounter = 0;
        currentColorIndex = 0;
    }
}
