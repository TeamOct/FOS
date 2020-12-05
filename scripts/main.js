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
venus.hasAtmosphere = false;

Planets.serpulo.orbitRadius = 28.0;

const mars = new JavaAdapter(Planet, {}, "mars", Planets.sun, 4, 0.8);
mars.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ff6058"), Color.valueOf("f24240"), Color.valueOf("f03336"));
mars.accessible = false;
mars.hasAtmosphere = false;

const jupiter = new JavaAdapter(Planet, {}, "jupiter", Planets.sun, 4, 2.2);
jupiter.meshLoader = () => new SunMesh(jupiter, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ffd866"), Color.valueOf("ffad00"));
jupiter.accessible = false;
jupiter.hasAtmosphere = false;

const saturn = new JavaAdapter(Planet, {}, "saturn", Planets.sun, 4, 0.8);
saturn.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ffd866"), Color.valueOf("ecffc2"), Color.valueOf("ffc2e8"));
saturn.accessible = false;
saturn.hasAtmosphere = false;

//ракетная шахта

const silo = extendContent(Block, "rocket-silo", {
  icons(){
    return [
      Core.atlas.find(this.name),
      Core.atlas.find(this.name + "-team")
      ];
  }
});
silo.buildType = () => extend(Building, {
  buildConfiguration(table){
    table.button(Icon.effect, Styles.clearTransi, () => {
      if (Vars.state.isCampaign()){
      Vars.ui.planet.showSelect(Vars.state.rules.sector, s => silo.sector = s);
      } else {
        Vars.ui.showInfo("@silo.campaignonly");
      }
      this.deselect();
    }).size(40);
    table.button(Icon.upOpen, Styles.clearTransi, () => {
      if ((Vars.state.isCampaign()) && (silo.sector != undefined) && (this.consValid())){
        this.items.clear();
        silo.sector.info.waves = false;
        silo.sector.info.wasCaptured = true;
        Fx.launchPod.at(this);
        Effect.shake(3, 3, this);
        Events.fire(new SectorCaptureEvent(silo.sector));
      } else {
        if (!Vars.state.isCampaign()){
          Vars.ui.showInfo("@silo.campaignonly");
          return;
        };
        if (silo.sector == undefined){
          Vars.ui.showInfo("@silo.nosector");
        };
      };
      this.deselect();
    }).size(40);
  },
  draw(){
    this.super$draw();
    Draw.rect(Core.atlas.find(silo.name), this.x, this.y);
    
    if (this.consValid()){
    Draw.rect(Core.atlas.find("launchpod"), this.x, this.y);
      var rad = 20 * 0.74;
      var scl = 2;
      Draw.z(Layer.bullet - 0.0001);
      Lines.stroke(1.75, this.team.color);
      Lines.square(this.x, this.y, rad * 1.22, 45);
      Lines.stroke(3, this.team.color);
      Lines.square(this.x, this.y, rad, Time.time / scl);
      Lines.square(this.x, this.y, rad, -Time.time / scl);
    };
    Draw.color(this.team.color);
    Draw.rect(Core.atlas.find(silo.name + "-team"), this.x, this.y);
  }
});