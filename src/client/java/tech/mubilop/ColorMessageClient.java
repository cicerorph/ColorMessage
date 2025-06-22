package tech.mubilop;

import net.fabricmc.api.ClientModInitializer;
import tech.mubilop.config.ColorMessageConfig;
import tech.mubilop.util.ColorCyclingHandler;
import tech.mubilop.util.KeyBindingHandler;

public class ColorMessageClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Load the config when the client initializes
		ColorMessageConfig.getInstance();
		
		// Initialize handlers
		KeyBindingHandler.init();
		ColorCyclingHandler.init();
		
		ColorMessage.LOGGER.info("ColorMessage client initialized");
	}
}