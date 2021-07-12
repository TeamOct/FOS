const bullet = (minChance, maxChance, minHPThreshold, maxHPThreshold, collidesGuardians) => extendContent(BasicBulletType, {
  target: Units.closestTarget(this.team, this.x, this.y, 160),
  hpPercent: target.health / target.maxHealth,
  health: target.health,
  maxHealth: target.maxHealth,
  chance(){
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
    if ((entity instanceof Unit) && (Math.random() < chance())){
      entity.team(this.team);
    }
  }
})

module.exports = {
  bullet: bullet
}