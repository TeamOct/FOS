package fos.graphics;

import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import mindustry.game.EventType;

public class ShaderTextureRegion extends TextureRegion {
    public static final Seq<ShaderTextureRegion> regions = new Seq<>();

    public Texture original;

    /**
     * Texture updates every {@link ShaderTextureRegion#frequency} frame
     **/
    public int frequency = 5;
    public Shader shader;
    private Cons<Shader> shaderPrepare;

    private int counter = 0;
    private FrameBuffer frameBuffer = new FrameBuffer();

    static {
        //TODO draw or update???
        Events.run(EventType.Trigger.update, () -> regions.each(ShaderTextureRegion::updateShader));
    }

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
