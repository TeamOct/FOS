const nodenew = (parent, content, req, objectives) => {
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
nodenew(Blocks.coreShard, Planets.serpulo, null, null);
Planets.serpulo.alwaysUnlocked = true;
nodenew(Planets.serpulo, planets.moon, null, Seq.with(new Objectives.Research(Blocks.interplanetaryAccelerator)));
//endregion

//region sectors
nodenew(SectorPresets.nuclearComplex, sectors.siloTerminal, null, Seq.with(new Objectives.SectorComplete(SectorPresets.nuclearComplex), new Objectives.Research(Blocks.launchPad)));
nodenew(SectorPresets.stainedMountains, sectors.meteorCrater, null, Seq.with(new Objectives.SectorComplete(SectorPresets.stainedMountains), new Objectives.Research(Blocks.laserDrill)));

nodenew(SectorPresets.planetaryTerminal, sectors.lsMoon, null, Seq.with(new Objectives.SectorComplete(SectorPresets.planetaryTerminal), new Objectives.Research(planets.moon)));
//endregion
//region blocks
nodenew(Blocks.launchPad, rocketSilo, null, Seq.with(new Objectives.SectorComplete(sectors.siloTerminal)));

nodenew(Blocks.separator, mechSeparator, null, Seq.with(new Objectives.Research(planets.moon), new Objectives.SectorComplete(sectors.lsMoon)));