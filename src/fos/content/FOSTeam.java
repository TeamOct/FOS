package fos.content;

import arc.Core;
import arc.graphics.Color;
import mindustry.game.Team;
import mindustry.ui.Fonts;

public class FOSTeam {
    public static Team corru, bessin;

    public static void load() {
        corru = newTeam(69, "corru", Color.valueOf("8ae3df"));
        bessin = newTeam(70, "bessin", Color.valueOf("000000"));
    }

    //modify any of 256 teams' properties
    private static Team newTeam(int id, String name, Color color) {
        Team team = Team.get(id);
        team.name = name;
        team.color.set(color);

        //team.emoji = Fonts.getUnicodeStr(team.name);

        team.palette[0] = color;
        team.palette[1] = color.cpy().mul(0.75f);
        team.palette[2] = color.cpy().mul(0.5f);

        for(int i = 0; i < 3; i++){
            team.palettei[i] = team.palette[i].rgba();
        }

        return team;
    }
}
