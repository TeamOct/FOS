package fos.maps.generators.bases;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.Seq;
import fos.world.blocks.environment.UndergroundOreBlock;
import mindustry.ai.*;
import mindustry.content.Blocks;
import mindustry.game.*;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.payloads.*;

import static mindustry.Vars.*;

public class LumoniBaseGenerator extends FOSBaseGenerator {
    public LumoniBaseGenerator() {
        schematics = new String[]{
            //cores
            "bXNjaAF4nF2QsW7DMAxET7YjVLCj1EPRr9CQsd9SdHBipTWg2IGUNL9fnrgVAvxoiiceiRFji26drhHv6XFd1imUnynPcQ7nLcdwDEf0cyznvNzuy7YCsGk6xVTQfH7t8HbZSihL+o05PKeUQpryd8ThXxqvTDyXdQ73Rz4ta4Rj5vZIJWJkWNtdtnzPsRRp8wEYOS3YUuEIA4/WoJHYEJ1iB9MILEA4NEz2Wjko9kQrgXw6ORUDahNPQcc7vmgVTpP1bsc7wjOwtMNuYoPoFD1NWGlkWHKA5F/kVOtV4FTgVOBU4FTgKDCSkgFa8dWhTmAVjnYHtdurpYGVRP3b849DVsGeAiarXc8Z6+K4LK8789wZ0SsGRfXia60Ysgp57A+okDUt",

            //defenses
            "bXNjaAF4nF1QzU7GIBDc/tAWqtGLifEdOHjxZYwHbPn8SGj7Bai+vrtMvJgWhl2W2dkhQ3NH/e42T8/x3MLubL66tPrVrv7i9+ztK82rz0sKtxKOnYiG6D59zNS+fwz0cDmyzSF++2R/XIz0KImbSyUsZ3TlSPT0r8RGl7483Um6hN0yWaH7v2hNgVnmUPxm83GmxXPLN17UUCfQAjpAD1CAATACJoAGGIGGv7apHG3LZ34n0cSLIwN+yfBv0IYj6aWoERg4ZjDo3QhLL3Tsohxkq5U9iyC5qyJ6PFAgU1IpMCJpIL0qE+mttFHCOchEkqwCB1SOaDti6BHJCaoniRSPzBu9dDJ8zWvYpmGbhm0aDBq2adimoVjDNi22/QIzGTiq",
            "bXNjaAF4nDWQXW6DMBCEFwNOHEzz26pSz+CXnqD3qPpAwFEtERMZo16/u4zywuddzw7roZbeSqpid/f0Pi73EDs3/3Zp8IMb/M3H2btPagY/9yk8cpgiEemxu/pxJvX9o+lwm2b36FIO/TJ2eUpkpZNDdCzK1D6rIYVxhHxKnt2z70W+dv5CHFxe0jVET03I/u7maUm95999ERWkqShIcbWiABRQAhXf8tlIVfJRCRRQoVkDGtiwD9+0VGJ4BdsVjErmaoHIt6KraSd3NR1loZpOqM7ABXgVFw0zDTMtLhWjRmWBFngB9sABOAJnWUKzJ2Oz7sabrHMblijFOAEXWWkrAQgUsL5hK8qCU1mjM4jOIDqD6AyUBp4GngaeO4w3T5yl2fBK/BorH/rgvsW1RSYWmVhkYiWTf2phOYk=",
            "bXNjaAF4nEWPW07DMBBFr2Pn1YSSdgVswB+sB/HRNkZYcpzKcVq2wzd7Yw3MZEBoZJ95eeYaOxw1TDxNDvW7C/7DPqMb3XJJ/pr9HAFU4XR2YUH58v359VpjeJsXe/dxtHlNZx8des5kHymbHFqOtlk4suuna5pvbrRj8iH8N9PQjP1flOY1u4TOZzfZZV7TxdHuJzowgCIrGqEmFsRf9IIB0GSK2zeHYQSloBLUggaqJLTQHO0k2Ql6qT2g4GgvyUfBILUDtsGGtxvRYkSLYS2KlzasvuJLEUR/vekHvdBca3Eo+Zs/qmA1Qw==",
            "bXNjaAF4nE3RXU6EMBQF4Ftob8vPOMZ98OAe3IXxAYaakIxAADNxOT67N9cg5ZxJJClfofe2JyBBzrnYsf2I4vppa56l6uN6WYZ5G6ZRRPTadvG6inv9/f55s+Lfp7XZK+WcJpdpnuPS3IYlymN6cRvGvtk+l24Yozz9K+nabYvL177ji+Ay+8jCPnZzPLpwTEWBBwGLBWtLPNYB25xSkUGLQYtBSyZ3Dc1oTi11VKmngRa0pBWt6Yk+HJlS5nRImqSbAg8C1kpUHuktSixKLFI7uWtoRnNqqaNKPQ20oCWtaE1PFOkV6RXpFdEU0RTpFek1pU+fHME8f59Hi0eLFy4WNJ36ByHMKHA=",
        };
        init();
    }

    @Override
    public void generate(Tiles tiles, Seq<Tile> cores, Tile spawn, Team team, Sector sector, float difficulty) {
        this.tiles = tiles;
        this.cores = cores;

        //don't generate bases when there are no loaded schematics
        if(coreParts.isEmpty()) return;

        Mathf.rand.setSeed(sector.id);

        float bracketRange = 0.17f;
        float baseChance = Mathf.lerp(0.7f, 2.1f, difficulty);
        int wallAngle = 180; //180 for full coverage
        double resourceChance = 0.5 * baseChance;
        double nonResourceChance = 0.0005 * baseChance;
        BaseRegistry.BasePart coreschem = coreParts.getFrac(difficulty);
        int passes = difficulty < 0.4 ? 1 : difficulty < 0.8 ? 2 : 3;

        Block wall = getDifficultyWall(1, difficulty), wallLarge = getDifficultyWall(2, difficulty);

        for(Tile tile : cores){
            tile.clearOverlay();
            Schematics.placeLoadout(coreschem.schematic, tile.x, tile.y, team, false);

            //fill core with every type of item (even non-material)
            Building entity = tile.build;
            for(Item item : content.items()){
                entity.items.add(item, entity.block.itemCapacity);
            }
        }

        for(int i = 0; i < passes; i++){
            //random schematics
            pass(tile -> {
                if(!tile.block().alwaysReplace) return;

                if(((tile.overlay().asFloor().itemDrop != null || tile.overlay() instanceof UndergroundOreBlock || (tile.drop() != null && Mathf.chance(nonResourceChance)))
                    || (tile.floor().liquidDrop != null && Mathf.chance(nonResourceChance * 2))) && Mathf.chance(resourceChance)){
                    Seq<BaseRegistry.BasePart> parts = forResource(
                        tile.overlay() instanceof UndergroundOreBlock uo ? uo.drop :
                            tile.drop() != null ? tile.drop() : tile.floor().liquidDrop
                    );
                    if(!parts.isEmpty()){
                        tryPlace(parts.getFrac(difficulty + Mathf.range(bracketRange)), tile.x, tile.y, team);
                    }
                }else if(Mathf.chance(nonResourceChance)){
                    tryPlace(parts.getFrac(difficulty + Mathf.range(bracketRange)), tile.x, tile.y, team);
                }
            });
        }

        //small walls
        pass(tile -> {

            if(tile.block().alwaysReplace){
                boolean any = false;

                for(Point2 p : Geometry.d4){
                    Tile o = tiles.get(tile.x + p.x, tile.y + p.y);

                    //do not block payloads
                    if(o != null && (o.block() instanceof PayloadConveyor || o.block() instanceof PayloadBlock)){
                        return;
                    }
                }

                for(Point2 p : Geometry.d8){
                    if(Angles.angleDist(Angles.angle(p.x, p.y), spawn.angleTo(tile)) > wallAngle){
                        continue;
                    }

                    Tile o = tiles.get(tile.x + p.x, tile.y + p.y);
                    if(o != null && o.team() == team && !(o.block() instanceof Wall)){
                        any = true;
                        break;
                    }
                }

                if(any){
                    tile.setBlock(wall, team);
                }
            }
        });

        //large walls
        pass(curr -> {
            int walls = 0;
            for(int cx = 0; cx < 2; cx++){
                for(int cy = 0; cy < 2; cy++){
                    Tile tile = tiles.get(curr.x + cx, curr.y + cy);
                    if(tile == null || tile.block().size != 1 || (tile.block() != wall && !tile.block().alwaysReplace)) return;

                    if(tile.block() == wall){
                        walls ++;
                    }
                }
            }

            if(walls >= 3){
                curr.setBlock(wallLarge, team);
            }
        });

        //clear path for ground units
        for(Tile tile : cores){
            Astar.pathfind(tile, spawn, t -> t.team() == state.rules.waveTeam && !t.within(tile, 25f * 8) ? 100000 : t.floor().hasSurface() ? 1 : 10, t -> !t.block().isStatic()).each(t -> {
                if(!t.within(tile, 25f * 8)){
                    if(t.team() == state.rules.waveTeam){
                        t.setBlock(Blocks.air);
                    }

                    for(Point2 p : Geometry.d8){
                        Tile other = t.nearby(p);
                        if(other != null && other.team() == state.rules.waveTeam){
                            other.setBlock(Blocks.air);
                        }
                    }
                }
            });
        }

    }
}
