package fos.ui.menus;

import mindustry.graphics.MenuRenderer;

import static mindustry.Vars.mobile;

public class FOSMenuRenderer extends MenuRenderer {
    private final int width = !mobile ? 100 : 60, height = !mobile ? 50 : 40;
    public MenuBackground background;


    public FOSMenuRenderer(MenuBackground background) {
        if (background != null) {
            this.background = background;
        } else {
            //a default BG just in case
            this.background = FOSMenus.uxerdSpace;
        }

        fosMenuGenerate();
    }

    public void fosMenuGenerate() {
        background.generateWorld(width, height);
    }

    @Override
    public void render() {
        super.render();
        background.render();
    }
}
