package fos.type.units.types;

import arc.Core;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Log;
import fos.core.FOSVars;
import fos.gen.EntityRegistry;
import fos.world.draw.FOSStats;
import mindustry.gen.*;
import mindustry.graphics.MultiPacker;
import mindustry.type.*;

import static mindustry.graphics.MultiPacker.PageType;

public class FOSUnitType extends UnitType {
    /**
     * Physical damage reduction fraction, 0 to 1. Applied before armour.
     * Status effect damage ignores this.
     */
    public float absorption = 0f;

    @SuppressWarnings("unchecked")
    public <T extends Unit> FOSUnitType(String name, Class<T> type) {
        super(name);
        outlineColor = Color.valueOf("2b2f36");
        constructor = EntityRegistry.content(name, type, n -> EntityMapping.map(this.name));
        if (constructor == null) throw new IllegalArgumentException("Unit entity class `" + type + "` not registered.");
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(FOSStats.damageReduction, Mathf.round(absorption * 100) + "%");
    }

    @Override
    public void createIcons(MultiPacker packer) {
        //if(internal) return;

        try {
            Unit sample = constructor.get();

            Func<Pixmap, Pixmap> outline = i -> i.outline(outlineColor, 3);
            Cons<TextureRegion> outliner = t -> {
                if(t != null && t.found()) {
                    packer.add(PageType.main, t.asAtlas().name, outline.get( Core.atlas.getPixmap(t).crop() ));
                }
            };

            Seq<TextureRegion> toOutline = new Seq<>();
            getRegionsToOutline(toOutline);

            for (TextureRegion region : toOutline) {
                var name = region.asAtlas().name;
                if (!FOSVars.outlined.add(name)) {
                    Log.warn("[FOS] Outline already exists for part: @, skipping.", name);
                    continue;
                }
                Pixmap pix = Core.atlas.getPixmap(region).crop().outline(outlineColor, outlineRadius);
                packer.add(PageType.main, name, pix);
                Log.info("[FOS] Generated outline for part: @.", name);
            }

            Seq<Weapon> weps = weapons.copy();
            weps.each(Weapon::load);
            weps.removeAll(w -> !w.region.found());

            for (Weapon weapon : weps) {
                if (packer.has(weapon.name) && !(this instanceof LumoniPlayerUnitType)) { // don't outline modular weapons twice
                    if (!FOSVars.outlined.add(weapon.name)) {
                        Log.warn("[FOS] Outline already exists for weapon: @, skipping.", weapon.name);
                        continue;
                    }
                    //only non-top weapons need separate outline sprites (this is mostly just mechs)
                    if (!weapon.top || weapon.parts.contains(p -> p.under)) {
                        packer.add(PageType.main, weapon.name + "-outline", outline.get( Core.atlas.getPixmap(weapon.name).crop() ));
                    } else {
                        //replace weapon with outlined version, no use keeping standard around
                        outliner.get(weapon.region);
                    }
                    Log.info("[FOS] Generated outline for weapon: @.", weapon.name);
                }
            }

            //generate tank animation
            if (sample instanceof Tankc) {
                Pixmap pix = Core.atlas.getPixmap(treadRegion).crop();

                for(int r = 0; r < treadRects.length; r++){
                    Rect treadRect = treadRects[r];
                    //slice is always 1 pixel wide
                    Pixmap slice = pix.crop((int)(treadRect.x + pix.width/2f), (int)(treadRect.y + pix.height/2f), 1, (int)treadRect.height);
                    int frames = treadFrames;
                    for(int i = 0; i < frames; i++){
                        int pullOffset = treadPullOffset;
                        Pixmap frame = new Pixmap(slice.width, slice.height);
                        for(int y = 0; y < slice.height; y++){
                            int idx = y + i;
                            if(idx >= slice.height){
                                idx -= slice.height;
                                idx += pullOffset;
                                idx = Mathf.mod(idx, slice.height);
                            }

                            frame.setRaw(0, y, slice.getRaw(0, idx));
                        }
                        packer.add(PageType.main, name + "-treads" + r + "-" + i, frame);
                    }
                }
            }

            outliner.get(jointRegion);
            outliner.get(footRegion);
            outliner.get(legBaseRegion);
            outliner.get(baseJointRegion);
            if (sample instanceof Legsc) outliner.get(legRegion);
            if (sample instanceof Tankc) outliner.get(treadRegion);

            Pixmap image = segments > 0 ? Core.atlas.getPixmap(segmentRegions[0]).crop() : outline.get(Core.atlas.getPixmap(previewRegion).crop());

            Func<Weapon, Pixmap> weaponRegion = weapon -> Core.atlas.has(weapon.name + "-preview") ? Core.atlas.getPixmap(weapon.name + "-preview").crop() : Core.atlas.getPixmap(weapon.region).crop();
            Cons2<Weapon, Pixmap> drawWeapon = (weapon, pixmap) -> {
                image.draw(pixmap,
                    (int) (weapon.x * 4 / Draw.scl + image.width / 2f - weapon.region.width / 2f),
                    (int) (-weapon.y * 4 / Draw.scl + image.height / 2f - weapon.region.height / 2f),
                    true);

                if (weapon.mirror) {
                    image.draw(pixmap.flipX(),
                        (int) (-weapon.x * 4 / Draw.scl + image.width / 2f - weapon.region.width / 2f),
                        (int) (-weapon.y * 4 / Draw.scl + image.height / 2f - weapon.region.height / 2f),
                        true);
                }
            };

            boolean anyUnder = false;

            //draw each extra segment on top before it is saved as outline
            if (sample instanceof Crawlc) {
                for (int i = 0; i < segments; i++) {
                    packer.add(PageType.main, name + "-segment-outline" + i, outline.get( Core.atlas.getPixmap(segmentRegions[i]).crop() ));

                    if (i > 0) {
                        drawCenter(image, Core.atlas.getPixmap(segmentRegions[i]).crop());
                    }
                }
                packer.add(PageType.main, name, image);
            }

            //outline is currently never needed, although it could theoretically be necessary
            if (needsBodyOutline()) {
                packer.add(PageType.main, name + "-outline", image);
            } else if (segments == 0) {
                packer.add(PageType.main, name, outline.get(Core.atlas.getPixmap(region).crop()));
            }

            //draw weapons that are under the base
            for (Weapon weapon : weps.select(w -> w.layerOffset < 0)) {
                drawWeapon.get(weapon, outline.get(weaponRegion.get(weapon)));
                anyUnder = true;
            }

            //draw over the weapons under the image
            if (anyUnder) {
                image.draw(outline.get(Core.atlas.getPixmap(previewRegion).crop()), true);
            }

            //draw treads
            if (sample instanceof Tankc) {
                Pixmap treads = outline.get(Core.atlas.getPixmap(treadRegion).crop());
                image.draw(treads, image.width / 2 - treads.width / 2, image.height / 2 - treads.height / 2, true);
                image.draw(Core.atlas.getPixmap(previewRegion), true);
            }

            //draw mech parts
            if (sample instanceof Mechc) {
                drawCenter(image, Core.atlas.getPixmap(baseRegion).crop());
                drawCenter(image, Core.atlas.getPixmap(legRegion).crop());
                drawCenter(image, Core.atlas.getPixmap(legRegion).crop().flipX());
                image.draw(Core.atlas.getPixmap(previewRegion), true);
            }

            //draw weapon outlines on base
            for (Weapon weapon : weps) {
                //skip weapons under unit
                if(weapon.layerOffset < 0) continue;

                drawWeapon.get(weapon, outline.get(weaponRegion.get(weapon)));
            }

            //draw base region on top to mask weapons
            if (drawCell) image.draw(Core.atlas.getPixmap(previewRegion), true);

            if (drawCell) {
                Pixmap baseCell = Core.atlas.getPixmap(cellRegion).crop();
                Pixmap cell = baseCell.copy();

                cell.replace(in -> in == 0xffffffff ? 0x8ae3dfff : in == 0xdcc6c6ff || in == 0xdcc5c5ff ? 0x51a0b0ff : 0);

                image.draw(cell, image.width / 2 - cell.width / 2, image.height / 2 - cell.height / 2, true);
            }

            for (Weapon weapon : weps) {
                //skip weapons under unit
                if (weapon.layerOffset < 0) continue;

                Pixmap reg = weaponRegion.get(weapon);
                Pixmap wepReg = weapon.top ? outline.get(reg) : reg;

                drawWeapon.get(weapon, wepReg);

                if (weapon.cellRegion.found()) {
                    Pixmap weaponCell = Core.atlas.getPixmap(weapon.cellRegion).crop();
                    weaponCell.replace(in -> in == 0xffffffff ? 0x8ae3dfff : in == 0xdcc6c6ff || in == 0xdcc5c5ff ? 0x51a0b0ff : 0);
                    drawWeapon.get(weapon, weaponCell);
                }
            }

            packer.add(PageType.ui, name + "-full", image);
        } catch (IllegalArgumentException e) {
            Log.err("WARNING: Skipping unit @: @", name, e.getMessage());
        }
    }

    void drawCenter(Pixmap pix, Pixmap other) {
        pix.draw(other, pix.width/2 - other.width/2, pix.height/2 - other.height/2, true);
    }
}
