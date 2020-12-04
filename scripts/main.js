const mercury = new JavaAdapter(Planet, {}, "mercury", Planets.sun, 4, 0.9);
mercury.orbitRadius = 12.0;
mercury.meshLoader = () => new SunMesh(mercury, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("101010"), Color.valueOf("202020"), Color.valueOf("161616"));
mercury.accessible = false;
mercury.hasAtmosphere = false;

Planets.serpulo.orbitRadius = 18.0;