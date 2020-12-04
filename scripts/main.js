const mercury = new JavaAdapter(Planet, {}, "mercury", Planets.sun, 4, 0.7);
mercury.orbitRadius = 12.0;
mercury.meshLoader = () => new SunMesh(mercury, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("101010"), Color.valueOf("202020"), Color.valueOf("161616"));
mercury.accessible = false;
mercury.bloom = false;
mercury.hasAtmosphere = false;

const venus = new JavaAdapter(Planet, {}, "venus", Planets.sun, 4, 0.9);
venus.orbitRadius = 21.0;
venus.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("cc7400"), Color.valueOf("e98400"), Color.valueOf("d47d00"));
venus.accessible = false;
venus.bloom = false;
venus.hasAtmosphere = false;

Planets.serpulo.orbitRadius = 28.0;

const mars = new JavaAdapter(Planet, {}, "mars", Planets.sun, 4, 0.8);
mars.meshLoader = () => new SunMesh(venus, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ff6058"), Color.valueOf("f24240"), Color.valueOf("f03336"));
mars.accessible = false;
mars.bloom = false;
mars.hasAtmosphere = false;