package tech.mubilop.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tech.mubilop.config.ColorMessageConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorMessageConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget prefixField;
    private ButtonWidget normalChatButton;
    private ButtonWidget gCommandButton;
    private ButtonWidget lCommandButton;
    private ButtonWidget colorCyclingButton;
    private final ColorMessageConfig config;
    private ButtonWidget profilesButton;
    private ButtonWidget serverConfigButton;
    private int currentTab = 0; // 0 = Main, 1 = Cycling, 2 = Profiles
    private final List<String> availableColors = Arrays.asList(
            "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", 
            "GOLD", "GRAY", "DARK_GRAY", "BLUE", "GREEN", "AQUA", 
            "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
    );
    private String currentServerAddress = "global";
    private ColorMessageConfig.ServerConfig serverConfig;
    private boolean useServerConfig = false;

    public ColorMessageConfigScreen(Screen parent) {
        super(Text.translatable("colormessage.config.title"));
        this.parent = parent;
        this.config = ColorMessageConfig.getInstance();
        
        // Get current server info if available
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getCurrentServerEntry() != null) {
            ServerInfo serverInfo = client.getCurrentServerEntry();
            currentServerAddress = serverInfo.address;
            serverConfig = config.getServerConfig(currentServerAddress);
        }
    }

    @Override
    protected void init() {
        if (currentTab == 0) {
            initMainTab();
        } else if (currentTab == 1) {
            initCyclingTab();
        } else if (currentTab == 2) {
            initProfilesTab();
        }
        
        // Navigation tabs always visible
        int tabWidth = 80;
        int startX = (this.width - (tabWidth * 3)) / 2;
        
        // For tab buttons, use style formatting instead of prefix codes
        Text mainText = Text.translatable("colormessage.config.tab.main");
        Text cyclingText = Text.translatable("colormessage.config.tab.cycling");
        Text profilesText = Text.translatable("colormessage.config.tab.profiles");
        
        if (currentTab == 0) {
            mainText = mainText.copy().setStyle(Style.EMPTY.withBold(true).withUnderline(true));
        } else if (currentTab == 1) {
            cyclingText = cyclingText.copy().setStyle(Style.EMPTY.withBold(true).withUnderline(true));
        } else if (currentTab == 2) {
            profilesText = profilesText.copy().setStyle(Style.EMPTY.withBold(true).withUnderline(true));
        }
        
        this.addDrawableChild(ButtonWidget.builder(
                mainText,
                button -> {
                    currentTab = 0;
                    this.clearAndInit();
                })
            .dimensions(startX, 10, tabWidth, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
                cyclingText,
                button -> {
                    currentTab = 1;
                    this.clearAndInit();
                })
            .dimensions(startX + tabWidth, 10, tabWidth, 20).build());
        
        this.addDrawableChild(ButtonWidget.builder(
                profilesText,
                button -> {
                    currentTab = 2;
                    this.clearAndInit();
                })
            .dimensions(startX + (tabWidth * 2), 10, tabWidth, 20).build());
    }
    
    private void initMainTab() {
        int centerX = this.width / 2;
        
        // Server configuration toggle
        if (!currentServerAddress.equals("global")) {
            this.serverConfigButton = ButtonWidget.builder(
                    Text.translatable("colormessage.config.using", 
                        useServerConfig ? Text.translatable("colormessage.config.server").getString() : 
                            Text.translatable("colormessage.config.global").getString()),
                    button -> {
                        useServerConfig = !useServerConfig;
                        button.setMessage(Text.translatable("colormessage.config.using", 
                            useServerConfig ? Text.translatable("colormessage.config.server").getString() : 
                                Text.translatable("colormessage.config.global").getString()));
                        clearAndInit();
                    })
                .dimensions(centerX - 100, 40, 200, 20).build();
            this.addDrawableChild(serverConfigButton);
        }
        
        int yOffset = !currentServerAddress.equals("global") ? 70 : 50;
        
        // Prefix field - directly editable, no color selector button
        this.prefixField = new TextFieldWidget(this.textRenderer, centerX - 100, yOffset, 200, 20, Text.translatable("colormessage.config.prefix"));
        this.prefixField.setText(useServerConfig && !serverConfig.getMessagePrefix().isEmpty() ? 
                serverConfig.getMessagePrefix() : config.getMessagePrefix());
        this.prefixField.setMaxLength(20);
        this.addDrawableChild(prefixField);
        
        // Normal chat toggle
        this.normalChatButton = ButtonWidget.builder(
                Text.translatable("colormessage.config.normalchat", 
                    useServerConfig ? 
                        (serverConfig.isEnabledForNormalChat() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString()) : 
                        (config.isEnabledForNormalChat() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString())),
                button -> {
                    if (useServerConfig) {
                        serverConfig.setEnabledForNormalChat(!serverConfig.isEnabledForNormalChat());
                        button.setMessage(Text.translatable("colormessage.config.normalchat", 
                            serverConfig.isEnabledForNormalChat() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    } else {
                        config.setEnabledForNormalChat(!config.isEnabledForNormalChat());
                        button.setMessage(Text.translatable("colormessage.config.normalchat", 
                            config.isEnabledForNormalChat() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    }
                })
            .dimensions(centerX - 100, yOffset + 30, 200, 20).build();
        this.addDrawableChild(normalChatButton);
        
        // /g command toggle
        this.gCommandButton = ButtonWidget.builder(
                Text.translatable("colormessage.config.gcommand", 
                    useServerConfig ? 
                        (serverConfig.isEnabledForGCommand() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString()) : 
                        (config.isEnabledForGCommand() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString())),
                button -> {
                    if (useServerConfig) {
                        serverConfig.setEnabledForGCommand(!serverConfig.isEnabledForGCommand());
                        button.setMessage(Text.translatable("colormessage.config.gcommand", 
                            serverConfig.isEnabledForGCommand() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    } else {
                        config.setEnabledForGCommand(!config.isEnabledForGCommand());
                        button.setMessage(Text.translatable("colormessage.config.gcommand", 
                            config.isEnabledForGCommand() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    }
                })
            .dimensions(centerX - 100, yOffset + 60, 200, 20).build();
        this.addDrawableChild(gCommandButton);
        
        // /l command toggle
        this.lCommandButton = ButtonWidget.builder(
                Text.translatable("colormessage.config.lcommand", 
                    useServerConfig ? 
                        (serverConfig.isEnabledForLCommand() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString()) : 
                        (config.isEnabledForLCommand() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString())),
                button -> {
                    if (useServerConfig) {
                        serverConfig.setEnabledForLCommand(!serverConfig.isEnabledForLCommand());
                        button.setMessage(Text.translatable("colormessage.config.lcommand", 
                            serverConfig.isEnabledForLCommand() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    } else {
                        config.setEnabledForLCommand(!config.isEnabledForLCommand());
                        button.setMessage(Text.translatable("colormessage.config.lcommand", 
                            config.isEnabledForLCommand() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    }
                })
            .dimensions(centerX - 100, yOffset + 90, 200, 20).build();
        this.addDrawableChild(lCommandButton);
        
        // Color cycling toggle
        this.colorCyclingButton = ButtonWidget.builder(
                Text.translatable("colormessage.config.colorcycling", 
                    useServerConfig ? 
                        (serverConfig.isColorCyclingEnabled() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString()) : 
                        (config.isColorCyclingEnabled() ? 
                            Text.translatable("colormessage.config.enabled").getString() : 
                            Text.translatable("colormessage.config.disabled").getString())),
                button -> {
                    if (useServerConfig) {
                        serverConfig.setColorCyclingEnabled(!serverConfig.isColorCyclingEnabled());
                        button.setMessage(Text.translatable("colormessage.config.colorcycling", 
                            serverConfig.isColorCyclingEnabled() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    } else {
                        config.setColorCyclingEnabled(!config.isColorCyclingEnabled());
                        button.setMessage(Text.translatable("colormessage.config.colorcycling", 
                            config.isColorCyclingEnabled() ? 
                                Text.translatable("colormessage.config.enabled").getString() : 
                                Text.translatable("colormessage.config.disabled").getString()));
                    }
                })
            .dimensions(centerX - 100, yOffset + 120, 200, 20).build();
        this.addDrawableChild(colorCyclingButton);
        
        // Save button
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.config.save"),
                button -> {
                    if (useServerConfig) {
                        serverConfig.setMessagePrefix(prefixField.getText());
                    } else {
                        config.setMessagePrefix(prefixField.getText());
                    }
                    config.saveConfig();
                    this.close();
                })
            .dimensions(centerX - 100, this.height - 40, 95, 20).build());
        
        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.CANCEL,
                button -> this.close())
            .dimensions(centerX + 5, this.height - 40, 95, 20).build());
    }
    
    private void initCyclingTab() {
        int centerX = this.width / 2;
        int yOffset = 50;
        
        // Interval selector
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.cycling.interval", config.getCycleInterval()),
                button -> {
                    int newInterval = (config.getCycleInterval() % 10) + 1; // Cycle 1-10 seconds
                    config.setCycleInterval(newInterval);
                    button.setMessage(Text.translatable("colormessage.cycling.interval", newInterval));
                })
            .dimensions(centerX - 100, yOffset, 200, 20).build());
        
        // Colors list
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.cycling.edit"),
                button -> {
                    MinecraftClient.getInstance().setScreen(new ColorCycleEditScreen(this, config));
                })
            .dimensions(centerX - 100, yOffset + 30, 200, 20).build());
        
        // Save button
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.config.save"),
                button -> {
                    config.saveConfig();
                    this.close();
                })
            .dimensions(centerX - 100, this.height - 40, 95, 20).build());
        
        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.CANCEL,
                button -> this.close())
            .dimensions(centerX + 5, this.height - 40, 95, 20).build());
    }
    
    private void initProfilesTab() {
        int centerX = this.width / 2;
        int yOffset = 50;
        
        // Add new profile button
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.profile.create"),
                button -> {
                    MinecraftClient.getInstance().setScreen(new ColorProfileEditScreen(this, null));
                })
            .dimensions(centerX - 100, yOffset, 200, 20).build());
        
        // List existing profiles
        List<ColorMessageConfig.ColorProfile> profiles = config.getColorProfiles();
        for (int i = 0; i < profiles.size() && i < 5; i++) {
            final ColorMessageConfig.ColorProfile profile = profiles.get(i);
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(profile.getName()),
                    button -> {
                        MinecraftClient.getInstance().setScreen(new ColorProfileEditScreen(this, profile));
                    })
                .dimensions(centerX - 100, yOffset + 30 + (i * 25), 150, 20).build());
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("colormessage.profile.use"),
                    button -> {
                        config.setCycleColors(new ArrayList<>(profile.getColors()));
                        config.saveConfig();
                        this.clearAndInit();
                    })
                .dimensions(centerX + 60, yOffset + 30 + (i * 25), 40, 20).build());
        }
        
        // Save button
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.config.save"),
                button -> {
                    config.saveConfig();
                    this.close();
                })
            .dimensions(centerX - 100, this.height - 40, 95, 20).build());
        
        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.CANCEL,
                button -> this.close())
            .dimensions(centerX + 5, this.height - 40, 95, 20).build());
    }
    
    private Formatting getFormattingFromColorName(String colorName) {
        switch (colorName) {
            case "BLACK": return Formatting.BLACK;
            case "DARK_BLUE": return Formatting.DARK_BLUE;
            case "DARK_GREEN": return Formatting.DARK_GREEN;
            case "DARK_AQUA": return Formatting.DARK_AQUA;
            case "DARK_RED": return Formatting.DARK_RED;
            case "DARK_PURPLE": return Formatting.DARK_PURPLE;
            case "GOLD": return Formatting.GOLD;
            case "GRAY": return Formatting.GRAY;
            case "DARK_GRAY": return Formatting.DARK_GRAY;
            case "BLUE": return Formatting.BLUE;
            case "GREEN": return Formatting.GREEN;
            case "AQUA": return Formatting.AQUA;
            case "RED": return Formatting.RED;
            case "LIGHT_PURPLE": return Formatting.LIGHT_PURPLE;
            case "YELLOW": return Formatting.YELLOW;
            case "WHITE": return Formatting.WHITE;
            default: return Formatting.GREEN;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        
        int centerX = this.width / 2;
        
        // Draw the main screen elements based on current tab
        if (currentTab == 0) {
            // Main tab
            int yOffset = !currentServerAddress.equals("global") ? 70 : 50;
            
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, centerX, 30, 0xFFFFFF);
            context.drawTextWithShadow(this.textRenderer, Text.translatable("colormessage.config.prefix"), centerX - 100, yOffset - 15, 0xAAAAAA);
            
            // Preview using current or entered prefix
            String previewPrefix = prefixField != null ? prefixField.getText() : 
                (useServerConfig ? serverConfig.getMessagePrefix() : config.getMessagePrefix());
            String previewText = "Hello World";
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.translatable("colormessage.config.preview", previewText), 
                centerX - 100, this.height - 70, 0xAAAAAA);
            
            // Convert color codes for preview
            String coloredText = previewPrefix + previewText;
            for (char c = '0'; c <= '9'; c++) {
                coloredText = coloredText.replace("&" + c, "§" + c);
            }
            for (char c = 'a'; c <= 'f'; c++) {
                coloredText = coloredText.replace("&" + c, "§" + c);
            }
            
            context.drawTextWithShadow(this.textRenderer, 
                Text.literal(coloredText), 
                centerX - 100, this.height - 60, 0xFFFFFF);
        } else if (currentTab == 1) {
            // Cycling tab
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("colormessage.cycling.title"), centerX, 30, 0xFFFFFF);
            
            // Draw current cycle colors
            context.drawTextWithShadow(this.textRenderer, Text.translatable("colormessage.cycling.current"), centerX - 100, 80, 0xAAAAAA);
            
            List<String> cycleColors = config.getCycleColors();
            if (cycleColors.isEmpty()) {
                context.drawTextWithShadow(this.textRenderer, Text.translatable("colormessage.cycling.empty"), centerX - 100, 100, 0xFF5555);
            } else {
                for (int i = 0; i < cycleColors.size() && i < 7; i++) {
                    String colorName = cycleColors.get(i);
                    context.drawTextWithShadow(this.textRenderer, 
                        Text.literal("• " + colorName).setStyle(Style.EMPTY.withColor(getFormattingFromColorName(colorName))), 
                        centerX - 90, 100 + (i * 15), 0xFFFFFF);
                }
            }
        } else if (currentTab == 2) {
            // Profiles tab
            context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("colormessage.config.tab.profiles"), centerX, 30, 0xFFFFFF);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}
