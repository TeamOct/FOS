const mw_miner = extendContent(UnitType, "mw-miner", {});
mw_miner.constructor = () => extend(UnitEntity, {});
mw_miner.defaultController = () => extend(MinerAI, {});

mw_miner.health = 200;
mw_miner.rotateSpeed = 10;
mw_miner.mineTier = 1;
mw_miner.mineSpeed = 0.5;
mw_miner.range = 30;

this.global.mwMiner = mw_miner;