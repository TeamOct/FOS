var meteorite = Vars.content.getByName(ContentType.item, "fictional-octo-system-meteorite");

const m_shot = extend(BasicBulletType, {});
m_shot.backColor = Color.valueOf("ad6b00");
m_shot.frontColor = Color.valueOf("ff9d00");

m_shot.width = m_shot.height = 8;
m_shot.damage = 16;
m_shot.splashDamage = 16;
m_shot.splashDamageRadius = 8;
m_shot.sprite = Core.atlas.find("fictional-octo-system-meteor-shot");

Blocks.scatter.ammo(Items.scrap, Bullets.flakScrap, Items.lead, Bullets.flakLead, Items.metaglass, Bullets.flakGlass, meteorite, m_shot);