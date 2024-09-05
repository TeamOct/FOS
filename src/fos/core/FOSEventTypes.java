package fos.core;

import mindustry.world.Tile;

public class FOSEventTypes {
    public static class InsectInvasionEvent {}
    public static class InsectDeathEvent {
        public Tile tile;

        public InsectDeathEvent(Tile tile) {
            this.tile = tile;
        }
    }
}
