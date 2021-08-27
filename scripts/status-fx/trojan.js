const trojan = (damage) => extendContent(StatusEffect, "trojan", {
  effectChance: 1,
  permanent: true,
  healthMultiplier: 0.5,
  damageMultiplier: 0.5,
  damage: damage
})

module.exports = {
  trojan: trojan
}