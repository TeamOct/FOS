package fos.type.blocks.defense;

import arc.graphics.g2d.Draw;
import mindustry.Vars;
import mindustry.world.blocks.defense.ShockMine;

public class CamoMine extends ShockMine {
    public float alpha = 0.6f;
    public CamoMine(String name) {
        super(name);
        hasShadow = false;
    }

    @SuppressWarnings("unused")
    public class CamoMineBuild extends ShockMineBuild {
        @Override
        public void draw() {
            var tile = Vars.world.tileWorld(x, y);
            Draw.color(tile.floor().mapColor, team == Vars.player.team() ? 1f : alpha); //camouflage!
            Draw.rect(block.region, x, y);

            Draw.color(team.color, teamAlpha);
            Draw.rect(teamRegion, x, y);
            Draw.color();
        }
    }
}
