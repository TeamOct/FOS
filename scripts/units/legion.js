function legion(unitType, amount, name) {
  var unit = extendContent(UnitType, name, {
    health: 25000,
    armor: 25,
    hitSize: 25,
    speed: 0.1,
    flying: false,
    canBoost: false
  });
  
  var angle = 0;
  for(var i = 0; i < amount; i++){
    var x = Mathf.cos(angle) * 32;
    var y = Mathf.sin(angle) * 32;
    unit.abilities.add(new UnitSpawnAbility(unitType, 600, x, y));
    angle += Mathf.PI2 / amount;
  }
  
  unit.constructor = () => extend(MechUnit, {}); 
  
  return unit;
}

const legion1 = legion(UnitTypes.atrax, 8, "legion1");
const legion2 = legion(UnitTypes.atrax, 16, "legion2");
const legion3 = legion(UnitTypes.spiroct, 12, "legion3");
const legion4 = legion(UnitTypes.arkyid, 3, "legion4");
const legion5 = legion(UnitTypes.arkyid, 8, "legion5");

module.exports = {
  legion1: legion1,
  legion2: legion2,
  legion3: legion3,
  legion4: legion4,
  legion5: legion5
}