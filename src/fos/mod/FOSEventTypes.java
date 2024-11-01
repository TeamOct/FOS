package fos.mod;

import mindustry.world.Tile;

public class FOSEventTypes {
    public static class InsectInvasionEvent {}
    public static class InsectDeathEvent {
        public Tile tile;

        public InsectDeathEvent(Tile tile) {
            this.tile = tile;
        }
    }

    public static class RealisticToggleEvent {}
}
