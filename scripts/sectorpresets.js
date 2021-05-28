//region planets
const moon = Vars.content.getByName(ContentType.planet, "fos-moon");
//endregion

const siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95);
siloTerminal.difficulty = 8;

const landingSiteMoon = new SectorPreset("landingSiteMoon", moon, 221);
landingSiteMoon.difficulty = 1;