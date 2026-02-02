package net.raphimc.immediatelyfast.gui;

import net.raphimc.immediatelyfast.Argon;
import net.raphimc.immediatelyfast.gui.components.ModuleButton;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.Module;
import net.raphimc.immediatelyfast.module.modules.client.ClickGUI;
import net.raphimc.immediatelyfast.utils.*;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class Window {

    public List<ModuleButton> moduleButtons = new ArrayList<>();

    private int x, y;
    private final int width, height;
    private final Category category;

    public boolean dragging = false;
    public boolean extended = true;

    private int dragX, dragY;

    private int prevX, prevY;
    private Color currentColor;

    public ClickGui parent;

    public Window(int x, int y, int width, int height, Category category, ClickGui parent) {
        this.x = x;
        this.y = y;
        this.prevX = x;
        this.prevY = y;
        this.width = width;
        this.height = height;
        this.category = category;
        this.parent = parent;

        int offset = height + 4;
        for (Module module : Argon.INSTANCE.getModuleManager().getModulesInCategory(category)) {
            moduleButtons.add(new ModuleButton(this, module, offset));
            offset += height;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int targetAlpha = ClickGUI.alphaWindow.getValueInt();

        if (currentColor == null)
            currentColor = new Color(15, 15, 15, 0);
        else
            currentColor = new Color(15, 15, 15, currentColor.getAlpha());

        if (currentColor.getAlpha() != targetAlpha)
            currentColor = ColorUtils.smoothAlphaTransition(0.1F, targetAlpha, currentColor);

        // Smooth follow
        prevX = (int) MathUtils.goodLerp(0.25f * delta, prevX, x);
        prevY = (int) MathUtils.goodLerp(0.25f * delta, prevY, y);

        // Shadow
        RenderUtils.renderRoundedQuad(
                context.getMatrices(),
                new Color(0, 0, 0, 90),
                prevX - 3, prevY - 3,
                prevX + width + 3, prevY + height + 3,
                ClickGUI.roundQuads.getValueInt(),
                ClickGUI.roundQuads.getValueInt(),
                0, 0, 50
        );

        // Main window
        RenderUtils.renderRoundedQuad(
                context.getMatrices(),
                currentColor,
                prevX, prevY,
                prevX + width, prevY + height,
                ClickGUI.roundQuads.getValueInt(),
                ClickGUI.roundQuads.getValueInt(),
                0, 0, 50
        );

        // Header accent
        RenderUtils.renderQuadAbs(
                context.getMatrices(),
                prevX,
                prevY + height - 2,
                prevX + width,
                prevY + height,
                Utils.getMainColor(255, moduleButtons.isEmpty() ? 0 : moduleButtons.indexOf(moduleButtons.get(0))).getRGB()
        );

        // Title
        int textWidth = TextRenderer.getWidth(category.name);
        int textX = prevX + (width / 2) - (textWidth / 2);
        TextRenderer.drawString(
                category.name,
                context,
                textX,
                prevY + 6,
                Color.WHITE.getRGB()
        );

        updateButtons(delta);

        if (extended) {
            for (ModuleButton button : moduleButtons) {
                button.render(context, mouseX, mouseY, delta);
            }
        }
    }

    public void updateButtons(float delta) {
        int offset = height + 4;

        for (ModuleButton button : moduleButtons) {
            button.animation.animate(
                    0.45 * delta,
                    button.extended ? height * (button.settings.size() + 1) : height
            );

            button.offset = offset;
            offset += (int) button.animation.getValue();
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0 && !isDraggingAlready()) {
                dragging = true;
                dragX = (int) (mouseX - x);
                dragY = (int) (mouseY - y);
            }
        }

        if (extended)
            for (ModuleButton mb : moduleButtons)
                mb.mouseClicked(mouseX, mouseY, button);
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            dragging = false;

        for (ModuleButton mb : moduleButtons)
            mb.mouseReleased(mouseX, mouseY, button);
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (extended)
            for (ModuleButton mb : moduleButtons)
                mb.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        y += (int) (verticalAmount * 20);
    }

    public void updatePosition(double mouseX, double mouseY, float delta) {
        if (dragging) {
            x = (int) MathUtils.goodLerp(0.35f * delta, x, mouseX - dragX);
            y = (int) MathUtils.goodLerp(0.35f * delta, y, mouseY - dragY);
        }
    }

    public boolean isDraggingAlready() {
        for (Window window : parent.windows)
            if (window.dragging)
                return true;
        return false;
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > prevX && mouseX < prevX + width
            && mouseY > prevY && mouseY < prevY + height;
    }

    public void onGuiClose() {
        currentColor = null;
        dragging = false;

        for (ModuleButton button : moduleButtons)
            button.onGuiClose();
    }

    // ===== Getters (required by other components) =====

    public int getX() {
        return prevX;
    }

    public int getY() {
        return prevY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
