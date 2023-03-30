package fos.type.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.Shader;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import fos.graphics.FOSShaders;
import fos.graphics.StaticBlockAnimator;
import mindustry.world.blocks.environment.OreBlock;

public class ShaderOreBlock<T extends Shader> extends OreBlock implements StaticBlockAnimator.StaticAnimatedBlock {
    public static Rand r = new Rand();

    public T shader;

    public ShaderOreBlock(String name, T shader) {
        super(name);
        this.shader = shader;
        new StaticBlockAnimator<T>(name, 0, this, shader);
    }

    @Override
    public void drawAnimation(Vec2 pos) {
        r.setSeed((long) (pos.x * Mathf.sin(pos.y) /
                Mathf.tan(Mathf.pow(pos.x, pos.len()), pos.angle(), pos.angleRad())));

        Draw.rect(variantRegions[r.random(variants)], pos.x, pos.y);
    }
}
