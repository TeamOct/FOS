package fos.ui.menus;

public class FOSMenuRenderer {
    public MenuBackground background;

    public void changeBackground(MenuBackground background) {
        this.background = background;
        if (background != null)
            background.generateWorld();
    }

    public void render() {
        if (background != null)
            background.render();
    }
}
