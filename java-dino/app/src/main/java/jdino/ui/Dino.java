/*
 * Author: Matěj Šťastný aka Kirei
 * Date created: 9/29/2024
 * Github link: https://github.com/kireiiiiiiii/java-dino
 */

package jdino.ui;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.ArrayList;

import jdino.common.GPanel.Renderable;
import jdino.constants.ZIndexList;

public class Dino implements Renderable {

    /////////////////
    // Variables
    ////////////////

    private boolean visible;
    public int[] position = { 100, 100 };
    public double velocity;

    /////////////////
    // Render
    ////////////////

    @Override
    public void render(Graphics2D g, int[] size, Container focusCycleRootAncestor) {
        g.setColor(Color.RED);
        g.fillRect(this.position[0], this.position[1], 20, 20);
    }

    @Override
    public int getZIndex() {
        return ZIndexList.DINO;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void hide() {
        this.visible = false;
    }

    @Override
    public void show() {
        this.visible = true;
    }

    @Override
    public ArrayList<String> getTags() {
        ArrayList<String> tags = new ArrayList<>();
        return tags;
    }

}
