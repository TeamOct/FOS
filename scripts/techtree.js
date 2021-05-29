const newNode = (parent, content, req, objectives) => {
  const parnode = TechTree.get(parent);
  const node = new TechTree.TechNode(parnode, content, req != null ? req : content.researchRequirements());
  var used = new ObjectSet();
  
  if (objectives != null){
    node.objectives.addAll(objectives);
  }
}

const rocketSilo = Vars.content.getByName(ContentType.block, "fos-rocket-silo");
const mechSeparator = Vars.content.getByName(ContentType.block, "fos-mechanical-separator");

const siloTerminal = Vars.content.getByName(ContentType.sector, "fos-siloTerminal");
const lsMoon = Vars.content.getByName(ContentType.sector, "fos-landingSiteMoon");

const moon = Vars.content.getByName(ContentType.planet, "fos-moon");

//region planets branch
newNode(Blocks.coreShard, Planets.serpulo, null, null);
Planets.serpulo.alwaysUnlocked = true;
newNode(Planets.serpulo, moon, null, Seq.with(new Objectives.Research(Blocks.interplanetaryAccelerator)));
//endregion

//region sectors
newNode(SectorPresets.nuclearComplex, siloTerminal, null, Seq.with(new Objectives.SectorComplete(SectorPresets.nuclearComplex), new Objectives.Research(Blocks.launchPad)));

newNode(SectorPresets.planetaryTerminal, lsMoon, null, Seq.with(new Objectives.SectorComplete(SectorPresets.planetaryTerminal), new Objectives.Research(moon)));
//endregion
//region blocks
newNode(Blocks.launchPad, rocketSilo, null, Seq.with(new Objectives.SectorComplete(siloTerminal)));

newNode(Blocks.separator, mechSeparator, null, Seq.with(new Objectives.Research(moon), new Objectives.SectorComplete(lsMoon)));