//init for future use
const mechSeparator = extendContent(Separator, "mechanical-separator", {});
const meteorite = extendContent(Item, "meteorite", {});

require('bullets/meteor-bullet');

require('campaign/planets');
require('campaign/rocket-silo');
require('campaign/sectorpresets');
require('campaign/techtree');

require('units/legion');
require('units/mw-miner');
require('units/whip');