package fos.type.units;

import fos.gen.*;
import mindustry.gen.*;

import static ent.anno.Annotations.EntityDef;

class EntityDefs<G> {
    @EntityDef({Unitc.class, LumoniPlayerc.class, Legsc.class}) G legsLumoniPlayerUnit;
    @EntityDef({Unitc.class, Crawlc.class, Bugc.class}) G bugCrawlUnit;
    @EntityDef({Unitc.class, Legsc.class, Bugc.class}) G bugLegsUnit;
    @EntityDef({Unitc.class, Bugc.class}) G bugUnit;
    @EntityDef({Unitc.class, Legsc.class, Burrowc.class, Bugc.class}) G bugBurrowLegsUnit;
    @EntityDef({Unitc.class, Minerc.class, BWorkerc.class}) G bWorkerMinerUnit;
}
