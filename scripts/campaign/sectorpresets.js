const planets = require('campaign/planets');

const siloTerminal = new SectorPreset("siloTerminal", Planets.serpulo, 95);
siloTerminal.difficulty = 8;
const meteorCrater = new SectorPreset("meteorCrater", Planets.serpulo, 271);
meteorCrater.difficulty = 6;

const landingSiteMoon = new SectorPreset("landingSiteMoon", planets.moon, 221);
landingSiteMoon.difficulty = 1;

module.exports = {
  siloTerminal: siloTerminal,
  meteorCrater: meteorCrater,
  lsMoon: landingSiteMoon
}