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
mars.meshLoader = () => new SunMesh(mars, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("ff6058"), Color.valueOf("f24240"), Color.valueOf("f03336"));
mars.accessible = false;
mars.hasAtmosphere = false;

const polaris = new JavaAdapter(Planet, {}, "polaris", Planets.sun, 4, 0.6);
polaris.meshLoader = () => new SunMesh(polaris, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("7b68ee"), Color.valueOf("6a5acd"), Color.valueOf("4169e1"));
polaris.accessible = false;
polaris.hasAtmosphere = false;

const moon = new JavaAdapter(Planet, {}, "moon", Planets.serpulo, 4, 0.7);
moon.meshLoader = () => new SunMesh(moon, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("847982"), Color.valueOf("939393"), Color.valueOf("837881"));
moon.accessible = false;
moon.hasAtmosphere = false;

const saylot = new JavaAdapter(Planet, {}, "saylot", Planets.polaris, 4, 0.7);
saylot.meshLoader = () => new SunMesh(saylot, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("000000"), Color.valueOf("ff9900"), Color.valueOf("323332"));
saylot.accessible = false;
saylot.hasAtmosphere = false;

const ko = new JavaAdapter(Planet, {}, "ko", Planets.polaris, 4, 0.7);
ko.meshLoader = () => new SunMesh(ko, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("000000"), Color.valueOf("ff9900"), Color.valueOf("323332"));
ko.accessible = false;
ko.hasAtmosphere = false;
