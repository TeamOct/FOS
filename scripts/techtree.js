function newNode(content, parentName, req, objectives){
  var parent = TechTree.all.find(t => t.content.name.equals(parentName));

  var node = new TechTree.TechNode(parent, content, req);
  node.objectives.add(objectives);
  
  parent.children.add(node);
}

const silo = Vars.content.getByName(ContentType.block, "fictional-octo-system-rocket-silo");
const siloTerminal = Vars.content.getByName(ContentType.sector, "fictional-octo-system-siloTerminal");

newNode(silo, "launch-pad", silo.researchRequirements(), Seq.with(new Objectives.SectorComplete(siloTerminal)));
newNode(siloTerminal, nuclearComplex, null, Seq.with(new Objectives.SectorComplete(SectorPresets.impact0078), new Objectives.SectorComplete(SectorPresets.nuclearComplex), new Objectives.Research(Blocks.launchPad)));