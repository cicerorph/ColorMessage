package tech.mubilop.util;

import net.minecraft.client.MinecraftClient;
import tech.mubilop.ColorMessage;
import tech.mubilop.config.ColorMessageConfig;

public class MessageModifier {

    public static String modifyMessage(String message) {
        ColorMessageConfig config = ColorMessageConfig.getInstance();
        String serverAddress = getCurrentServer();
        
        // Check if we should use server-specific config
        if (serverAddress != null && config.getServerConfigs().containsKey(serverAddress)) {
            ColorMessageConfig.ServerConfig serverConfig = config.getServerConfig(serverAddress);
            if (!serverConfig.getMessagePrefix().isEmpty()) {
                if (serverConfig.isEnabledForNormalChat() && !message.startsWith("/")) {
                    return serverConfig.getMessagePrefix() + message;
                }
                return message;
            }
        }
        
        if (config.isEnabledForNormalChat() && !message.startsWith("/")) {
            return config.getMessagePrefix() + message;
        }
        
        return message;
    }
    
    public static String modifyCommand(String command) {
        ColorMessageConfig config = ColorMessageConfig.getInstance();
        String serverAddress = getCurrentServer();
        
        // Check if we should use server-specific config
        if (serverAddress != null && config.getServerConfigs().containsKey(serverAddress)) {
            ColorMessageConfig.ServerConfig serverConfig = config.getServerConfig(serverAddress);
            if (!serverConfig.getMessagePrefix().isEmpty()) {
                if (command.startsWith("g ") && serverConfig.isEnabledForGCommand()) {
                    return "g " + serverConfig.getMessagePrefix() + command.substring(2);
                } else if (command.startsWith("l ") && serverConfig.isEnabledForLCommand()) {
                    return "l " + serverConfig.getMessagePrefix() + command.substring(2);
                }
                return command;
            }
        }
        
        if (command.startsWith("g ") && config.isEnabledForGCommand()) {
            // Handle /g command - add prefix to the message part (after "g ")
            return "g " + config.getMessagePrefix() + command.substring(2);
        } else if (command.startsWith("l ") && config.isEnabledForLCommand()) {
            // Handle /l command - add prefix to the message part (after "l ")
            return "l " + config.getMessagePrefix() + command.substring(2);
        }
        
        return command;
    }
    
    private static String getCurrentServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.getCurrentServerEntry() != null) {
            return client.getCurrentServerEntry().address;
        }
        return null;
    }
}
