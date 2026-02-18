/*
 * Author: Matěj Šťastný aka Kirei
 * Date created: 9/29/2024
 * Github link: https://github.com/kireiiiiiiii/java-dino
 */

package jdino.ui;

import java.awt.Container;
import java.awt.Graphics2D;
import java.util.ArrayList;

import jdino.common.Colors;
import jdino.common.GPanel.Renderable;
import jdino.constants.ZIndexList;

public class Background implements Renderable {

    /////////////////
    // Variables
    ////////////////

    private boolean visible;
    ArrayList<String> tags;

    /////////////////
    // Render methods
    ////////////////

    @Override
    public void render(Graphics2D g, int[] size, Container focusCycleRootAncestor) {
        g.setColor(Colors.BASE);
        g.fillRect(0, 0, size[0], size[1]);
    }

    @Override
    public int getZIndex() {
        return ZIndexList.BACKGROUND;
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
        return this.tags;
    }

}
