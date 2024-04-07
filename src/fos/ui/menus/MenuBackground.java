package fos.ui.menus;

import mindustry.world.Tiles;

/**
 * Original code from Project Unity <br>
 * Author: @Goobrr <br>
 * it does not look like the original code, but it works pretty much the same way <br>
 **/
public abstract class MenuBackground {
    /** Used in terrain menus. Generates a world displayed in the menu. */
    void generateWorld() {}
    /** Used in generateWorld() to generate the tiles. */
    void generate(Tiles tiles) {}
    /** Renders the menu BG itself. */
    void render() {}
}
