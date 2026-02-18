/*
 * Author: Matěj Šťastný aka Kirei
 * Date created: 9/29/2024
 * Github link: https://github.com/kireiiiiiiii/java-dino
 */

package jdino;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import jdino.common.GPanel;
import jdino.common.GPanel.InteractableHandeler;
import jdino.common.GPanel.Renderable;
import jdino.ui.Background;
import jdino.ui.Dino;

/**
 * Main method for this application
 */
public class App implements InteractableHandeler {

    /////////////////
    // Main method
    ////////////////

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new App();
        });
    }

    /////////////////
    // Constants
    ////////////////

    private final String APP_NAME = "Java Dino";
    private final int WINDOW_HEIGHT = 500;
    private final int WINDOW_WIDTH = 1500;
    private final int FPS = 60;
    private final double GRAVITY = 0.98;
    private final int GROUND_LEVEL = 300;
    private final double JUMP_VELOCITY = -15.0;

    /////////////////
    // Variables
    ////////////////

    private GPanel gpanel;
    private boolean jumpQued;

    /////////////////
    // Constructor
    ////////////////

    /**
     * Initialize the application.
     */
    public App() {
        // ----------------------
        this.gpanel = new GPanel(this, FPS, WINDOW_WIDTH, WINDOW_HEIGHT, false, APP_NAME);
        widgetIni();
        this.gpanel.showAllWidgets();

        // ----------------------
        GameLoop gameLoop = new GameLoop(60);
        gameLoop.start();
    }

    /////////////////
    // Enents
    ////////////////

    /**
     * Method excecuted each tick of the game loop.
     *
     */
    public void tick() {

        // Jump, if jump qued
        if (this.jumpQued) {
            for (Dino dino : this.gpanel.getWidgetsByClass(Dino.class)) {
                int[] pos = dino.position;
                if (pos[1] >= GROUND_LEVEL)
                    dino.velocity = JUMP_VELOCITY;
            }
        }

        // Apply gravity and update positions for each dino
        for (Dino dino : this.gpanel.getWidgetsByClass(Dino.class)) {
            int[] pos = dino.position;

            // Apply gravity if the dino is in the air
            if (pos[1] < GROUND_LEVEL || this.jumpQued) {
                dino.velocity += GRAVITY;
                dino.position[1] += dino.velocity;

            } else { // Dino has hit the ground, stop falling
                dino.position[1] = GROUND_LEVEL;
                dino.velocity = 0;
            }

        }

        this.jumpQued = false;
    }

    /**
     * Initializes all of the necessary game windgets
     *
     */
    public void widgetIni() {
        ArrayList<Renderable> widgets = new ArrayList<Renderable>();
        // ---------------------
        widgets.add(new Background());
        widgets.add(new Dino());

        this.gpanel.add(widgets);
    }

    /////////////////
    // InteractableHandeler
    ////////////////

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            this.jumpQued = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    /**
     * Game loop, handeles everything that has to be done each tick, except
     * rendering the game.
     *
     */
    private class GameLoop implements Runnable {

        private boolean running = false;
        private int tickrate;

        /**
         * Constructor, sets the target fps the renderer should work at.
         *
         * @param period - updates per second.
         */
        public GameLoop(int tickrate) {
            setTickrate(tickrate);
        }

        public void start() {
            running = true;
            Thread renderThread = new Thread(this, "Render Thread");
            renderThread.start();
        }

        // public void stop() {
        // running = false;
        // }

        @Override
        public void run() {
            while (running) {

                long optimalTime = 1000000000 / tickrate; // In nanoseconds
                long startTime = System.nanoTime();

                tick();

                long elapsedTime = System.nanoTime() - startTime;
                long sleepTime = optimalTime - elapsedTime;

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime / 1000000, (int) (sleepTime % 1000000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Sets a new tickrate value.
         *
         * @param value - new value.
         */
        public void setTickrate(int value) {
            tickrate = value;
        }
    }

}
