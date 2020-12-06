const mercury = new JavaAdapter(Planet, {}, "mercury", Planets.sun, 4, 0.7);
mercury.orbitRadius = 10.0;
mercury.meshLoader = () => new SunMesh(mercury, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("202020"), Color.valueOf("161616"), Color.valueOf("101010"));
mercury.accessible = false;
mercury.hasAtmosphere = false;

const venus = new JavaAdapter(Planet, {}, "venus", Planets.sun, 4, 0.9);
venus.orbitRadius = 20.0;
venus.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("cc7400"), Color.valueOf("e98400"), Color.valueOf("d47d00"));
venus.accessible = false;
venus.hasAtmosphere = false;

Planets.serpulo.orbitRadius = 30.0;

const mars = new JavaAdapter(Planet, {}, "mars", Planets.sun, 4, 0.8);
mars.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ff6058"), Color.valueOf("f24240"), Color.valueOf("f03336"));
mars.accessible = false;
mars.hasAtmosphere = false;

const jupiter = new JavaAdapter(Planet, {}, "jupiter", Planets.sun, 4, 2.2);
jupiter.meshLoader = () => new SunMesh(jupiter, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ffd866"), Color.valueOf("ffad00"));
jupiter.accessible = false;
jupiter.hasAtmosphere = false;

const saturn = new JavaAdapter(Planet, {}, "saturn", Planets.sun, 4, 0.8);
saturn.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ffd866"), Color.valueOf("ecffc2"), Color.valueOf("ffc2e8"));
saturn.accessible = false;
saturn.hasAtmosphere = false;

const moon = new JavaAdapter(Planet, {}, "moon", Planets.serpulo, 4, 0.7);
moon.meshLoader = () => new SunMesh(moon, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("847982"), Color.valueOf("939393"), Color.valueOf("837881"));
moon.accessible = false;
moon.hasAtmosphere = false;