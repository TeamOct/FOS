package fos;

import arc.util.io.Reads;
import mindustry.annotations.Annotations;
import mindustry.entities.units.WeaponMount;
import mindustry.io.TypeIO;

@Annotations.TypeIOHandler
public class FOSTypeIO extends TypeIO {
    public static WeaponMount[] readMounts(Reads read) {
        WeaponMount[] mounts = new WeaponMount[read.b()];

        for(int i = 0; i < mounts.length; i++){
            byte state = read.b();
            float ax = read.f(), ay = read.f();

            if(i <= mounts.length - 1){
                WeaponMount m = mounts[i];
                m.aimX = ax;
                m.aimY = ay;
                m.shoot = (state & 1) != 0;
                m.rotate = (state & 2) != 0;
            }
        }

        return mounts;
    }
}
