package fos.type.units;

import fos.content.FOSMusic;
import mindustry.Vars;
import mindustry.gen.LegsUnit;

public class BossLegsUnit extends LegsUnit {

    @Override
    public void add() {
        super.add();

        FOSMusic.luminaBoss.setVolume(1f);
        FOSMusic.luminaBoss.setLooping(true);
        FOSMusic.luminaBoss.play();
    }

    @Override
    public void killed() {
        super.killed();
        FOSMusic.luminaBoss.stop();
    }
}
