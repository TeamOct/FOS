package fos.type.blocks.environment;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Strings;
import fos.core.FOSVars;
import fos.graphics.*;
import fos.graphics.cachelayers.AnimatedOreCacheLayer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.graphics.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.OreBlock;

public class AnimatedOreBlock extends OreBlock {
    public int frames = 1;
    protected TextureRegion[] regions;

    // TODO make base shader for animated ore block
    public Shader shader;

    public CacheLayer layer;

    public AnimatedOreBlock(String name, Shader shader) {
        super(name);
        layer = new AnimatedOreCacheLayer(shader);
    }
}
