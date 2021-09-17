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

//planet Zuila
const marsgen = extend(PlanetGenerator, {
    rawHeight(position){
        position = Tmp.v33.set(position).scl(this.scl);
        return (Mathf.pow(this.noise.octaveNoise3D(7, 0.5, 1 / 3, position.x, position.y, position.z), 2.3) + this.waterOffset) / (1 + this.waterOffset);
    },

    getHeight(position){
        var height = this.rawHeight(position);
        return Math.max(height, this.water);
    },

    getColor(position){
        var block = this.getBlock(position);
        if(block == null) return Blocks.sand.mapColor;
        Tmp.c1.set(block.mapColor).a = 1 - block.albedo;
        
        return Tmp.c1;
    },

    genTile(position, tile){
        tile.floor = this.getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(this.rid.getValue(position.x, position.y, position.z, 22) > 0.32){
            tile.block = Blocks.air;
        };
    },

    getBlock(position){
        var arr = this.arr;
        var scl = this.scl;
        
        var height = this.rawHeight(position);
        Tmp.v31.set(position);
        
        position = Tmp.v33.set(position).scl(scl);
        var rad = this.scl;
        var temp = Mathf.clamp(Math.abs(position.y * 2) / rad);
        var tnoise = this.noise.octaveNoise3D(7, 0.56, 1 / 3, position.x, position.y + 999, position.z);
        
        temp = Mathf.lerp(temp, tnoise, 0.5);
        height *= 1.2;
        height = Mathf.clamp(height);

        var res = arr[Mathf.clamp(Math.floor(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp(Math.floor(height * arr[0].length), 0, arr[0].length - 1)];
        return res;
    },
    
    noiseOct(x, y, octaves, falloff, scl){
        var v = this.sector.rect.project(x, y).scl(5);
        return this.noise.octaveNoise3D(octaves, falloff, 1 / scl, v.x, v.y, v.z);
    },

    generateSector(sec){
      
    },

    generate(tiles, sec){
        this.tiles = tiles;
        this.sector = sec;
        
        const rand = this.rand;
        rand.setSeed(sec.id);
        
        //tile, sector
        var gen = new TileGen();
        this.tiles.each((x, y) => {
            gen.reset();
            var position = this.sector.rect.project(x / tiles.width, y / tiles.height);

            this.genTile(position, gen);
            tiles.set(x, y, new Tile(x, y, gen.floor, gen.overlay, gen.block));
        });
        
        const Room = {
            x: 0, y: 0, radius: 0,
            connected: new ObjectSet(),

            connect(to){
                if(this.connected.contains(to)) return;

                this.connected.add(to);
                
                var nscl = rand.random(20, 60);
                var stroke = rand.random(4, 12);
                
                marsgen.brush(marsgen.pathfind(this.x, this.y, to.x, to.y, tile => (tile.solid() ? 5 : 0) + marsgen.noiseOct(tile.x, tile.y, 1, 1, 1 / nscl) * 60, Asmud.manhattan), stroke);
            }
        };
        
        const setRoom = (x, y, radius) => {
            var room = Object.create(Room);
            
            room.x = x;
            room.y = y;
            room.radius = radius;
            
            return room;
        };

        this.cells(4);
        this.distort(10, 12);

        this.width = this.tiles.width;
        this.height = this.tiles.height;

        var constraint = 1.3;
        var radius = this.width / 2 / Mathf.sqrt3;
        var rooms = rand.random(2, 5);
        var roomseq = new Seq();

        for(var i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360), rand.random(radius / constraint));
            var rx = Math.floor(this.width / 2 + Tmp.v1.x);
            var ry = Math.floor(this.height / 2 + Tmp.v1.y);
            var maxrad = radius - Tmp.v1.len();
            var rrad = Math.floor(Math.min(rand.random(9, maxrad / 2), 30));
            
            roomseq.add(setRoom(rx, ry, rrad));
        };

        var spawn = null;
        var enemies = new Seq();
        var enemySpawns = rand.random(1, Math.max(Mathf.floor(this.sector.threat * 4), 1));
        
        var offset = rand.nextInt(360);
        var length = this.width / 2.55 - rand.random(13, 23);
        var angleStep = 5;
        var waterCheckRad = 5;
        
        for(var i = 0; i < 360; i += angleStep){
            var angle = offset + i;
            var cx = Math.floor(this.width / 2 + Angles.trnsx(angle, length));
            var cy = Math.floor(this.height / 2 + Angles.trnsy(angle, length));

            var waterTiles = 0;

            for(var rx = -waterCheckRad; rx <= waterCheckRad; rx++){
                for(var ry = -waterCheckRad; ry <= waterCheckRad; ry++){
                    var tile = this.tiles.get(cx + rx, cy + ry);
                    
                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles++;
                    };
                };
            };

            if(waterTiles <= 4 || (i + angleStep >= 360)){
                spawn = setRoom(cx, cy, rand.random(10, 18));
                roomseq.add(spawn);

                for(var j = 0; j < enemySpawns; j++){
                    var enemyOffset = rand.range(60);
                    
                    Tmp.v1.set(cx - this.width / 2, cy - this.height / 2).rotate(180 + enemyOffset).add(this.width / 2, this.height / 2);
                    var espawn = setRoom(Math.floor(Tmp.v1.x), Math.floor(Tmp.v1.y), rand.random(10, 16));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                };

                break;
            };
        };

        roomseq.each(room => this.erase(room.x, room.y, room.radius));

        var connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(var i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        };

        roomseq.each(room => spawn.connect(room));

        this.cells(1);
        this.distort(10, 6);

        this.inverseFloodFill(this.tiles.getn(spawn.x, spawn.y));

        var ores = Seq.with(Blocks.oreCopper, Blocks.oreLead);
        var poles = Math.abs(this.sector.tile.v.y);
        var nmag = 0.5;
        var scl = 1;
        var addscl = 1.3;

        if(this.noise.octaveNoise3D(2, 0.5, scl, this.sector.tile.v.x, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.25 * addscl){
            ores.add(Blocks.oreCoal);
        };

        if(this.noise.octaveNoise3D(2, 0.5, scl, this.sector.tile.v.x + 1, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.5 * addscl){
            ores.add(Blocks.oreTitanium);
        };

        if(this.noise.octaveNoise3D(2, 0.5, scl, this.sector.tile.v.x + 2, this.sector.tile.v.y, this.sector.tile.v.z) * nmag + poles > 0.7 * addscl){
            ores.add(Blocks.oreThorium);
        };

        if(rand.chance(0.25)){
            ores.add(Blocks.oreScrap);
        };
        
        if(rand.chance(0.7)){
          ores.add(Vars.content.getByName(ContentType.block, "goldmod-ore-goldOre"));
        }

        var frequencies = new FloatSeq();
        for(var i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1, 0.01) - i * 0.01 + poles * 0.04);
        };

        this.pass((x, y) => {
            if(!this.floor.asFloor().hasSurface()) return;

            var offsetX = x - 4, offsetY = y + 23;
            for(var i = ores.size - 1; i >= 0; i--){
                var entry = ores.get(i);
                var freq = frequencies.get(i);
                
                if(Math.abs(0.5 - this.noiseOct(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.22 + i * 0.01 &&
                    Math.abs(0.5 - this.noiseOct(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37 + freq){
                    this.ore = entry;
                    break;
                };    
            };

            if(this.ore == Blocks.oreScrap && rand.chance(0.33)){
                this.floor = Blocks.metalFloorDamaged;
            };
        });

        this.trimDark();
        this.median(2);
        this.tech();
        this.pass((x, y) => {
            //random boulder
            if(this.floor == Blocks.stone){
                if(Math.abs(0.5 - this.noiseOct(x - 90, y, 4, 0.8, 65)) > 0.02){
                    this.floor = Blocks.boulder;
                };
            };

            if(this.floor != null && this.floor != Blocks.stone && this.floor != Blocks.dirt && this.floor.asFloor().hasSurface()){
                var noise = this.noiseOct(x + 782, y, 5, 0.75, 260);
                if(noise > 0.72){
                    this.floor = noise > 0.78 ? Blocks.water : (this.floor == Blocks.sand ? Blocks.sandWater : Blocks.sandWater);
                    this.ore = Blocks.air;
                }else if(noise > 0.67){
                    this.floor = (this.floor == Blocks.sand ? this.floor : Blocks.sand);
                    this.ore = Blocks.air;
                };
            };
        });

        var difficulty = this.sector.threat;
        const ints = this.ints;
        
        ints.clear();
        ints.ensureCapacity(this.width * this.height / 4);

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        enemies.each(espawn => this.tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));

        var state = Vars.state;

        if(this.sector.hasEnemyBase()){
            this.basegen.generate(tiles, enemies.map(r => this.tiles.getn(r.x, r.y)), this.tiles.get(spawn.x, spawn.y), state.rules.waveTeam, this.sector, difficulty);

            state.rules.attackMode = this.sector.info.attack = true;
        }else{
            state.rules.winWave = this.sector.info.winWave = 10 + 5 * Math.max(difficulty * 10, 1);
        };

        var waveTimeDec = 0.4;

        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60 * 60 * 1, Math.floor(Math.max(difficulty - waveTimeDec, 0) / 0.8));
        state.rules.waves = this.sector.info.waves = true;
        state.rules.enemyCoreBuildRadius = 480;

        state.rules.spawns = Waves.generate(difficulty, new Rand(), state.rules.attackMode);
        
        //this.generate(tiles);
    },

    postGenerate(tiles){
        if(this.sector.hasEnemyBase()){
            this.basegen.postGenerate();
        };
    } 
});
marsgen.arr = [   
    [Blocks.deepwater, Blocks.sandWater, Blocks.sandWater, Blocks.grass, Blocks.craters, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.deepwater, Blocks.sandWater, Blocks.sandWater, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.metalFloorDamaged, Blocks.sand, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.deepwater, Blocks.sandWater, Blocks.grass, Blocks.grass, Blocks.metalFloorDamaged, Blocks.metalFloorDamaged, Blocks.dirt, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.water, Blocks.sandWater, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.metalFloorDamaged, Blocks.stone, Blocks.hotrock, Blocks.stone, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.sandWater, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.metalFloorDamaged, Blocks.mud, Blocks.dirt, Blocks.stone, Blocks.stone, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    
    [Blocks.sandWater, Blocks.craters, Blocks.grass, Blocks.mud, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.water, Blocks.sandWater, Blocks.grass, Blocks.mud, Blocks.mud, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.sandWater, Blocks.sandWater, Blocks.grass, Blocks.mud, Blocks.metalFloorDamaged, Blocks.mud, Blocks.sand, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.sandWater, Blocks.grass, Blocks.dirt, Blocks.dirt, Blocks.grass, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt, Blocks.dirt],
    [Blocks.sandWater, Blocks.sandWater, Blocks.grass, Blocks.grass, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sandWater, Blocks.stone, Blocks.stone],
    [Blocks.sandWater, Blocks.sandWater, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sandWater, Blocks.stone, Blocks.stone, Blocks.stone],
    [Blocks.sandWater, Blocks.sandWater, Blocks.sand, Blocks.sand, Blocks.craters, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sandWater, Blocks.stone, Blocks.stone, Blocks.stone],
    [Blocks.sandWater, Blocks.sandWater, Blocks.sand, Blocks.craters, Blocks.craters, Blocks.craters, Blocks.sand, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.dirt, Blocks.grass, Blocks.dirt]
];
marsgen.rid = new Packages.arc.util.noise.RidgedPerlin(1, 2);
marsgen.basegen = new BaseGenerator();
marsgen.scl = 5;
marsgen.waterOffset = 0.07;
marsgen.water = 2 / marsgen.arr[0].length;
const mars = new JavaAdapter(Planet, {}, "mars", Planets.sun, 3, 1);
mars.meshLoader = () => new HexMesh(mars, 6);
mars.generator = marsgen;
mars.smudtSector = 27;
mars.hasAtmosphere = true;
mars.atmosphereColor = Color.valueOf("d24c11");

const polaris = new JavaAdapter(Planet, {}, "polaris", Planets.sun, 4, 0.6);
polaris.meshLoader = () => new SunMesh(polaris, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("7b68ee"), Color.valueOf("6a5acd"), Color.valueOf("4169e1"));
polaris.accessible = false;
polaris.hasAtmosphere = false;


const saylot = new JavaAdapter(Planet, {}, "saylot", polaris, 4, 0.7);
saylot.meshLoader = () => new SunMesh(saylot, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("000000"), Color.valueOf("ff9900"), Color.valueOf("323332"));
saylot.accessible = false;
saylot.hasAtmosphere = false;

const ko = new JavaAdapter(Planet, {}, "ko", polaris, 4, 0.7);
ko.meshLoader = () => new SunMesh(ko, 4, 5, 0.3, 1.7, 1.2, 1, 1.5, Color.valueOf("000000"), Color.valueOf("ff9900"), Color.valueOf("323332"));
ko.accessible = false;
ko.hasAtmosphere = false;

//moon's generator
const mgen = extend(PlanetGenerator, {
    rawHeight(position){
        position = Tmp.v33.set(position).scl(this.scl);
        return (Mathf.pow(this.noise.octaveNoise3D(7, 0.5, 1 / 3, position.x, position.y, position.z), 2.3) + this.waterOffset) / (1 + this.waterOffset);
    },

    getHeight(position){
        var height = this.rawHeight(position);
        return Math.max(height, this.water);
    },

    getColor(position){
        var block = this.getBlock(position);
        if(block == null) return Blocks.sand.mapColor;
        Tmp.c1.set(block.mapColor).a = 1 - block.albedo;
        
        return Tmp.c1;
    },

    genTile(position, tile){
        tile.floor = this.getBlock(position);
        tile.block = tile.floor.asFloor().wall;

        if(this.rid.getValue(position.x, position.y, position.z, 22) > 0.32){
            tile.block = Blocks.air;
        };
    },

    getBlock(position){
        var arr = this.arr;
        var scl = this.scl;
        
        var height = this.rawHeight(position);
        Tmp.v31.set(position);
        
        position = Tmp.v33.set(position).scl(scl);
        var rad = this.scl;
        var temp = Mathf.clamp(Math.abs(position.y * 2) / rad);
        var tnoise = this.noise.octaveNoise3D(7, 0.56, 1 / 3, position.x, position.y + 999, position.z);
        
        temp = Mathf.lerp(temp, tnoise, 0.5);
        height *= 1.2;
        height = Mathf.clamp(height);

        var res = arr[Mathf.clamp(Math.floor(temp * arr.length), 0, arr[0].length - 1)][Mathf.clamp(Math.floor(height * arr[0].length), 0, arr[0].length - 1)];
        return res;
    },
    
    noiseOct(x, y, octaves, falloff, scl){
        var v = this.sector.rect.project(x, y).scl(5);
        return this.noise.octaveNoise3D(octaves, falloff, 1 / scl, v.x, v.y, v.z);
    },

    generate(tiles, sec){
        this.tiles = tiles;
        this.sector = sec;
        
        const rand = this.rand;
        rand.setSeed(sec.id);
        
        //tile, sector
        var gen = new TileGen();
        this.tiles.each((x, y) => {
            gen.reset();
            var position = this.sector.rect.project(x / tiles.width, y / tiles.height);

            this.genTile(position, gen);
            tiles.set(x, y, new Tile(x, y, gen.floor, gen.overlay, gen.block));
        });
        
        const Room = {
            x: 0, y: 0, radius: 0,
            connected: new ObjectSet(),

            connect(to){
                if(this.connected.contains(to)) return;

                this.connected.add(to);
                
                var nscl = rand.random(20, 60);
                var stroke = rand.random(4, 12);
                
                mgen.brush(mgen.pathfind(this.x, this.y, to.x, to.y, tile => (tile.solid() ? 5 : 0) + mgen.noiseOct(tile.x, tile.y, 1, 1, 1 / nscl) * 60, Asmud.manhattan), stroke);
            }
        };
        
        const setRoom = (x, y, radius) => {
            var room = Object.create(Room);
            
            room.x = x;
            room.y = y;
            room.radius = radius;
            
            return room;
        };

        this.cells(4);
        this.distort(10, 12);

        this.width = this.tiles.width;
        this.height = this.tiles.height;

        var constraint = 1.3;
        var radius = this.width / 2 / Mathf.sqrt3;
        var rooms = rand.random(2, 5);
        var roomseq = new Seq();

        for(var i = 0; i < rooms; i++){
            Tmp.v1.trns(rand.random(360), rand.random(radius / constraint));
            var rx = Math.floor(this.width / 2 + Tmp.v1.x);
            var ry = Math.floor(this.height / 2 + Tmp.v1.y);
            var maxrad = radius - Tmp.v1.len();
            var rrad = Math.floor(Math.min(rand.random(9, maxrad / 2), 30));
            
            roomseq.add(setRoom(rx, ry, rrad));
        };

        var spawn = null;
        var enemies = new Seq();
        var enemySpawns = rand.random(1, Math.max(Mathf.floor(this.sector.threat * 4), 1));
        
        var offset = rand.nextInt(360);
        var length = this.width / 2.55 - rand.random(13, 23);
        var angleStep = 5;
        var waterCheckRad = 5;
        
        for(var i = 0; i < 360; i += angleStep){
            var angle = offset + i;
            var cx = Math.floor(this.width / 2 + Angles.trnsx(angle, length));
            var cy = Math.floor(this.height / 2 + Angles.trnsy(angle, length));

            var waterTiles = 0;

            for(var rx = -waterCheckRad; rx <= waterCheckRad; rx++){
                for(var ry = -waterCheckRad; ry <= waterCheckRad; ry++){
                    var tile = this.tiles.get(cx + rx, cy + ry);
                    
                    if(tile == null || tile.floor().liquidDrop != null){
                        waterTiles++;
                    };
                };
            };

            if(waterTiles <= 4 || (i + angleStep >= 360)){
                spawn = setRoom(cx, cy, rand.random(10, 18));
                roomseq.add(spawn);

                for(var j = 0; j < enemySpawns; j++){
                    var enemyOffset = rand.range(60);
                    
                    Tmp.v1.set(cx - this.width / 2, cy - this.height / 2).rotate(180 + enemyOffset).add(this.width / 2, this.height / 2);
                    var espawn = setRoom(Math.floor(Tmp.v1.x), Math.floor(Tmp.v1.y), rand.random(10, 16));
                    roomseq.add(espawn);
                    enemies.add(espawn);
                };

                break;
            };
        };

        roomseq.each(room => this.erase(room.x, room.y, room.radius));

        var connections = rand.random(Math.max(rooms - 1, 1), rooms + 3);
        for(var i = 0; i < connections; i++){
            roomseq.random(rand).connect(roomseq.random(rand));
        };

        roomseq.each(room => spawn.connect(room));

        this.cells(1);
        this.distort(10, 6);

        this.inverseFloodFill(this.tiles.getn(spawn.x, spawn.y));

        var ores = Seq.with(Blocks.oreCopper, Blocks.oreLead);
        var poles = Math.abs(this.sector.tile.v.y);
        var nmag = 0.5;
        var scl = 1;
        var addscl = 1.3;

        if(rand.chance(0.25)){
            ores.add(Blocks.oreScrap);
        };

        var frequencies = new FloatSeq();
        for(var i = 0; i < ores.size; i++){
            frequencies.add(rand.random(-0.1, 0.01) - i * 0.01 + poles * 0.04);
        };

        this.pass((x, y) => {
            if(!this.floor.asFloor().hasSurface()) return;

            var offsetX = x - 4, offsetY = y + 23;
            for(var i = ores.size - 1; i >= 0; i--){
                var entry = ores.get(i);
                var freq = frequencies.get(i);
                
                if(Math.abs(0.5 - this.noiseOct(offsetX, offsetY + i * 999, 2, 0.7, (40 + i * 2))) > 0.22 + i * 0.01 &&
                    Math.abs(0.5 - this.noiseOct(offsetX, offsetY - i * 999, 1, 1, (30 + i * 4))) > 0.37 + freq){
                    this.ore = entry;
                    break;
                };    
            };

            if(this.ore == Blocks.oreScrap && rand.chance(0.33)){
                this.floor = Blocks.metalFloorDamaged;
            };
        });

        this.trimDark();
        this.median(2);
        this.tech();
        this.pass((x, y) => {});

        var difficulty = this.sector.threat;
        const ints = this.ints;
        
        ints.clear();
        ints.ensureCapacity(this.width * this.height / 4);

        Schematics.placeLaunchLoadout(spawn.x, spawn.y);

        enemies.each(espawn => this.tiles.getn(espawn.x, espawn.y).setOverlay(Blocks.spawn));

        var state = Vars.state;

        if(this.sector.hasEnemyBase()){
            this.basegen.generate(tiles, enemies.map(r => this.tiles.getn(r.x, r.y)), this.tiles.get(spawn.x, spawn.y), state.rules.waveTeam, this.sector, difficulty);

            state.rules.attackMode = this.sector.info.attack = true;
        }else{
            state.rules.winWave = this.sector.info.winWave = 10 + 5 * Math.max(difficulty * 10, 1);
        };

        var waveTimeDec = 0.4;

        state.rules.waveSpacing = Mathf.lerp(60 * 65 * 2, 60 * 60 * 1, Math.floor(Math.max(difficulty - waveTimeDec, 0) / 0.8));
        state.rules.waves = this.sector.info.waves = true;
        state.rules.enemyCoreBuildRadius = 480;

        state.rules.spawns = Waves.generate(difficulty, new Rand(), state.rules.attackMode);
        
        //this.generate(tiles);
    },

    postGenerate(tiles){
        if(this.sector.hasEnemyBase()){
            this.basegen.postGenerate();
        };
    } 
});
mgen.arr = [   
    [Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],
    [Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],
    [Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass],
    [Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],
    [Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],
    
    [Blocks.stone, Blocks.grass, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],
    [Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],
    [Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],
    [Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass],

    [Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.stone],
    [Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone],
    [Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.stone],
    [Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.grass, Blocks.stone, Blocks.stone, Blocks.stone, Blocks.grass, Blocks.grass, Blocks.grass]
];
mgen.rid = new Packages.arc.util.noise.RidgedPerlin(1, 2);
mgen.basegen = new BaseGenerator();
mgen.scl = 5;
mgen.waterOffset = 0.07;
mgen.water = 2 / mgen.arr[0].length;

const moon = new JavaAdapter(Planet, {}, "moon", Planets.serpulo, 3, 0.6);
moon.generator = mgen;
moon.hasAtmosphere = false;
moon.meshLoader = () => new HexMesh(moon, 6);
moon.smudtSector = 40;

module.exports = {
  mercury: mercury,
  venus: venus,
  moon: moon,
  mars: mars,
  polaris: polaris,
  saylot: saylot,
  ko: ko
}