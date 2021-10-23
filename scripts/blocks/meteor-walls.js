const spark = extend(BasicBulletType, {
  damage: 40,
  speed: 5, lifetime: 20,
  width: 5, height: 5,
  incendAmount: 1,
  backColor: Color.valueOf("ad6b00"),
  frontColor: Color.valueOf("ff9d00"),
});

const meteorWall = extendContent(Wall, "meteorite-wall", {});
meteorWall.buildType = () => extend(Wall.WallBuild, meteorWall, {
  collision(bullet){
    this.super$collision(bullet);
    
    if (Mathf.chance(0.4)){
      spark.create(this, this.team, this.x, this.y, this.angleTo(bullet.owner) + Mathf.range(10));
    };
    return true;
  }
})
const meteorWallLarge = extend(Wall, "meteorite-wall-large", {});
meteorWallLarge.buildType = () => extendContent(Wall.WallBuild, meteorWallLarge, {
  collision(bullet){
    this.super$collision(bullet);
    
    if (Mathf.chance(0.4)){
      spark.create(this, this.team, this.x, this.y, this.angleTo(bullet.owner) + Mathf.range(10));
    };
    return true;
  }
});