package fos.graphics;

import arc.Core;
import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

public class ShaderTextureRegion extends TextureRegion {
    public static final Seq<ShaderTextureRegion> regions = new Seq<>();

    public Texture original;

    /**
     * Texture updates every {@link ShaderTextureRegion#frequency} frame
     **/
    public int frequency = 5;

    static {
        Events.run(EventType.Trigger.draw, () -> {
            regions.each(ShaderTextureRegion::updateShader);
        });
    }

    public Shader shader;
    Cons<Shader> shaderPrepare;

    public ShaderTextureRegion(Shader shader, Texture original, Cons<Shader> shaderPrepare) {
        regions.add(this);
        this.shader = shader;
        this.shaderPrepare = shaderPrepare;

        this.original = original;
        set(original);
    }

    public void remove() {
        regions.remove(this);
    }

    int counter = 0;
    FrameBuffer frameBuffer = new FrameBuffer();
    public void updateShader() {
        counter++;
        if ((counter %= frequency) == 0) {
            Draw.flush();
            Draw.reset();
            frameBuffer.resize(width, height);
            shaderPrepare.get(shader);
            frameBuffer.begin(Color.black.cpy().a(0f));

            Draw.blit(original, shader);
            Draw.flush();

            frameBuffer.end();
            set(frameBuffer.getTexture());
        }
    }
}
