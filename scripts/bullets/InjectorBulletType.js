const bullet = (minChance, maxChance, minHPThreshold, maxHPThreshold, collidesGuardians) => extendContent(BasicBulletType, {
  collidesTiles: false,
  speed: 4,
  lifetime: 40,
  width: 6, height: 12,
  
  chance(entity){
    var health = entity.health;
    var maxHealth = entity.maxHealth;
    if (health <= minHPThreshold){
      return maxChance;
    } else if (health >= maxHPThreshold){
      return minChance;
    } else {
      var hpRange = maxHPThreshold - minHPThreshold;
      return (health - minHPThreshold) / hpRange;
    }
  },
  hitEntity(b, entity, health){
    if (entity instanceof Unit){
      var chance = this.chance(entity);
      if (Math.random() < chance){
        entity.team = b.team;
      }
    }
  }
});

module.exports = {
  bullet: bullet
}