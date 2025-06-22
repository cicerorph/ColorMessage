package tech.mubilop.mixin.client;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import tech.mubilop.config.ColorMessageConfig;
import tech.mubilop.util.MessageModifier;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatMixin {
    
    @ModifyVariable(
        method = "sendChatMessage",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private String modifyChatMessage(String message) {
        return MessageModifier.modifyMessage(message);
    }
    
    @ModifyVariable(
        method = "sendChatCommand",
        at = @At("HEAD"),
        argsOnly = true,
        ordinal = 0
    )
    private String modifyCommand(String command) {
        return MessageModifier.modifyCommand(command);
    }
}
