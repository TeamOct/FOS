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