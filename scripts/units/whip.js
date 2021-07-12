const injectorb = require('bullets/InjectorBulletType');

const whip = extendContent(UnitType, "whip", {
  health: 150,
  hitSize: 6,
  speed: 1.2
});
whip.constructor = () => extend(UnitEntity, {});

const weapon = extendContent(Weapon, "injector", {
  bullet: injectorb.bullet(0.1, 0.25, 50, 180, false),
  x: 0, y: 0,
  reload: 60 * 20,
  ejectEffect: Fx.casing1
});
whip.weapons.add(weapon);

