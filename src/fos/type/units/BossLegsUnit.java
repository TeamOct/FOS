package fos.type.units;

import fos.content.FOSMusic;
import mindustry.Vars;
import mindustry.gen.LegsUnit;

public class BossLegsUnit extends LegsUnit {

    @Override
    public void add() {
        super.add();
        FOSMusic.luminaBoss.play();
        FOSMusic.luminaBoss.setLooping(true);
    }

    @Override
    public void killed() {
        super.killed();
        FOSMusic.luminaBoss.stop();
    }
}
