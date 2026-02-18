/*
 * Author: Matěj Šťastný aka Kirei
 * Date created: 9/29/2024
 * Github link: https://github.com/kireiiiiiiii/java-dino
 */

package jdino.common;

// import java.util.HashMap;
// import java.util.List;
import java.awt.Color;

import com.catppuccin.Pair;
import com.catppuccin.Palette;

public class Colors {

    /////////////////
    // Constants
    ////////////////

    // public static Color BASE = getCatppuccin().get("base");
    public static Color BASE = Color.decode("#1e1e2e");

    /////////////////
    // Public methods
    ////////////////

    /**
     * Prints the catppuccin colors information
     */
    public static void printCatppuccin() {
        for (Pair<String, com.catppuccin.Color> colourPair : Palette.MOCHA.toList()) {
            String name = colourPair.key();
            com.catppuccin.Color colour = colourPair.value();
            System.out.println(name + ": " + colour.hex());
        }

    }

    /////////////////
    // Private methods
    ////////////////

    // /**
    // * Gets an {@code HashMap} of Catppuccin colors
    // *
    // * @return
    // */
    // private static HashMap<String, Color> getCatppuccin() {
    // List<Pair<String, com.catppuccin.Color>> catppuccin = Palette.MOCHA.toList();
    // HashMap<String, Color> catppuccinMap = new HashMap<>();
    // for (Pair<String, com.catppuccin.Color> colourPair : catppuccin) {
    // catppuccinMap.put(colourPair.key(), Color.decode("#" +
    // colourPair.value().hex()));
    // }
    // return catppuccinMap;
    // }
}
