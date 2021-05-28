const newNode = (parent, content, req, objectives) => {
  const parnode = TechTree.get(parent);
  const node = new TechTree.TechNode(parnode, content, req != null ? req : content.researchRequirements());
  var used = new ObjectSet();
  
  if ((objectives != null) && !(content instanceof SectorPreset)){
    node.objectives.addAll(objectives);
  }
}

const rocketSilo = Vars.content.getByName(ContentType.block, "fos-rocket-silo");
const siloTerminal = Vars.content.getByName(ContentType.sector, "fos-siloTerminal");

newNode(Blocks.launchPad,
  rocketSilo,
  null,
  Seq.with(new Objectives.SectorComplete(siloTerminal))
);
newNode(SectorPresets.nuclearComplex,
  siloTerminal,
  null,
  Seq.with(new Objectives.SectorComplete(SectorPresets.nuclearComplex), new Objectives.Research(Blocks.launchPad), new Objectives.Research(Blocks.interplanetaryAccelerator))
);