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
    public int x, y;
    private final int width, height;
    private final Category category;
    public boolean dragging, extended;
    private int dragX, dragY;

    private float renderX, renderY;
    private float alpha = 0f;

    public ClickGui parent;

    public Window(int x, int y, int width, int height, Category category, ClickGui parent) {
        this.x = x;
        this.y = y;
        this.renderX = x;
        this.renderY = y;
        this.width = width;
        this.height = height;
        this.category = category;
        this.parent = parent;
        this.extended = true;

        int offset = height + 4;
        for (Module module : Argon.INSTANCE.getModuleManager().getModulesInCategory(category)) {
            moduleButtons.add(new ModuleButton(this, module, offset));
            offset += height;
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int targetAlpha = ClickGUI.alphaWindow.getValueInt();
        alpha += (targetAlpha - alpha) * 0.12f;

        renderX += (x - renderX) * 0.25f * delta;
        renderY += (y - renderY) * 0.25f * delta;

        Color background = new Color(18, 18, 18, (int) alpha);
        Color headerTop = new Color(32, 32, 32, (int) alpha);
        Color headerBottom = new Color(22, 22, 22, (int) alpha);

        // Shadow
        RenderUtils.renderRoundedQuad(
                context.getMatrices(),
                new Color(0, 0, 0, 80),
                renderX - 3, renderY - 3,
                renderX + width + 3, renderY + height + 3,
                10, 10, 0, 0, 50
        );

        // Body
        RenderUtils.renderRoundedQuad(
                context.getMatrices(),
                background,
                renderX, renderY,
                renderX + width, renderY + height,
                10, 10, 0, 0, 50
        );

        // Header
        RenderUtils.renderVerticalGradient(
                context.getMatrices(),
                renderX, renderY,
                renderX + width, renderY + height,
                headerTop.getRGB(),
                headerBottom.getRGB()
        );

        // Title
        int textX = (int) (renderX + width / 2f - TextRenderer.getWidth(category.name) / 2f);
        TextRenderer.drawString(
                category.name,
                context,
                textX,
                (int) renderY + 7,
                Color.WHITE.getRGB()
        );

        updateButtons(delta);

        if (extended) {
            for (ModuleButton moduleButton : moduleButtons)
                moduleButton.render(context, mouseX, mouseY, delta);
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isHovered(mouseX, mouseY)) {
            if (button == 0 && !parent.isDraggingAlready()) {
                dragging = true;
                dragX = (int) (mouseX - x);
                dragY = (int) (mouseY - y);
            }
        }

        if (extended)
            for (ModuleButton moduleButton : moduleButtons)
                moduleButton.mouseClicked(mouseX, mouseY, button);
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) dragging = false;

        for (ModuleButton moduleButton : moduleButtons)
            moduleButton.mouseReleased(mouseX, mouseY, button);
    }

    public void updateButtons(float delta) {
        int offset = height + 4;

        for (ModuleButton moduleButton : moduleButtons) {
            moduleButton.animation.animate(
                    0.45 * delta,
                    moduleButton.extended ? height * (moduleButton.settings.size() + 1) : height
            );

            moduleButton.offset = offset;
            offset += moduleButton.animation.getValue();
        }
    }

    public void updatePosition(double mouseX, double mouseY, float delta) {
        if (dragging) {
            x = (int) MathUtils.goodLerp(0.35f * delta, x, mouseX - dragX);
            y = (int) MathUtils.goodLerp(0.35f * delta, y, mouseY - dragY);
        }
    }

    public boolean isHovered(double mouseX, double mouseY) {
        return mouseX > renderX && mouseX < renderX + width
            && mouseY > renderY && mouseY < renderY + height;
    }
}
