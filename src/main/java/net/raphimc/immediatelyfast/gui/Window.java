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
        this.c

