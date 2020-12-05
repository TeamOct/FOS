//планеты
const mercury = new JavaAdapter(Planet, {}, "mercury", Planets.sun, 4, 0.7);
mercury.orbitRadius = 12.0;
mercury.meshLoader = () => new SunMesh(mercury, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("101010"), Color.valueOf("202020"), Color.valueOf("161616"));
mercury.accessible = false;
mercury.bloom = false;
mercury.hasAtmosphere = false;

const venus = new JavaAdapter(Planet, {}, "venus", Planets.sun, 4, 0.9);
venus.orbitRadius = 21.0;
venus.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("cc7400"), Color.valueOf("e98400"), Color.valueOf("d47d00"));
venus.accessible = false;
venus.bloom = false;
venus.hasAtmosphere = false;

Planets.serpulo.orbitRadius = 28.0;

const mars = new JavaAdapter(Planet, {}, "mars", Planets.sun, 4, 0.8);
mars.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ff6058"), Color.valueOf("f24240"), Color.valueOf("f03336"));
mars.accessible = false;
mars.bloom = false;
mars.hasAtmosphere = false;

//ракетная шахта

const silo = extendContent(Block, "rocket-silo", {});
silo.buildType = () => extend(Building, {
  buildConfiguration(table){
    table.button(Icon.effect, Styles.clearTransi, () => {
      if (Vars.state.isCampaign()){
      Vars.ui.planet.showSelect(Vars.state.rules.sector, s => silo.sector = s);
      } else {
        Vars.ui.showInfo("@silo.campaign");
      }
      this.deselect();
    }).size(40);
    table.button(Icon.upOpen, Styles.clearTransi, () => {
      if ((Vars.state.isCampaign()) && silo.sector != undefined){
        silo.sector.info.waves = false;
        silo.sector.info.wasCaptured = true;
        Events.fire(new SectorCaptureEvent(silo.sector));
      } else {
        if (!Vars.state.isCampaign()){
          Vars.ui.showInfo("@silo.campaign");
          return;
        };
        Vars.ui.showInfo("@silo.nosector");
      };
      this.deselect();
    }).size(40);
  }
});