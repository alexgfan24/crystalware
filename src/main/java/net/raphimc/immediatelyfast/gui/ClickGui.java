package net.raphimc.immediatelyfast.gui;

import net.raphimc.immediatelyfast.Argon;
import net.raphimc.immediatelyfast.module.Category;
import net.raphimc.immediatelyfast.module.modules.client.ClickGUI;
import net.raphimc.immediatelyfast.utils.ColorUtils;
import net.raphimc.immediatelyfast.utils.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.raphimc.immediatelyfast.Argon.mc;

public final class ClickGui extends Screen {

    public List<Window> windows = new ArrayList<>();
    private float backgroundAlpha = 0f;

    public ClickGui() {
        super(Text.empty());

        int startX = 60;
        int centerY = mc.getWindow().getHeight() / 2 - 150;

        for (Category category : Category.values()) {
            windows.add(new Window(startX, centerY, 220, 28, category, this));
            startX += 240;
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (mc.currentScreen != this) return;

        if (Argon.INSTANCE.previousScreen != null)
            Argon.INSTANCE.previousScreen.render(context, 0, 0, delta);

        float target = ClickGUI.background.getValue() ? 180f : 0f;
        backgroundAlpha += (target - backgroundAlpha) * 0.12f;

        Color bg = new Color(10, 10, 10, (int) backgroundAlpha);

        RenderUtils.renderQuadAbs(
                context.getMatrices(),
                0, 0,
                mc.getWindow().getWidth(),
                mc.getWindow().getHeight(),
                bg.getRGB()
        );

        RenderUtils.unscaledProjection();
        float scale = (float) mc.getWindow().getScaleFactor();
        mouseX *= scale;
        mouseY *= scale;

        for (Window window : windows) {
            window.render(context, mouseX, mouseY, delta);
            window.updatePosition(mouseX, mouseY, delta);
        }

        RenderUtils.scaledProjection();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scale = (float) mc.getWindow().getScaleFactor();
        mouseX *= scale;
        mouseY *= scale;

        for (Window window : windows)
            window.mouseClicked(mouseX, mouseY, button);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        float scale = (float) mc.getWindow().getScaleFactor();
        mouseX *= scale;
        mouseY *= scale;

        for (Window window : windows)
            window.mouseReleased(mouseX, mouseY, button);

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        Argon.INSTANCE.getModuleManager()
                .getModule(ClickGUI.class)
                .setEnabledStatus(false);

        mc.setScreenAndRender(Argon.INSTANCE.previousScreen);
        backgroundAlpha = 0f;

        for (Window window : windows)
            window.onGuiClose();
    }
}

