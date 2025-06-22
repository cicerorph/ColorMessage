package tech.mubilop.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tech.mubilop.config.ColorMessageConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorProfileEditScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget nameField;
    private final ColorMessageConfig config;
    private final List<String> availableColors = Arrays.asList(
            "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", 
            "GOLD", "GRAY", "DARK_GRAY", "BLUE", "GREEN", "AQUA", 
            "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
    );
    private List<String> profileColors = new ArrayList<>();
    private String profileName = "";
    private boolean isEditing = false;
    private String originalName = "";
    
    public ColorProfileEditScreen(Screen parent, ColorMessageConfig.ColorProfile profile) {
        super(Text.translatable(profile == null ? "colormessage.profile.title.create" : "colormessage.profile.title.edit"));
        this.parent = parent;
        this.config = ColorMessageConfig.getInstance();
        
        if (profile != null) {
            this.isEditing = true;
            this.profileName = profile.getName();
            this.originalName = profile.getName();
            this.profileColors = new ArrayList<>(profile.getColors());
        }
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        
        // Profile name field
        this.nameField = new TextFieldWidget(this.textRenderer, centerX - 100, 40, 200, 20, Text.translatable("colormessage.profile.name"));
        this.nameField.setText(profileName);
        this.nameField.setMaxLength(20);
        this.addDrawableChild(nameField);
        
        // Add new color button
        if (profileColors.size() < 10) {
            CyclingButtonWidget<String> colorSelector = CyclingButtonWidget.<String>builder(color -> 
                    Text.translatable("colormessage.color.add", color)
                    .setStyle(Style.EMPTY.withColor(getFormattingFromColorName(color))))
                .values(availableColors)
                .initially(availableColors.get(0))
                .build(centerX - 100, 70, 150, 20, Text.translatable("colormessage.color.add", ""), 
                    (button, value) -> {});
            this.addDrawableChild(colorSelector);
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("+"),
                    button -> {
                        String selectedColor = colorSelector.getValue();
                        if (!profileColors.contains(selectedColor)) {
                            profileColors.add(selectedColor);
                            this.clearAndInit();
                        }
                    })
                .dimensions(centerX + 60, 70, 40, 20).build());
        }
        
        // Show current colors with remove buttons
        for (int i = 0; i < profileColors.size(); i++) {
            final int index = i;
            final String color = profileColors.get(i);
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(color).setStyle(Style.EMPTY.withColor(getFormattingFromColorName(color))),
                    button -> {})
                .dimensions(centerX - 100, 100 + (i * 25), 150, 20).build());
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("✕"),
                    button -> {
                        profileColors.remove(index);
                        this.clearAndInit();
                    })
                .dimensions(centerX + 60, 100 + (i * 25), 40, 20).build());
        }
        
        // Move up/down buttons if we have more than one color
        if (profileColors.size() > 1) {
            for (int i = 0; i < profileColors.size(); i++) {
                final int index = i;
                
                if (i > 0) {
                    this.addDrawableChild(ButtonWidget.builder(
                            Text.literal("↑"),
                            button -> {
                                String temp = profileColors.get(index);
                                profileColors.set(index, profileColors.get(index - 1));
                                profileColors.set(index - 1, temp);
                                this.clearAndInit();
                            })
                        .dimensions(centerX + 110, 100 + (i * 25), 20, 20).build());
                }
                
                if (i < profileColors.size() - 1) {
                    this.addDrawableChild(ButtonWidget.builder(
                            Text.literal("↓"),
                            button -> {
                                String temp = profileColors.get(index);
                                profileColors.set(index, profileColors.get(index + 1));
                                profileColors.set(index + 1, temp);
                                this.clearAndInit();
                            })
                        .dimensions(centerX + 135, 100 + (i * 25), 20, 20).build());
                }
            }
        }
        
        // Delete button (only for existing profiles)
        if (isEditing) {
            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("colormessage.profile.delete"),
                    button -> {
                        config.removeColorProfile(originalName);
                        config.saveConfig();
                        this.client.setScreen(parent);
                    })
                .dimensions(centerX - 100, this.height - 70, 200, 20).build());
        }
        
        // Save button
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.config.save"),
                button -> {
                    String newName = nameField.getText().trim();
                    if (newName.isEmpty()) {
                        return; // Don't save if name is empty
                    }
                    
                    if (profileColors.isEmpty()) {
                        return; // Don't save if no colors
                    }
                    
                    if (isEditing) {
                        config.removeColorProfile(originalName);
                    }
                    
                    config.addColorProfile(new ColorMessageConfig.ColorProfile(newName, new ArrayList<>(profileColors)));
                    config.saveConfig();
                    this.client.setScreen(parent);
                })
            .dimensions(centerX - 100, this.height - 40, 95, 20).build());
        
        // Cancel button
        this.addDrawableChild(ButtonWidget.builder(
                ScreenTexts.CANCEL,
                button -> this.client.setScreen(parent))
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
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        context.drawTextWithShadow(this.textRenderer, Text.translatable("colormessage.profile.name"), this.width / 2 - 100, 25, 0xAAAAAA);
        
        if (profileColors.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, 
                Text.translatable("colormessage.profile.empty"), 
                this.width / 2, 150, 0xAAAAAA);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public void tick() {
        super.tick();
    }
}
