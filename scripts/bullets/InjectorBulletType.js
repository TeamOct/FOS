const status = require('status-fx/hacked');

const bullet = (minChance, maxChance, minHPThreshold, maxHPThreshold, attacksGuardians) => extendContent(BasicBulletType, {
  collidesTiles: false,
  speed: 8,
  lifetime: 20,
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
      //check whether a unit is a boss
      var statuses = entity.statuses;
      for (var i = 0; i < statuses.size; i++){
        if ((statuses.get(i) == StatusEffects.boss) && !attacksGuardians) return;
      }
      
      var chance = this.chance(entity);
      if (Math.random() < chance){
        entity.team = b.team;
        entity.apply(status.hacked(entity.health / (60*20)));
      }
    }
  }
});

module.exports = {
  bullet: bullet
}