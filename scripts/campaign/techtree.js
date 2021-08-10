const newNode = (parent, content, req, objectives) => {
  var parnode = TechTree.get(parent);
  var node = new TechTree.TechNode(parnode, content, req != null ? req : content.researchRequirements());
  var used = new ObjectSet();
  
  if (objectives != null){
    node.objectives.addAll(objectives);
  }
}

const rocketSilo = Vars.content.getByName(ContentType.block, "fos-rocket-silo");
const mechSeparator = Vars.content.getByName(ContentType.block, "fos-mechanical-separator");

const sectors = require('campaign/sectorpresets');
const planets = require('campaign/planets');

//region planets branch
newNode(Blocks.coreShard, Planets.serpulo, null, null);
Planets.serpulo.alwaysUnlocked = true;
newNode(Planets.serpulo, planets.moon, null, Seq.with(new Objectives.Research(Blocks.interplanetaryAccelerator)));
//endregion

//region sectors
newNode(SectorPresets.nuclearComplex, sectors.siloTerminal, null, Seq.with(new Objectives.SectorComplete(SectorPresets.nuclearComplex), new Objectives.Research(Blocks.launchPad)));
newNode(SectorPresets.stainedMountains, sectors.meteorCrater, null, Seq.with(new Objectives.SectorComplete(SectorPresets.stainedMountains), new Objectives.Research(Blocks.laserDrill)));

newNode(SectorPresets.planetaryTerminal, sectors.lsMoon, null, Seq.with(new Objectives.SectorComplete(SectorPresets.planetaryTerminal), new Objectives.Research(planets.moon)));
//endregion
//region blocks
newNode(Blocks.launchPad, rocketSilo, null, Seq.with(new Objectives.SectorComplete(sectors.siloTerminal)));

newNode(Blocks.separator, mechSeparator, null, Seq.with(new Objectives.Research(planets.moon), new Objectives.SectorComplete(sectors.lsMoon)));