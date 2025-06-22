package tech.mubilop.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import tech.mubilop.config.ColorMessageConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorCycleEditScreen extends Screen {
    private final Screen parent;
    private final ColorMessageConfig config;
    private final List<String> availableColors = Arrays.asList(
            "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE", 
            "GOLD", "GRAY", "DARK_GRAY", "BLUE", "GREEN", "AQUA", 
            "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
    );
    private List<String> cycleColors;
    
    public ColorCycleEditScreen(Screen parent, ColorMessageConfig config) {
        super(Text.translatable("colormessage.color.edit"));
        this.parent = parent;
        this.config = config;
        this.cycleColors = new ArrayList<>(config.getCycleColors());
    }
    
    @Override
    protected void init() {
        int centerX = this.width / 2;
        
        // Add new color button
        if (cycleColors.size() < 10) {
            CyclingButtonWidget<String> colorSelector = CyclingButtonWidget.<String>builder(color -> 
                    Text.translatable("colormessage.color.add", color)
                    .setStyle(Style.EMPTY.withColor(getFormattingFromColorName(color))))
                .values(availableColors)
                .initially(availableColors.get(0))
                .build(centerX - 100, 40, 150, 20, Text.translatable("colormessage.color.add", ""), 
                    (button, value) -> {});
            this.addDrawableChild(colorSelector);
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("+"),
                    button -> {
                        String selectedColor = colorSelector.getValue();
                        if (!cycleColors.contains(selectedColor)) {
                            cycleColors.add(selectedColor);
                            this.clearAndInit();
                        }
                    })
                .dimensions(centerX + 60, 40, 40, 20).build());
        }
        
        // Show current colors with remove buttons
        for (int i = 0; i < cycleColors.size(); i++) {
            final int index = i;
            final String color = cycleColors.get(i);
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal(color).setStyle(Style.EMPTY.withColor(getFormattingFromColorName(color))),
                    button -> {})
                .dimensions(centerX - 100, 70 + (i * 25), 150, 20).build());
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.literal("✕"),
                    button -> {
                        cycleColors.remove(index);
                        this.clearAndInit();
                    })
                .dimensions(centerX + 60, 70 + (i * 25), 40, 20).build());
        }
        
        // Move up/down buttons if we have more than one color
        if (cycleColors.size() > 1) {
            for (int i = 0; i < cycleColors.size(); i++) {
                final int index = i;
                
                if (i > 0) {
                    this.addDrawableChild(ButtonWidget.builder(
                            Text.literal("↑"),
                            button -> {
                                String temp = cycleColors.get(index);
                                cycleColors.set(index, cycleColors.get(index - 1));
                                cycleColors.set(index - 1, temp);
                                this.clearAndInit();
                            })
                        .dimensions(centerX + 110, 70 + (i * 25), 20, 20).build());
                }
                
                if (i < cycleColors.size() - 1) {
                    this.addDrawableChild(ButtonWidget.builder(
                            Text.literal("↓"),
                            button -> {
                                String temp = cycleColors.get(index);
                                cycleColors.set(index, cycleColors.get(index + 1));
                                cycleColors.set(index + 1, temp);
                                this.clearAndInit();
                            })
                        .dimensions(centerX + 135, 70 + (i * 25), 20, 20).build());
                }
            }
        }
        
        // Save button
        this.addDrawableChild(ButtonWidget.builder(
                Text.translatable("colormessage.config.save"),
                button -> {
                    config.setCycleColors(new ArrayList<>(cycleColors));
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
        
        if (cycleColors.isEmpty()) {
            context.drawCenteredTextWithShadow(this.textRenderer, 
                Text.translatable("colormessage.cycling.empty"), 
                this.width / 2, this.height / 2, 0xAAAAAA);
        }
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public void tick() {
        super.tick();
    }
}
