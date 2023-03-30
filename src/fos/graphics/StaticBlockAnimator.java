package fos.graphics;

import arc.Core;
import arc.func.Cons2;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.graphics.gl.Shader;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.world.Block;

/**
 * Use for animate Blocks in cache layer. That blocks must implement {@link StaticAnimatedBlock}.
 * @author nekit508
 **/
public class StaticBlockAnimator<T extends Shader> {
    public static final Seq<StaticBlockAnimator<? extends Shader>> animators = new Seq<>();

    public static void renderAnimators() {
        animators.each(StaticBlockAnimator::draw);
    }

    /**
     * Sources for rendering ({@link StaticAnimatedBlock#drawAnimation(Vec2)} execution) position: <ul>
     *     <li>{@link AnimationSource#LIST} - uses positions from {@link StaticBlockAnimator#drawPlaces}</li>
     *     <li>{@link AnimationSource#WORLD} - uses positions of blocks equal {@link StaticBlockAnimator#owner} from the {@link Vars#world}</li>
     * </ul>
     * <h3>In editor always uses WORLD source.
     **/
    public AnimationSource source = AnimationSource.LIST;
    public Seq<Vec2> drawPlaces = new Seq<>();
    /** Using for comparison **/
    public Block owner;
    /** Using for drawing **/
    public StaticAnimatedBlock drawer;

    public final String name;

    public FrameBuffer buffer = new FrameBuffer();

    public T shader;
    public Cons2<T, Object[]> shaderPrepare = (s, p) -> {};
    public Object[] shaderParams;

    public StaticBlockAnimator(String name, int shaderParamsSize, Block owner, T shader) {
        this.name = name;
        this.owner = owner;
        this.drawer = (StaticAnimatedBlock) owner;
        this.shader = shader;
        shaderParams = new Object[shaderParamsSize + 2];

        animators.add(this);
    }

    public void remove() {
        animators.remove(this);
    }

    public void add(Vec2 pos) {
        drawPlaces.add(pos);
    }

    public void remove(Vec2 pos) {
        drawPlaces.remove(pos);
    }

    public void draw(){
        buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

        Draw.color();
        buffer.begin(Color.clear);
        Draw.sort(false);
        Gl.blendEquationSeparate(Gl.funcAdd, Gl.max);
        Blending.normal.apply();

        if (Vars.state.isEditor() || source == AnimationSource.WORLD)
            drawFromWorld();
        else if (source == AnimationSource.LIST)
            drawFromList();

        Draw.reset();
        Draw.sort(true);
        buffer.end();
        Gl.blendEquationSeparate(Gl.funcAdd, Gl.funcAdd);

        Draw.color();

        shaderPrepare.get(shader, shaderParams);
        buffer.blit(shader);
    }

    Vec2 tempPoint = new Vec2();
    void drawFromWorld() {
        Vars.world.tiles.eachTile(t -> {
            if ((t.block() != null && t.block().equals(owner)) || (t.floor() != null && t.floor().equals(owner)) ||
                    (t.overlay() != null && t.overlay().equals(owner)))
                drawer.drawAnimation(tempPoint.set(t.worldx(), t.worldy()));
        });
    }

    void drawFromList() {
        drawPlaces.each(p -> {
            drawer.drawAnimation(p);
        });
    }

    public interface StaticAnimatedBlock {
        void drawAnimation(Vec2 pos);
    }

    public enum AnimationSource {
        WORLD,
        LIST
    }
}
