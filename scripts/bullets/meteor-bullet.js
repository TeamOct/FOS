const meteorbullet = extend(BasicBulletType, {
  speed: 4, lifetime: 60,
  width: 6, height: 8,
  damage: 3, splashDamage: 40,
  splashDamageRadius: 24,
  incendAmount: 1,
  ammoMultiplier: 5,
  targetGround: false, collidesGround: false,
  backColor: Color.valueOf("ad6b00"),
  frontColor: Color.valueOf("ff9d00"),
  sprite: "meteor-shot"
});

const meteorite = Vars.content.getByName(ContentType.item, "fos-meteorite");
Blocks.scatter.ammoTypes.put(meteorite, meteorbullet);