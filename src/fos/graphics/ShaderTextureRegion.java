package fos.graphics;

import arc.Events;
import arc.func.Cons2;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.graphics.gl.*;
import arc.struct.Seq;
import mindustry.game.EventType;

// TODO it should use atlas
// TODO optimize updateShader()
// TODO don't not used regions
public class ShaderTextureRegion extends TextureRegion {
    public static final Seq<ShaderTextureRegion> regions = new Seq<>();

    public Texture original;

    /** Texture update frequency, in frames **/
    public int frequency = 5;
    public Shader shader;
    private final Cons2<Shader, Object[]> shaderPrepare;

    private int counter = 0;
    private final FrameBuffer frameBuffer = new FrameBuffer();

    /** Use to transfer shader parameters to {@link ShaderTextureRegion#shaderPrepare} **/
    public Object[] shaderPrepareParams;

    static {
        Events.run(EventType.Trigger.update, () -> regions.each(ShaderTextureRegion::updateShader));
    }

    public ShaderTextureRegion(Shader shader, Texture original, Cons2<Shader, Object[]> shaderPrepare,
                               int shaderPrepareParamsSize) {
        regions.add(this);

        shaderPrepareParams = new Object[shaderPrepareParamsSize];
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
            shaderPrepare.get(shader, shaderPrepareParams);
            frameBuffer.begin(Color.black.cpy().a(0f));

            Draw.blit(original, shader);
            Draw.flush();

            frameBuffer.end();
            set(frameBuffer.getTexture());
        }
    }
}
