const effect = new Effect(60, e => {
  Draw.color(Color.valueOf("51a0b0"), Color.valueOf("8ae3df"), e.fin());
  Fill.circle(e.x, e.y, (7 - e.fin() * 7)/4);
})

const hacked = (damage) => extendContent(StatusEffect, "hacked", {
  effectChance: 1,
  permanent: true,
  healthMultiplier: 0.5,
  effect: effect, 
  damage: damage
})

module.exports = {
  hacked: hacked
}