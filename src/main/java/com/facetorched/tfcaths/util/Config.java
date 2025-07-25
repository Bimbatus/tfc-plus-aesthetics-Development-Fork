package com.facetorched.tfcaths.util;

import java.io.File;

import com.dunk.tfc.Reference;
import com.dunk.tfc.api.TFCBlocks;
import com.dunk.tfc.api.Enums.EnumTree;
import com.facetorched.tfcaths.AthsBlockSetup;
import com.facetorched.tfcaths.AthsGlobal;
import com.facetorched.tfcaths.AthsMod;
import com.facetorched.tfcaths.WorldGen.Generators.AthsWorldGenCrystals;
import com.facetorched.tfcaths.WorldGen.Generators.AthsWorldGenPlants;
import com.facetorched.tfcaths.WorldGen.Generators.CrystalSpawnData;
import com.facetorched.tfcaths.WorldGen.Generators.PlantSpawnData;
import com.facetorched.tfcaths.blocks.BlockCrystal;
import com.facetorched.tfcaths.blocks.BlockPlantCactus;
import com.facetorched.tfcaths.blocks.BlockPlantEpiphyte3d;
import com.facetorched.tfcaths.interfaces.ILilyPad;
import com.facetorched.tfcaths.interfaces.ITree;

import cpw.mods.fml.common.Loader;
import net.minecraft.init.Items;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;

public class Config {
	//configuration object
	public static Configuration config;
	
	//define configuration fields here
	public static int numCustomGenerators;
	//public static double[] cullSurfaceWeights;
	//public static Block [] cullSurfaceBlocks;
	public static float cullShrubs;
	
	public static boolean mushroomRecipes;
	public static boolean propagationRecipes;
	public static boolean miscRecipes;
	
	public static boolean plantsPopOff;
	public static boolean requireShovel;
	
	public static float rarityTree;
	public static float rarityLilyPad;
	public static float rarityEpiphyte;
	public static float rarityCactus;
	public static float rarityOther;
	
	public static boolean rockCrystalNetherQuartz;
	
	public static void preInit(File configDir){
		if (config != null) throw new IllegalStateException("Preinit can't be called twice.");
		config = new Configuration(new File(configDir,"TFCPlusAesthetics.cfg"));
		String version = null;
		if (config.hasKey("_general", "_version")){
			version = config.get("_general", "_version", AthsMod.VERSION).getString();
		}
		if (!AthsMod.VERSION.equals(version)){
			File configFile = new File(config.getConfigFile().getAbsolutePath());
			File renameFile = new File(config.getConfigFile().getAbsolutePath() + (version == null ? "" : version) + ".backup");
		    boolean rename = configFile.renameTo(renameFile); // Backup old config
		    if (!rename) {
		    	configFile.delete();
		    }
		    config.load();
		    config.get("_general", "_version", AthsMod.VERSION);
		    config.save();
		}
	}
	
	public static void reload(){
		if (config == null) throw new IllegalStateException("Config reload attempt before preinit.");
		AthsLogger.info("Loading TFC+ Aesthetics Config");
		config.load();
		// set configs here
		String[] soilBlocks = AthsParser.prefix(new String[] {"Dirt", "Dirt2", "Grass", "Grass2", "DryGrass", "DryGrass2", "Clay", "Clay2", "ClayGrass", "ClayGrass2", "tilledSoil", "tilledSoil2"}, Reference.MOD_ID + ":");
		soilBlocks = config.get("_soil_ore_dict", "blockSoil", soilBlocks, "blocks to add to the ore dictionary 'blockSoil'. Leave empty to disable").getStringList();
		
		for(String soil : soilBlocks) {
			OreDictionary.registerOre("blockSoil", AthsParser.getBlockFromName(soil));
		}
		
		numCustomGenerators = config.getInt("numCustomGenerators", "_general", 1, 0, Integer.MAX_VALUE, "The number of custom plant generators to read from. The names of these generators are enumerated as \"_z[n]\"");
		
		cullShrubs = config.getFloat("cullShrubs", "_general", 0.0f, 0.0f, 1.0f, "The degree to which TFC+ shrubs should be culled from the world. Set to 0 to disable the culling.");
		/*
		String[] cullSurfaceBlockNames = config.getStringList("_cull_surface_blocks", "cullSurfaceBlocks", new String[] {"terrafirmacraftplus:shrub"}, "blocks to be culled from the surface during world generation");
		cullSurfaceBlocks = AthsParser.getBlockFromName(cullSurfaceBlockNames);
		cullSurfaceWeights = config.get("cullSurfaceWeights", "_cull_surface_weights", new double[] {0.2}, "The weights associated with each cullSurfaceBlock. The higher the weight the more the block will be culled", 0.0, 1.0).getDoubleList();
		
		if(cullSurfaceBlocks.length != cullSurfaceWeights.length) {
			throw new java.lang.IllegalArgumentException("cullSurfaceBlocks must have the same length as cullSurfaceWeights!");
		}
		*/
		
		mushroomRecipes = config.getBoolean("mushroomRecipes", "_general", true, "Set to false to prevent fungi from being craftable into mushroom food items");
		propagationRecipes = config.getBoolean("propagationRecipes", "_general", true, "Set to false to prevent plants from being growable in a barrel");
		miscRecipes = config.getBoolean("miscRecipes", "_general", true, "Set to false to prevent addition of various TFC+ styled plant-based recipes");
		
		plantsPopOff = config.getBoolean("plantsPopOff", "_general", false, "Set to true to allow plants to drop an item when the block they are on is destroyed.");
		requireShovel = config.getBoolean("requireShovel", "_general", false, "Set to true to only drop plants if the player breaks them with a shovel.");
		
		rarityTree = config.getFloat("rarityTree", "_general", 1f, 0f, 10000f, "The multiplier applied to tree-like plant rarity. Set to 0 to disable these from spawning entirely");
		rarityLilyPad = config.getFloat("rarityLilyPad", "_general", 1f, 0f, 10000f, "The multiplier applied to lilypad-like plant rarity (including algae). Set to 0 to disable these from spawning entirely");
		rarityEpiphyte = config.getFloat("rarityEpiphyte", "_general", 1f, 0f, 10000f, "The multiplier applied to epiphyte plant rarity. Set to 0 to disable these from spawning entirely");
		rarityCactus = config.getFloat("rarityCactus", "_general", 1f, 0f, 10000f, "The multiplier applied to cactus rarity. Set to 0 to disable these from spawning entirely");
		rarityOther = config.getFloat("rarityOther", "_general", 1f, 0f, 10000f, "The multiplier applied to non-categorized plants. Set to 0 to disable these from spawning entirely");
		
		rockCrystalNetherQuartz = config.getBoolean("rockCrystalNetherQuartz", "_general", true, "Set to false to prevent rock crystal from dropping nether quartz. Will drop quartzite rocks only.");
		if (rockCrystalNetherQuartz)
			((BlockCrystal)(AthsBlockSetup.rockCrystalCluster)).setItem(Items.quartz, 0);
		
		if (config.hasChanged()) config.save();
	}
	
	public static void reloadCrystals() {
		athsCrystalHelper(AthsGlobal.AGATE, new String[] {"Sed","Diorite","Granite","Rhyolite","Andesite"}, /*size*/1, /*dispersion*/1, /*rarity*/70);
        athsCrystalClusterHelper(AthsGlobal.AMETHYST, new String[] {"IgEx","Limestone","Granite"}, /*size*/20, /*dispersion*/1, /*rarity*/120);
        athsCrystalClusterHelper(AthsGlobal.BERYL, new String[] {"Granite","Rhyolite","Gneiss"}, /*size*/18, /*dispersion*/3, /*rarity*/300);
        athsCrystalClusterHelper(AthsGlobal.DIAMOND, new String[] {"Gabbro"}, /*size*/35, /*dispersion*/2, /*rarity*/280);
        athsCrystalClusterHelper(AthsGlobal.EMERALD, new String[] {"Granite","Rhyolite","Gneiss"}, /*size*/18, /*dispersion*/3, /*rarity*/350);
        athsCrystalClusterHelper(AthsGlobal.GARNET, new String[] {"Granite","MM","Sandstone","Shale","Claystone"}, /*size*/15, /*dispersion*/5, /*rarity*/300);
        athsCrystalHelper(AthsGlobal.JADE, new String[] {"MM"}, /*size*/1, /*dispersion*/1, /*rarity*/280);
        athsCrystalHelper(AthsGlobal.JASPER, new String[] {"Chert","Limestone","Dolomite","Schist","Gneiss","Phyllite","Slate"}, /*size*/1, /*dispersion*/1, /*rarity*/70);
        athsCrystalHelper(AthsGlobal.OPAL, new String[] {"All"}, /*size*/1, /*dispersion*/1, /*rarity*/480);
        athsCrystalClusterHelper(AthsGlobal.RUBY, new String[] {"IgIn","MM"}, /*size*/20, /*dispersion*/6, /*rarity*/350);
        athsCrystalClusterHelper(AthsGlobal.SAPPHIRE, new String[] {"IgIn","MM"}, /*size*/20, /*dispersion*/6, /*rarity*/350);
        athsCrystalClusterHelper(AthsGlobal.TOPAZ, new String[] {"Granite","Rhyolite"}, /*size*/18, /*dispersion*/3, /*rarity*/280);
        athsCrystalClusterHelper(AthsGlobal.TOURMALINE, new String[] {"Granite","Diorite","Gneiss","Phyllite","Quartzite"}, /*size*/18, /*dispersion*/3, /*rarity*/280);
        
        if (Loader.isModLoaded("teloaddon") && com.facetorched.teloaddon.util.Config.addFluorite) {
        	athsCrystalClusterHelper(AthsGlobal.FLUORITE, new String[] {"Limestone","Dolomite","Granite","Rhyolite","Sandstone"}, /*size*/20, /*dispersion*/1, /*rarity*/70);
        }
        
        athsCrystalClusterHelper(AthsGlobal.ROCK_CRYSTAL, new String[] {"MM"}, /*size*/20, /*dispersion*/1, /*rarity*/80);
        athsCrystalClusterHelper(AthsGlobal.GYPSUM, new String[] {"MM"}, /*size*/50, /*dispersion*/1, /*rarity*/100);
		
		if (config.hasChanged()) 
			config.save();
	}
	
	//this must be run in the init phase (after blocks setup but before world gen)
	public static void reloadPlants() {
		//System.out.println(Arrays.toString(AthsParser.getBiomeStringList()));
		
		//int[] p = new int[AthsParser.getBiomeStringList().length];
		//for(int i = 0; i < AthsParser.getBiomeStringList().length; i++)
		//	p[i] = TFCBiome.getBiomeByName(AthsParser.getBiomeStringList()[i]).biomeID;
		//System.out.println(Arrays.toString(p));
		
		athsPlantHelper(AthsGlobal.ADDERS_TONGUE_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/5, /*dispersion*/1, /*rarity*/7568, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/700f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-0.4f);
		athsPlantHelper(AthsGlobal.AFRICAN_MILK_BARREL, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/12, /*dispersion*/6, /*rarity*/2984, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/16f, /*maxTemp*/24f, /*minRain*/60f, /*maxRain*/160f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.AFRICAN_MILK_TREE, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/12, /*dispersion*/5, /*rarity*/3984, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/19f, /*maxTemp*/40f, /*minRain*/120f, /*maxRain*/660f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.ALBANIAN_SPURGE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/8, /*dispersion*/4, /*rarity*/6984, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/15f, /*maxTemp*/20f, /*minRain*/150f, /*maxRain*/560f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.ALGAE_MAT_CYANOBACTERIA, new int[] {0,1,2}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:SaltWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/64, /*dispersion*/1, /*rarity*/2968, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/80f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.ALGAE_MAT_GREEN, new int[] {0,1,2}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/64, /*dispersion*/1, /*rarity*/968, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/80f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.ALGAE_MAT_RED, new int[] {0,1,2}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:SaltWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/64, /*dispersion*/1, /*rarity*/9068, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/40f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.ALOE_VERA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills"}, new String[]{"Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/834, /*minAltitude*/156, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/45f, /*maxRain*/130f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.ALPINE_JUNIPER + "_Mountain", AthsGlobal.ALPINE_JUNIPER, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel","ore:stone"},  new String[] {"Mountains","Mountain Range","Mountains Edge","Mountain Range Edge", "Foothills"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/15, /*dispersion*/15, /*rarity*/2900, /*minAltitude*/170, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/14f, /*minRain*/150f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.6f);
		athsPlantHelper(AthsGlobal.ALPINE_JUNIPER, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/15, /*dispersion*/15, /*rarity*/2900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/7f, /*minRain*/150f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.6f);
		athsPlantHelper(AthsGlobal.AMARANTH, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/10, /*dispersion*/2, /*rarity*/6700, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/40f, /*minRain*/330, /*maxRain*/810f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.ANEMONE + "_Broadleaf", AthsGlobal.ANEMONE, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/2, /*rarity*/5884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/19f, /*minRain*/380f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.ANEMONE + "_Poppy", AthsGlobal.ANEMONE, new int[] {2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/2, /*rarity*/4884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/21f, /*minRain*/380f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.ANEMONE + "_Thimbleweed", AthsGlobal.ANEMONE, new int[] {3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/5, /*dispersion*/2, /*rarity*/5884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/17f, /*minRain*/680f, /*maxRain*/1600f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.ANEMONE /*Balkan*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/5, /*dispersion*/2, /*rarity*/5884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/18f, /*minRain*/380f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.ANGELS_TRUMPET + "_Pink", AthsGlobal.ANGELS_TRUMPET, new int[] {1}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/15, /*rarity*/1084, /*minAltitude*/170, /*maxAltitude*/220, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/900f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.ANGELS_TRUMPET + "_White", AthsGlobal.ANGELS_TRUMPET, new int[] {2}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/15, /*rarity*/1084, /*minAltitude*/150, /*maxAltitude*/220, /*minTemp*/20f, /*maxTemp*/35f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.ANGELS_TRUMPET /*Orange*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/15, /*rarity*/1084, /*minAltitude*/150, /*maxAltitude*/220, /*minTemp*/25f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.ANGEL_WING_CACTUS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Americas"},
				/*size*/22, /*dispersion*/15, /*rarity*/980, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/19f, /*maxTemp*/35f, /*minRain*/75f, /*maxRain*/210f, /*minEVT*/1f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.ARPOPHYLLUM_GIGANTEUM, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
		        /*size*/1, /*dispersion*/1, /*rarity*/2300, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/980f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ARROWROOT, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/6, /*dispersion*/8, /*rarity*/4800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/1500f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ARTISTS_CONK, new int[] {0}, new String[] {"oak","maple","ash","aspen","sycamore","whiteelm","willow","spruce","douglasfir"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/2000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/18f, /*minRain*/500f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ASTER + "_Alpine", AthsGlobal.ASTER, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Mountains","Mountains Edge","Mountain Range","Mountain Range Edge","Foothills","High Hills"}, new String[]{"Europe","Americas"},
				/*size*/5, /*dispersion*/2, /*rarity*/7184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/10f, /*minRain*/500f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.ASTER, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/5, /*dispersion*/2, /*rarity*/3884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/18f, /*minRain*/500f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.AUTUMN_SKULLCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/21f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.AZALEA_DECIDUOUS + "_Swamp", AthsGlobal.AZALEA_DECIDUOUS, new int[] {1}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Asia","Europe"},
				/*size*/6, /*dispersion*/6, /*rarity*/3980, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/5f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/5f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.AZALEA_DECIDUOUS /*Honeysuckle*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Riverbank","Swamp","Peat Bog"}, new String[]{"Asia","Europe"},
				/*size*/6, /*dispersion*/6, /*rarity*/3980, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/10f, /*maxTemp*/18f, /*minRain*/650f, /*maxRain*/1400f, /*minEVT*/1f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.AZALEA_EVERGREEN + "_Lapland", AthsGlobal.AZALEA_EVERGREEN, new int[] {1}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Mountains","Rolling Hills","Lakeshore","Riverbank","Swamp","Peat Bog","Mountain Range","Mountain Range Edge","High Plains","Lake","Foothills"}, new String[]{"Americas"},
				/*size*/12, /*dispersion*/9, /*rarity*/4980, /*minAltitude*/150, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/5f, /*minRain*/250f, /*maxRain*/850f, /*minEVT*/1f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.AZALEA_EVERGREEN + "_Satsuki", AthsGlobal.AZALEA_EVERGREEN, new int[] {2}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Asia"},
				/*size*/6, /*dispersion*/6, /*rarity*/3980, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/11f, /*maxTemp*/19f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/5f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.AZALEA_EVERGREEN /*Flame*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountains","Mountain Range","Mountain Range Edge","High Plains","Lake","Foothills"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/6, /*rarity*/3980, /*minAltitude*/150, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/5f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.AZOLLA_FERN, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/64, /*dispersion*/1, /*rarity*/1268, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/3f, /*maxTemp*/40f, /*minRain*/700f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.BACON_MARASMIUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/6096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BASEBALL_PLANT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/10, /*dispersion*/7, /*rarity*/1780, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/9f, /*maxTemp*/23f, /*minRain*/75f, /*maxRain*/310f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.BASKET_STINKHORN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Asia","Europe"},
				/*size*/6, /*dispersion*/6, /*rarity*/3980, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/0f, /*maxTemp*/16f, /*minRain*/550f, /*maxRain*/1600f, /*minEVT*/1.5f, /*maxEVT*/5f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.BEAR_CORN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/8980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/17f, /*minRain*/850f, /*maxRain*/2100f, /*minEVT*/1.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEECH_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/8980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/17f, /*minRain*/850f, /*maxRain*/3500f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEEFSTEAK_FUNGUS, new int[] {0}, new String[] {"oak","chestnut"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/2400, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/35f, /*minRain*/700f, /*maxRain*/4500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA + "_Brevirimosa", AthsGlobal.BEGONIA, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/14, /*dispersion*/5, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/26f, /*maxTemp*/40f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA + "_Egregia", AthsGlobal.BEGONIA, new int[] {2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/14, /*dispersion*/5, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA + "_Escargot", AthsGlobal.BEGONIA, new int[] {3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/14, /*dispersion*/5, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/32f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA + "_Fireworks", AthsGlobal.BEGONIA, new int[] {4}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/14, /*dispersion*/5, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/32f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA + "_Maurice_Amey", AthsGlobal.BEGONIA, new int[] {5}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/14, /*dispersion*/5, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA + "_Peacock", AthsGlobal.BEGONIA, new int[] {6}, new String[] {"ore:blockSoil"}, new String[] {"Mountains","Mountains Edge","Mountain Range","Mountain Range Edge","Foothills","High Hills"}, new String[]{"Asia"},
				/*size*/14, /*dispersion*/5, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA + "_Polka_Dot", AthsGlobal.BEGONIA, new int[] {7}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/14, /*dispersion*/5, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BEGONIA /*Amphioxus*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/9, /*dispersion*/3, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/36f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BIRCH_BOLETE + "_Asia", AthsGlobal.BIRCH_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  new String[] {"Mountains","Mountain Range","Mountains Edge","Mountain Range Edge", "Foothills"}, new String[]{"Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/17f, /*minRain*/350f, /*maxRain*/5500f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BIRCH_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/17f, /*minRain*/350f, /*maxRain*/5500f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BIRCH_POLYPORE, new int[] {0}, new String[] {"birch"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/4500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/13f, /*minRain*/300f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BIRDS_NEST_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia"},
				/*size*/5, /*dispersion*/3, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/1500f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BIRD_OF_PARADISE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/3, /*dispersion*/3, /*rarity*/7584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/16f, /*minRain*/600f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BISHOPS_WEED, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/65, /*dispersion*/1, /*rarity*/5100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/18f, /*minRain*/670f, /*maxRain*/2000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BITTER_OYSTER, new int[] {0}, new String[] {"oak","birch","maple","hickory","ash","chestnut","pine"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/800f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLACK_BAT_FLOWER, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/3, /*dispersion*/3, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/28f, /*minRain*/1100f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLACK_EYED_SUSAN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/3, /*rarity*/4884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/400f, /*maxRain*/950f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.BLACK_EYED_SUSAN_VINE, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/15, /*dispersion*/1, /*rarity*/2300, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLACK_SPLEENWORT, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Americas","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/1000, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLAZING_STAR, new int[] {0,1}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/4, /*dispersion*/2, /*rarity*/4984, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-3f, /*maxTemp*/24f, /*minRain*/630f, /*maxRain*/790f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.BLEEDING_HEARTS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel"}, new String[]{"Mountains","Mountains Edge","Mountain Range","Mountain Range Edge","Foothills","High Hills"}, new String[]{"Asia"},
				/*size*/5, /*dispersion*/2, /*rarity*/5184, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/-6f, /*maxTemp*/10f, /*minRain*/900f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLOODROOT, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/12, /*dispersion*/9, /*rarity*/3790, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/17f, /*minRain*/680f, /*maxRain*/1208f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLOOD_LILY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/8, /*dispersion*/3, /*rarity*/4584, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/10f, /*maxTemp*/18f, /*minRain*/250f, /*maxRain*/760f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BLUEBELL, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","High Plains","High Hills","High Hills Edge","Lakeshore","Riverbank","Swamp"}, new String[]{"Europe"},
				/*size*/200, /*dispersion*/1, /*rarity*/12884, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/-5f, /*maxTemp*/17f, /*minRain*/850f, /*maxRain*/3060f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.BLUE_CEREUS_CACTUS, new int[] {0,1}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Americas"},
				/*size*/5, /*dispersion*/8, /*rarity*/1200, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/17f, /*maxTemp*/34f, /*minRain*/55f, /*maxRain*/190f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BLUE_OIL_FERN, new int[] {0}, new String[] {"alltrees","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/9, /*dispersion*/1, /*rarity*/2184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/29f, /*minRain*/1400f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BOLETUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas", "Asia", "Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/7884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/15f, /*minRain*/700f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BONATEA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/7, /*dispersion*/3, /*rarity*/4584, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/7f, /*maxTemp*/17f, /*minRain*/500f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BOSTON_FERN, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/1000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BOXWOOD, new int[] {0}, new String[]{"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
        		/*size*/5, /*dispersion*/4, /*rarity*/5900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/32f, /*minRain*/530f, /*maxRain*/1170f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BRACKEN_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/22, /*dispersion*/8, /*rarity*/2568, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-10f, /*maxTemp*/30f, /*minRain*/500f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-0.4f);
		athsPlantHelper(AthsGlobal.BRIDAL_CREEPER, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/19f, /*minRain*/450f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BRIDAL_VEIL_STINKHORN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Africa"},
				/*size*/2, /*dispersion*/1, /*rarity*/10096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/32f, /*minRain*/1000f, /*maxRain*/8000f, /*minEVT*/1f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.BRISTLE_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog","Mountain Range","Mountains", "Mountain Range Edge","Mountains Edge"}, new String[]{"Asia","Americas"},
				/*size*/8, /*dispersion*/4, /*rarity*/6969, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/30f, /*minRain*/2000f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/8f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.BUCEPHALANDRA, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Asia"},
				/*size*/64, /*dispersion*/1, /*rarity*/1068, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/24f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f, /*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BUCKTHORN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/22, /*dispersion*/3, /*rarity*/3489, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/2000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BULBLET_FERN, new int[] {0}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/5, /*dispersion*/2, /*rarity*/650, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BURDOCK, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/8, /*dispersion*/9, /*rarity*/968, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/18f, /*minRain*/450f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BURMA_CREEPER, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BURNING_BUSH, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Mountains","Rolling Hills","Lakeshore","Riverbank","Swamp","Mountain Range","Mountain Range Edge","High Plains","Lake","Foothills"}, new String[]{"Asia"},
				/*size*/12, /*dispersion*/10, /*rarity*/6980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-11f, /*maxTemp*/13f, /*minRain*/450f, /*maxRain*/1050f, /*minEVT*/1f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.BUTTERCUP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/7, /*dispersion*/2, /*rarity*/3584, /*minAltitude*/0, /*maxAltitude*/195, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/600f, /*maxRain*/2400f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.3f);
		athsPlantHelper(AthsGlobal.CAMAS_FLOWER, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/25, /*dispersion*/6, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-13f, /*maxTemp*/12f, /*minRain*/300f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.CANADA_WILD_GINGER, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"High Hills","High Hills Edge","Plains","Rolling Hills","Estuary","Riverbank","Swamp","Lakeshore"}, new String[]{"Americas"},
				/*size*/43, /*dispersion*/1, /*rarity*/6984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/14f, /*minRain*/850f, /*maxRain*/1200f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.CANARY_CREEPER, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/27f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CARALLUMA, new int[] {0}, new String[] {"ore:blockSoil", "ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa", "Asia"},
				/*size*/14, /*dispersion*/5, /*rarity*/3584, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/16f, /*maxTemp*/32f, /*minRain*/85f, /*maxRain*/300f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.CARNATIONS + "_Helena_Allwood", AthsGlobal.CARNATIONS, new int[] {2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/9984, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/420f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CARNATIONS + "_Liberty", AthsGlobal.CARNATIONS, new int[] {5}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/9284, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/420f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CARNATIONS + "_Monty's_Pink", AthsGlobal.CARNATIONS, new int[] {4}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/8484, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/420f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CARNATIONS + "_Nahema", AthsGlobal.CARNATIONS, new int[] {3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/9684, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/420f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CARNATIONS + "_Northland", AthsGlobal.CARNATIONS, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/9884, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/420f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CARNATIONS + "_Royal_Crimson", AthsGlobal.CARNATIONS, new int[] {6}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/10284, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/420f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CARNATIONS /*Wild*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/6884, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/420f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CEYLON_CREEPER + "_Variegated", AthsGlobal.CEYLON_CREEPER, new int[] {1}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/19f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CEYLON_CREEPER, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/19f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CHAGA, new int[] {0}, new String[] {"birch", "oak"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/13f, /*minRain*/450f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CHANTERELLE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Europe"},
				/*size*/14, /*dispersion*/5, /*rarity*/5684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.CHIVES, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Europe","Asia","Americas"},
				/*size*/8, /*dispersion*/4, /*rarity*/984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/16f, /*minRain*/600f, /*maxRain*/800f, /*minEVT*/1f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.CHI_NGULU_NGULU, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/4, /*dispersion*/6, /*rarity*/11584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.4f);
		athsPlantHelper(AthsGlobal.CHLOROPHOS_FOXFIRE, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/4412, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/26f, /*minRain*/850f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/7f);
		athsPlantHelper(AthsGlobal.CHRISTMAS_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"High Hills","High Hills Edge","Plains","Rolling Hills","Estuary","Riverbank","Swamp","Lakeshore"}, new String[]{"Americas"},
				/*size*/13, /*dispersion*/4, /*rarity*/6084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/18f, /*minRain*/900f, /*maxRain*/1400f, /*minEVT*/1f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.CHRYSANTHEMUM + "Harmony", AthsGlobal.CHRYSANTHEMUM, new int[] {2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/6, /*dispersion*/4, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/17f, /*minRain*/500f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.CHRYSANTHEMUM + "Regal_Mistd", AthsGlobal.CHRYSANTHEMUM, new int[] {3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/6, /*dispersion*/4, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/17f, /*minRain*/500f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.CHRYSANTHEMUM + "_French_Vanilla", AthsGlobal.CHRYSANTHEMUM, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/6, /*dispersion*/4, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/17f, /*minRain*/500f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.CHRYSANTHEMUM /*Clara_Curtis*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/6, /*dispersion*/4, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/17f, /*minRain*/500f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.CINNAMON_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/22, /*dispersion*/6, /*rarity*/4068, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-5f, /*maxTemp*/20f, /*minRain*/850f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.8f);
		athsPlantHelper(AthsGlobal.CLIFF_BRAKE, new int[] {0}, new String[] {"ore:stone"}, new String[] {AthsGlobal.LAND_BIOMES, "Beach","Gravel Beach"}, new String[]{"Americas","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/950, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/1190f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CLIMBING_ROSE, new int[] {0,1,2}, new String[] {"alltrees", "ore:stone"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp","Peat Bog","High Hills","High Hills Edge"}, new String[]{"Americas","Asia","Africa","Europe"},
				/*size*/20, /*dispersion*/1, /*rarity*/3600, /*minAltitude*/0, /*maxAltitude*/180, /*minTemp*/-5f, /*maxTemp*/22f, /*minRain*/600f, /*maxRain*/900f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.CLOVER, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Europe","Asia"},
				/*size*/20, /*dispersion*/4, /*rarity*/656, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/33f, /*minRain*/650f, /*maxRain*/950f, /*minEVT*/1f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.CLUBMOSS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Africa","Europe"},
				/*size*/29, /*dispersion*/8, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/810f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COBRA_LILY, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[] {"Swamp","Peat Bog","Estuary","Salt Swamp","Lakeshore","Riverbank","Plains"}, new String[]{"Americas"},
				/*size*/7, /*dispersion*/1, /*rarity*/7584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/21f, /*minRain*/1300f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS + "_Chocolate_Covered_Cherry", AthsGlobal.COLEUS, new int[] {1}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/8068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS + "_Chocolate_Mint", AthsGlobal.COLEUS, new int[] {2}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/7268, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS + "_Crimson_Gold", AthsGlobal.COLEUS, new int[] {3}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/7068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS + "_Jade", AthsGlobal.COLEUS, new int[] {4}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/6068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS + "_Red_Velvet", AthsGlobal.COLEUS, new int[] {5}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/7568, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS + "_Watermelon", AthsGlobal.COLEUS, new int[] {6}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/7068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS + "_Wild", AthsGlobal.COLEUS, new int[] {7}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/5068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COLEUS /*Autumn_Rainbow*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountain Range","Mountain Range Edge","High Plains","Mountains","Foothills"}, new String[]{"Africa", "Asia"},
				/*size*/10, /*dispersion*/5, /*rarity*/7068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1150f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COMMON_CATCHFLY, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Africa"},
				/*size*/12, /*dispersion*/4, /*rarity*/3284, /*minAltitude*/0, /*maxAltitude*/150, /*minTemp*/11f, /*maxTemp*/22f, /*minRain*/600f, /*maxRain*/890f, /*minEVT*/0.0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.COMMON_REEDS, new int[] {0}, new String[] {"ore:blockSoil", "terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Estuary","Lakeshore","Riverbank","Swamp","River"}, new String[]{"Africa","Europe","Asia","Americas"},
				/*size*/150, /*dispersion*/3, /*rarity*/484, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/0f, /*maxTemp*/30f, /*minRain*/350f, /*maxRain*/2400f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/-0.2f);
		athsPlantHelper(AthsGlobal.COMMON_STINKHORN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas", "Asia", "Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/220, /*minTemp*/-5f, /*maxTemp*/13f, /*minRain*/670f, /*maxRain*/3000f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.COMMON_TREE_FERN + "_Riverbank",AthsGlobal.COMMON_TREE_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[] {"Riverbank"}, new String[]{"Africa"},
				/*size*/10, /*dispersion*/10, /*rarity*/2984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/1100f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COMMON_TREE_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/10, /*dispersion*/10, /*rarity*/3984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/1100f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CONGO_FERN, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Africa"},
				/*size*/9, /*dispersion*/3, /*rarity*/906, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.CORDYCEPS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/8396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CORNFLOWER, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/5, /*dispersion*/4, /*rarity*/3350, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/13f, /*minRain*/420f, /*maxRain*/890f, /*minEVT*/0f, /*maxEVT*/1f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.CRABAPPLE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/8, /*dispersion*/15, /*rarity*/6000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/19f, /*minRain*/320f, /*maxRain*/1100f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CRABAPPLE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/4, /*dispersion*/15, /*rarity*/7000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/19f, /*minRain*/320f, /*maxRain*/1100f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CRABAPPLE2, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/8, /*dispersion*/15, /*rarity*/6000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/19f, /*minRain*/320f, /*maxRain*/1100f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CREEPING_BELLFLOWER, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/25, /*dispersion*/3, /*rarity*/5884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/24f, /*minRain*/550f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.CREEPING_CHARLIE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/35, /*dispersion*/3, /*rarity*/1056, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/33f, /*minRain*/650f, /*maxRain*/1200f, /*minEVT*/0.5f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.CRETAN_BRAKE_FERN + "_Variegated", AthsGlobal.CRETAN_BRAKE_FERN, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia","Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/7284, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CRETAN_BRAKE_FERN /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia","Europe"},
				/*size*/5, /*dispersion*/3, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CROCUS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Africa"},
				/*size*/3, /*dispersion*/2, /*rarity*/3084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/23f, /*minRain*/310f, /*maxRain*/930f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.CUP_PLANT, new int[] {0,1}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/7, /*dispersion*/2, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/0f, /*maxTemp*/15f, /*minRain*/600f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.CYCAD, new int[] {0,1}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia","Africa","Americas"},
		        /*size*/10, /*dispersion*/30, /*rarity*/7584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DAFFODIL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/7, /*dispersion*/3, /*rarity*/7384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/28f, /*minRain*/280f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.DAYLILY + "_Nikkokisuge", AthsGlobal.DAYLILY, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Mountains","Mounatains Edge","Mountain Range Edge","Mountain Range","Foothills","High Plains"}, new String[]{"Asia"},
				/*size*/10, /*dispersion*/2, /*rarity*/6984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/18f, /*minRain*/480f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.DAYLILY + "_Tawny", AthsGlobal.DAYLILY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/10, /*dispersion*/2, /*rarity*/5884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/18f, /*minRain*/480f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.DAYLILY /*Lemon*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia", "Europe"},
				/*size*/10, /*dispersion*/2, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/16f, /*minRain*/480f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.DEADLY_NIGHTSHADE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia", "Europe"},
				/*size*/12, /*dispersion*/4, /*rarity*/5228, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/6000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DEADNETTLE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Africa"},
				/*size*/25, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/20f, /*minRain*/590f, /*maxRain*/2450f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.DEATH_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/13f, /*minRain*/700f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DEER_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Foothills","Swamp","Peat Bog","Mountains","Mountains Edge","Mountain Range Edge","Mountain Range"}, new String[]{"Americas","Asia","Europe"},
				/*size*/18, /*dispersion*/5, /*rarity*/3684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/14f, /*minRain*/800f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DELTA_MAIDENHAIR_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/12, /*dispersion*/4, /*rarity*/3300, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/990f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DELTA_MAIDENHAIR_FERN_EPIPHYTE, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/1200, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/990f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DESERT_ROSE, new int[] {0}, new String[] {"ore:blockSoil", "ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Africa"},
				/*size*/6, /*dispersion*/5, /*rarity*/7584, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/65f, /*maxRain*/200f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.DESTROYING_ANGEL + "_European", AthsGlobal.DESTROYING_ANGEL, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-14f, /*maxTemp*/19f, /*minRain*/700f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DESTROYING_ANGEL /*American*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-14f, /*maxTemp*/19f, /*minRain*/700f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DEVILS_CLUB, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/30, /*dispersion*/4, /*rarity*/6084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/9f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DEVILS_FINGERS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Asia"},
				/*size*/4, /*dispersion*/1, /*rarity*/5384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/0.5f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.DEVILS_TOUNGE, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia"},
				/*size*/2, /*dispersion*/6, /*rarity*/10096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/20f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DIAMONDLEAF_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/10, /*dispersion*/10, /*rarity*/5984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/25f, /*maxTemp*/40f, /*minRain*/1100f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DIPTERIS_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"High Hills","Mountains","Mountain Range","Mountains Edge","Mountain Range Edge","Foothills","High Hills Edge"}, new String[]{"Asia"},
				/*size*/40, /*dispersion*/4, /*rarity*/8128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/28f, /*minRain*/680f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DOLLS_EYES, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","High Plains","High Hills","High Hills Edge","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/5, /*rarity*/4444, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-11f, /*maxTemp*/6f, /*minRain*/720f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DRYADS_SADDLE, new int[] {0}, new String[] {"whiteelm","ash","maple","willow","aspen","sycamore"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/6000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/1f, /*maxTemp*/16f, /*minRain*/700f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DUCKWEED, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/64, /*dispersion*/1, /*rarity*/668, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/500f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.DUMB_CANE, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/6, /*dispersion*/4, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/30f, /*minRain*/900f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DUNE_GRASS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary"}, new String[]{"Americas","Asia"},
				/*size*/12, /*dispersion*/4, /*rarity*/256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/220f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.DWARF_PALMETTO, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/15, /*dispersion*/16, /*rarity*/4969, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/26f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.EARTHBALL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Americas","Africa","Asia"},
				/*size*/7, /*dispersion*/4, /*rarity*/4996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/500f, /*maxRain*/1400f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.EARTHSTAR, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Americas","Africa","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/29f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.EASTERN_SKUNK_CABBAGE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Lakeshore","Riverbank","Swamp","Peat Bog","River"}, new String[]{"Americas"},
				/*size*/43, /*dispersion*/2, /*rarity*/7012, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-1f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.EGYPTIAN_AUTUMN_CROCUS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Africa","Europe","Asia"},
				/*size*/6, /*dispersion*/3, /*rarity*/3884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/26f, /*minRain*/130f, /*maxRain*/300f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.ELEPHANT_GRASS + "_Dense", AthsGlobal.ELEPHANT_GRASS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains"}, new String[]{"Africa"},
				/*size*/120, /*dispersion*/3, /*rarity*/484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/32f, /*minRain*/505f, /*maxRain*/520f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-0.5f);
		athsPlantHelper(AthsGlobal.ELEPHANT_GRASS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains"}, new String[]{"Africa"},
				/*size*/200, /*dispersion*/2, /*rarity*/8384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/32f, /*minRain*/330f, /*maxRain*/700f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.ENGLISH_IVY, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/15, /*dispersion*/1, /*rarity*/1900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/19f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ENOKI /*Enokitake*/, new int[] {0}, new String[] {"willow","birch","whiteelm"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/4200, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/3f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ENOKI + "_Velvet_Shank", AthsGlobal.ENOKI, new int[] {1}, new String[] {"whiteelm","ash","oak"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/4200, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/3f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ENTOLOMA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/5, /*dispersion*/3, /*rarity*/6096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/15f, /*minRain*/900f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.EUROPEAN_BEDSTRAW, new int[] {0,1,2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia", "Europe"},
				/*size*/50, /*dispersion*/3, /*rarity*/3228, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/6000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FIELD_HORSETAIL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Lake","Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp","Estuary"}, new String[]{"Americas", "Asia", "Europe"},
				/*size*/23, /*dispersion*/3, /*rarity*/4012, /*minAltitude*/0, /*maxAltitude*/150, /*minTemp*/-10f, /*maxTemp*/12f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.FILMY_FERN, new int[] {0}, new String[] {"ore:stone","alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia", "Europe","Americas","Africa"},
				/*size*/20, /*dispersion*/1, /*rarity*/1028, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/2350f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FIREWEED, new int[] {0}, new String[] {"ore:blockSoil","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/60, /*dispersion*/3, /*rarity*/4384, /*minAltitude*/144, /*maxAltitude*/225, /*minTemp*/-13f, /*maxTemp*/8f, /*minRain*/550f, /*maxRain*/1300f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.FLAMINGO_FLOWER, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/24f, /*maxTemp*/35f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.FLOWERPOT_DAPPERLING, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/5096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/24f, /*maxTemp*/33f, /*minRain*/850f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FOUNTAIN_GRASS + "_Purple", AthsGlobal.FOUNTAIN_GRASS, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia"},
				/*size*/120, /*dispersion*/4, /*rarity*/9784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/38f, /*minRain*/350f, /*maxRain*/730f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.FOUNTAIN_GRASS /*Green*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia"},
				/*size*/120, /*dispersion*/4, /*rarity*/6784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/38f, /*minRain*/350f, /*maxRain*/730f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.FOXGLOVE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Africa"},
				/*size*/6, /*dispersion*/2, /*rarity*/3484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/18f, /*minRain*/510f, /*maxRain*/990f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.FREESIA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/6, /*dispersion*/5, /*rarity*/5384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/500f, /*maxRain*/1080f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.FRINGED_ACALYPHA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia"},
				/*size*/13, /*dispersion*/3, /*rarity*/2980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/30f, /*minRain*/650f, /*maxRain*/900f, /*minEVT*/1f, /*maxEVT*/5f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.GARDEN_PHLOX + "_Flamingo", AthsGlobal.GARDEN_PHLOX, new int[] {1}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Asia", "Europe"},
				/*size*/22, /*dispersion*/4, /*rarity*/5028, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GARDEN_PHLOX + "_Starfire", AthsGlobal.GARDEN_PHLOX, new int[] {2}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Asia", "Europe"},
				/*size*/22, /*dispersion*/4, /*rarity*/5928, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GARDEN_PHLOX + "_White_Admiral", AthsGlobal.GARDEN_PHLOX, new int[] {3}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Asia", "Europe"},
				/*size*/22, /*dispersion*/4, /*rarity*/4528, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GARDEN_PHLOX /*Blue_Paradise*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Asia", "Europe"},
				/*size*/22, /*dispersion*/4, /*rarity*/5228, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GARLIC_MUSTARD, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia", "Europe"},
				/*size*/70, /*dispersion*/3, /*rarity*/4228, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/19f, /*minRain*/750f, /*maxRain*/6000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GERANIUM + "Meadow", AthsGlobal.GERANIUM, new int[] {2}, new String[] {"ore:blockSoil"}, new String[] {"Mountains","Mountains Edge","Mountain Range","Mountain Range Edge","Foothills","High Hills","High Hills Edge","High Plains"}, new String[]{"Europe","Asia"},
				/*size*/14, /*dispersion*/3, /*rarity*/5700, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/1f, /*maxTemp*/16f, /*minRain*/350f, /*maxRain*/670f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.GERANIUM + "_Clarkes", AthsGlobal.GERANIUM, new int[] {1}, new String[] {"ore:blockSoil"}, new String[] {"Mountains","Mountains Edge","Mountain Range","Mountain Range Edge","Foothills","High Hills"}, new String[]{"Asia"},
				/*size*/14, /*dispersion*/3, /*_rarity*/5700, /*minAltitude*/175, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/15f, /*minRain*/350f, /*maxRain*/670f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.GERANIUM + "_Wild", AthsGlobal.GERANIUM, new int[] {3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/14, /*dispersion*/5, /*rarity*/5900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/18f, /*minRain*/330f, /*maxRain*/770f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.GERANIUM /*Bloody*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/14, /*dispersion*/3, /*rarity*/5700, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/3f, /*maxTemp*/13f, /*minRain*/350f, /*maxRain*/670f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.GIANT_HOGWEED, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Mountains","High Hills","High Hills Edge","Mountains Edge","Foothills","Riverbank"}, new String[]{"Europe"},
				/*size*/13, /*dispersion*/8, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/14f, /*minRain*/660f, /*maxRain*/930f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.GIANT_PHILODENDRON, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/8, /*dispersion*/6, /*rarity*/3784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GIANT_PHILODENDRON_EPIPHYTE, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/3784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GIFBOOM, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"Plains","High Hills Edge","Rolling Hills","High Plains","Foothills"}, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/656, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/31f, /*minRain*/78f, /*maxRain*/200f, /*minEVT*/1f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.GLADIOLUS + "_Elvira", AthsGlobal.GLADIOLUS, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Europe"},
				/*size*/6, /*dispersion*/5, /*rarity*/5374, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/21f, /*minRain*/500f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.GLADIOLUS + "_Nathalie", AthsGlobal.GLADIOLUS, new int[] {2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Europe"},
				/*size*/6, /*dispersion*/5, /*rarity*/6184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/21f, /*minRain*/500f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.GLADIOLUS + "_Yellowstone", AthsGlobal.GLADIOLUS, new int[] {3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Europe"},
				/*size*/6, /*dispersion*/5, /*rarity*/5084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/21f, /*minRain*/500f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.GLADIOLUS /*Blue_Moon*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Europe"},
				/*size*/6, /*dispersion*/5, /*rarity*/5384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/21f, /*minRain*/500f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.GOLDEN_LEATHER_FERN, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass","ore:block:Gravel"}, new String[]{"Salt_Swamp","Estuary","Riverbank"}, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/20, /*dispersion*/2, /*rarity*/1288, /*minAltitude*/0, /*maxAltitude*/153, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.GOLDEN_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/5296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-12f, /*maxTemp*/20f, /*minRain*/980f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GOLDEN_SPINDLES, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/16f, /*minRain*/620f, /*maxRain*/2000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GOLDEN_WAXCAP + "_Grassland", AthsGlobal.GOLDEN_WAXCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-14f, /*maxTemp*/15f, /*minRain*/530f, /*maxRain*/710f, /*minEVT*/0.5f, /*maxEVT*/9f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GOLDEN_WAXCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-14f, /*maxTemp*/15f, /*minRain*/980f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/9f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GRIFFONIA_SIMPLICIFOLIA, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/720f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GROUND_CEDARS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Africa","Europe"},
				/*size*/29, /*dispersion*/8, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/10f, /*minRain*/810f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HAMMOCK_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/11, /*dispersion*/5, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HARTS_TONGUE_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas","Africa","Europe"},
				/*size*/14, /*dispersion*/8, /*rarity*/9568, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/3f, /*maxTemp*/22f, /*minRain*/750f, /*maxRain*/1300f, /*minEVT*/2f, /*maxEVT*/10f,/*forestGen*/-0.4f);
		athsPlantHelper(AthsGlobal.HAY_SCENTED_FERN + "_Appalachian", AthsGlobal.HAY_SCENTED_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"High Hills","High Hills Edge","Mountains","Mountains Edge","Mountain Range Edge","Mountain Range","Foothills"}, new String[]{"Americas"},
				/*size*/52, /*dispersion*/2, /*rarity*/4068, /*minAltitude*/0, /*maxAltitude*/220, /*minTemp*/-7f, /*maxTemp*/17f, /*minRain*/850f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.8f);
		athsPlantHelper(AthsGlobal.HAY_SCENTED_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"Plains","High Plains","Rolling Hills","Lakeshore","Riverbank"}, new String[]{"Americas"},
				/*size*/172, /*dispersion*/1, /*rarity*/6668, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-7f, /*maxTemp*/17f, /*minRain*/850f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.8f);
		athsPlantHelper(AthsGlobal.HEART_LEAF_PHILODENDRON, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/1, /*rarity*/3284, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HEATHER + "_White", AthsGlobal.HEATHER, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Europe"},
				/*size*/45, /*dispersion*/9, /*rarity*/384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/20f, /*minRain*/160f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.HEATHER /*Pink*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Europe"},
				/*size*/40, /*dispersion*/9, /*rarity*/484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/20f, /*minRain*/160f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.HEDGEHOG_LIP_ORCHID, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/2500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/33f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HEMLOCK_VARNISH_SHELF, new int[] {0}, new String[] {"whitecedar","pine","spruce","redwood"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/7500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HIBISCUS + "_Pink", AthsGlobal.HIBISCUS, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia"},
				/*size*/7, /*dispersion*/7, /*rarity*/6084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/24f, /*minRain*/600f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.HIBISCUS + "_Red", AthsGlobal.HIBISCUS, new int[] {2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia"},
				/*size*/7, /*dispersion*/7, /*rarity*/6684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/30f, /*minRain*/600f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.HIBISCUS + "_White", AthsGlobal.HIBISCUS, new int[] {3}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/7, /*dispersion*/7, /*rarity*/6484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/17f, /*minRain*/600f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.HIBISCUS /*Orange*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/7, /*dispersion*/7, /*rarity*/6969, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/26f, /*maxTemp*/30f, /*minRain*/600f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.HOLLY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa"},
		        /*size*/1, /*dispersion*/1, /*rarity*/3628, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/15f, /*minRain*/650f, /*maxRain*/3000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOLLY + "_Thicket", AthsGlobal.HOLLY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa"},
		        /*size*/50, /*dispersion*/3, /*rarity*/9528, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/15f, /*minRain*/650f, /*maxRain*/3000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOLLY_FERN, new int[] {0}, new String[] {"ore:blockSoil","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/19, /*dispersion*/5, /*rarity*/6168, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.HONEYCOMB_FUNGUS, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/6000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/900f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HONEY_FUNGUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/5796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_DANCING_QUEEN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3628, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_ELEGANS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3828, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_FRANCES_WILLIAMS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3828, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_HALCYON, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3628, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_NARROW_LEAVED, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3828, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_PATRIOT, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3528, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_SUM_AND_SUBSTANCE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3828, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_TOUCH_OF_CLASS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3828, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HOSTA_VULCAN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/3828, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HYACINTH + "_Cultivars", AthsGlobal.HYACINTH, new int[] {1,2,3,4,5,6}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/10, /*dispersion*/4, /*rarity*/6300, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/23f, /*minRain*/220f, /*maxRain*/410f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.HYACINTH, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/10, /*dispersion*/4, /*rarity*/3300, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/23f, /*minRain*/220f, /*maxRain*/410f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.HYDRANGEA + "_Limelight", AthsGlobal.HYDRANGEA, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/5284, /*minAltitude*/0, /*maxAltitude*/170, /*minTemp*/8f, /*maxTemp*/17f, /*minRain*/680f, /*maxRain*/2600f, /*minEVT*/1f, /*maxEVT*/6f,/*forestGen*/0.8f);
		athsPlantHelper(AthsGlobal.HYDRANGEA + "_Nikko_Blue", AthsGlobal.HYDRANGEA, new int[] {3}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/6884, /*minAltitude*/0, /*maxAltitude*/170, /*minTemp*/8f, /*maxTemp*/17f, /*minRain*/680f, /*maxRain*/2600f, /*minEVT*/1f, /*maxEVT*/6f,/*forestGen*/0.8f);
		athsPlantHelper(AthsGlobal.HYDRANGEA + "_Passion", AthsGlobal.HYDRANGEA, new int[] {2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/170, /*minTemp*/8f, /*maxTemp*/17f, /*minRain*/680f, /*maxRain*/2600f, /*minEVT*/1f, /*maxEVT*/6f,/*forestGen*/0.8f);
		athsPlantHelper(AthsGlobal.HYDRANGEA /*Anabelle*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/4, /*dispersion*/4, /*rarity*/5884, /*minAltitude*/0, /*maxAltitude*/170, /*minTemp*/8f, /*maxTemp*/17f, /*minRain*/680f, /*maxRain*/2600f, /*minEVT*/1f, /*maxEVT*/6f,/*forestGen*/0.8f);
		athsPlantHelper(AthsGlobal.INDIAN_PIPE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas"},
				/*size*/3, /*dispersion*/1, /*rarity*/11384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/13f, /*minRain*/780f, /*maxRain*/10000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.INDIGO_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas"},
				/*size*/3, /*dispersion*/4, /*rarity*/4512, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/24f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/1f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.INDIGO_PINKGILLS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  new String [] {"Mountains","Mountain Range","Mountains Edge","Mountain Range Edge","Foothills"}, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/5096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/17f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.INK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7896, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/15f, /*minRain*/480f, /*maxRain*/800f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.INTERRUPTED_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/25, /*dispersion*/6, /*rarity*/3768, /*minAltitude*/0, /*maxAltitude*/180, /*minTemp*/-6f, /*maxTemp*/18f, /*minRain*/770f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.7f);
		athsPlantHelper(AthsGlobal.IRIS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Mountains","Mountain Range","Mountain range Edge","Foothills","Mountains Edge","High Plains","Riverbank"}, new String[]{"Americas","Asia","Europe"},
				/*size*/6, /*dispersion*/5, /*rarity*/5384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/13f, /*minRain*/400f, /*maxRain*/680f, /*minEVT*/0f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.JACK_IN_THE_PULPIT, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas"},
				/*size*/3, /*dispersion*/4, /*rarity*/984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/16f, /*minRain*/800f, /*maxRain*/2000f, /*minEVT*/3f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JACK_O_LANTERN_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/18f, /*minRain*/700f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JADE_PLANT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/9, /*dispersion*/3, /*rarity*/1984, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/15f, /*maxTemp*/21f, /*minRain*/60f, /*maxRain*/160f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.JAPANESE_MAPLE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/3, /*dispersion*/15, /*rarity*/6300, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/16f, /*minRain*/640f, /*maxRain*/2500f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JAPANESE_MOUNTAIN_YAM, new int[] {0}, new String[] {"alltrees", "ore:stone"}, new String [] {"Mountains","Mountain Range","Mountains Edge","Mountain Range Edge","Foothills","High Hills","High Hills Edge"}, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/16f, /*minRain*/650f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JAPANESE_STILTGRASS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/52, /*dispersion*/1, /*rarity*/7884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/14f, /*minRain*/880f, /*maxRain*/1600f, /*minEVT*/2f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.JELLY_FUNGUS + "_Apricot_Jelly", AthsGlobal.JELLY_FUNGUS, new int[] {1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/31f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JELLY_FUNGUS + "_Orange_Jelly", AthsGlobal.JELLY_FUNGUS, new int[] {2}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/35f, /*minRain*/750f, /*maxRain*/1200f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JELLY_FUNGUS + "_Snow_Fungus", AthsGlobal.JELLY_FUNGUS, new int[] {4}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/5800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/33f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JELLY_FUNGUS + "_Witchs_Butter", AthsGlobal.JELLY_FUNGUS, new int[] {3}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/28f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JELLY_FUNGUS /*Black_Jelly_Roll*/, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JIAN_CHUN_LUO, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Africa"},
				/*size*/4, /*dispersion*/4, /*rarity*/7984, /*minAltitude*/0, /*maxAltitude*/150, /*minTemp*/-4f, /*maxTemp*/19f, /*minRain*/660f, /*maxRain*/1090f, /*minEVT*/1f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.JUNIPER, new int[] {0,1,2,3,4}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel","ore:stone"}, new String[] {"All","!Ocean","!Hell","!Deep Ocean", "!River","!Beach","!Gravel Beach","!Swamp","!Lake","!Shore","!Salt Swamp","!Lakeshore","!Riverbank","!Estuary"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/20, /*dispersion*/30, /*rarity*/20000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/450f, /*maxRain*/2500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-0.2f);
		athsPlantHelper(AthsGlobal.JUNIPER + "_Outcrop", AthsGlobal.JUNIPER, new int[] {0,1,2,3,4,5}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel","ore:stone"}, new String[] {"Mountains","High Hills","Mountain Range","Mountain Range Edge","Foothills","Mountains Edge","High Hills Edge"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/10, /*dispersion*/15, /*rarity*/12800, /*minAltitude*/180, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/450f, /*maxRain*/2500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.JUNIPER + "_Desert", AthsGlobal.JUNIPER, new int[] {0,1,2,3,4,5}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel","ore:stone"}, new String[] {"Mountains","High Hills","Mountain Range","Mountain Range Edge","Foothills","Mountains Edge","High Hills Edge"}, new String[]{"Americas","Asia","Europe"},
				/*size*/8, /*dispersion*/40, /*rarity*/20000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/88f, /*maxRain*/450f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-1f);
		athsPlantHelper(AthsGlobal.LACCARIA_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/7500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LADY_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/20, /*dispersion*/8, /*rarity*/5968, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-7f, /*maxTemp*/20f, /*minRain*/800f, /*maxRain*/4000f, /*minEVT*/1f, /*maxEVT*/8f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.LAMBS_EAR, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/20, /*dispersion*/8, /*rarity*/5968, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/17f, /*minRain*/230f, /*maxRain*/800f, /*minEVT*/1f, /*maxEVT*/8f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.LAVENDER, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains"}, new String[]{"Africa","Asia","Europe"},
				/*size*/94, /*dispersion*/4, /*rarity*/8684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/25f, /*minRain*/400f, /*maxRain*/700f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LAVENDER_LEAF_SUNDROPS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/3784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/22f, /*minRain*/120f, /*maxRain*/310f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.LEAFY_LOW_UNDERGROWTH + "_Variegated", AthsGlobal.LEAFY_LOW_UNDERGROWTH, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","River","Lake","Riverbank","Swamp"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/18, /*dispersion*/1, /*rarity*/1888, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-2f, /*maxTemp*/30f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LEAFY_LOW_UNDERGROWTH /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","River","Lake","Riverbank","Swamp"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/18, /*dispersion*/1, /*rarity*/488, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-2f, /*maxTemp*/30f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LEAFY_UNDERGROWTH, new int[] {0,1,2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/15, /*dispersion*/5, /*rarity*/128, /*minAltitude*/0, /*maxAltitude*/230, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LECANOPTERIS_FERN, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/1150, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/1190f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LEOPARD_ORCHID, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Salt Swamp","Estuary"}, new String[]{"Africa"},
				/*size*/3, /*dispersion*/6, /*rarity*/7096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/25f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LEOPARD_ORCHID_EPIPHYTE, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/3500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/25f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LILAC, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains"}, new String[]{"Asia","Europe"},
				/*size*/8, /*dispersion*/4, /*rarity*/6484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/19f, /*minRain*/470f, /*maxRain*/900f, /*minEVT*/0.5f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.LILY_OF_THE_VALLEY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/25, /*dispersion*/1, /*rarity*/4370, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/14f, /*minRain*/710f, /*maxRain*/930f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LILY_PAD, new int[] {0,1,2}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/18, /*dispersion*/3, /*rarity*/264, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.LIONS_MANE, new int[] {0}, new String[] {"oak","birch","maple","hickory","ash","chestnut","aspen","gingko","sycamore","whiteelm","willow"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/7500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LITHOPS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/24, /*dispersion*/8, /*rarity*/7984, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/180f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LIVERWORT + "_Moist", AthsGlobal.LIVERWORT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass","ore:stone","ore:blockGravel"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/22, /*dispersion*/1, /*rarity*/3796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/1500f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LIVERWORT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass","ore:stone","ore:blockGravel"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/22, /*dispersion*/1, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LIVERWORT_EPIPHYTE, new int[] {0}, new String[] {"ore:stone"}, new String[] {AthsGlobal.LAND_BIOMES, "Beach","Gravel Beach"}, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/12, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LIVERWORT_EPIPHYTE + "_Moist", AthsGlobal.LIVERWORT, new int[] {0}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/22, /*dispersion*/1, /*rarity*/3796, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/1500f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LIVERWORT_EPIPHYTE + "_Cliff", AthsGlobal.LIVERWORT, new int[] {0}, new String[] {"ore:stone"}, new String[] {AthsGlobal.LAND_BIOMES, "Beach","Gravel Beach"}, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/12, /*dispersion*/2, /*rarity*/1796, /*minAltitude*/0, /*maxAltitude*/155, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/700f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LOBSTER_CLAWS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/4, /*dispersion*/3, /*rarity*/3536, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LOOSE_FLOWERED_ORCHID, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Plains","Estuary","Plains","Swamp","Peat Bog","Salt Swamp","Riverbank","Lakeshore","Rolling Hills"}, new String[]{"Europe","Asia"},
				/*size*/5, /*dispersion*/9, /*rarity*/3556, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/2f, /*maxTemp*/17f, /*minRain*/550f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.LOTUS, new int[] {0,1,2}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Asia"},
				/*size*/60, /*dispersion*/4, /*rarity*/1264, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/2f, /*maxTemp*/24f, /*minRain*/450f, /*maxRain*/6000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.LUPINE + "_Purple", AthsGlobal.LUPINE, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/35, /*dispersion*/4, /*rarity*/7384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/14f, /*minRain*/650f, /*maxRain*/850f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LUPINE + "_Red", AthsGlobal.LUPINE, new int[] {2}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Foothills","Mountains","Mountain Range","Mountain Range Edge","Mountains Edge"}, new String[]{"Americas"},
				/*size*/35, /*dispersion*/4, /*rarity*/7384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/16f, /*minRain*/650f, /*maxRain*/850f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LUPINE + "_Yellow", AthsGlobal.LUPINE, new int[] {3}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","Foothills","Mountains","Mountain Range","Mountain Range Edge","Mountains Edge"}, new String[]{"Americas"},
				/*size*/35, /*dispersion*/4, /*rarity*/7384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/15f, /*minRain*/650f, /*maxRain*/850f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LUPINE /*Blue*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/35, /*dispersion*/4, /*rarity*/7384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/20f, /*minRain*/650f, /*maxRain*/850f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LURID_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/16f, /*minRain*/700f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MADONNA_LILY, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/4, /*dispersion*/3, /*rarity*/4984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/19f, /*minRain*/460f, /*maxRain*/790f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.MAGNOLIA + "_Asia", AthsGlobal.MAGNOLIA, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/4, /*dispersion*/15, /*rarity*/7000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/29f, /*minRain*/650f, /*maxRain*/1200f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.MAGNOLIA, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/4, /*dispersion*/15, /*rarity*/7000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/23f, /*minRain*/650f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.MAGNOLIA2 + "_Asia", AthsGlobal.MAGNOLIA2, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/4, /*dispersion*/15, /*rarity*/7000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/29f, /*minRain*/650f, /*maxRain*/1200f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.MAGNOLIA2, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/4, /*dispersion*/15, /*rarity*/7000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/23f, /*minRain*/650f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.MAIDENHAIR_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia","Americas"},
				/*size*/40, /*dispersion*/3, /*rarity*/5484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MAIDENHAIR_SPLEENWORT, new int[] {0}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Americas","Africa"},
				/*size*/3, /*dispersion*/1, /*rarity*/9000, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MALLOW, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Estuary","Lakeshore","Riverbank","Swamp","River"}, new String[]{"Africa","Europe","Asia"},
				/*size*/70, /*dispersion*/4, /*rarity*/784, /*minAltitude*/0, /*maxAltitude*/148, /*minTemp*/5f, /*maxTemp*/30f, /*minRain*/450f, /*maxRain*/2400f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.MARIGOLD, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/10, /*dispersion*/6, /*rarity*/1284, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/28f, /*minRain*/140f, /*maxRain*/360f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.MARTAGON_LILY + "_Chameleon", AthsGlobal.MARTAGON_LILY, new int[] {3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/4, /*dispersion*/3, /*rarity*/4984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/16f, /*minRain*/410f, /*maxRain*/720f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.MARTAGON_LILY + "_Orange_Marmelade", AthsGlobal.MARTAGON_LILY, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/4, /*dispersion*/3, /*rarity*/3884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/16f, /*minRain*/410f, /*maxRain*/720f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.MARTAGON_LILY + "_Snowy_Morning", AthsGlobal.MARTAGON_LILY, new int[] {2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/4, /*dispersion*/3, /*rarity*/4184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/16f, /*minRain*/410f, /*maxRain*/720f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.MARTAGON_LILY /*Claude_Shride*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/4, /*dispersion*/3, /*rarity*/4484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/16f, /*minRain*/410f, /*maxRain*/720f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.MATONIA_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/19, /*dispersion*/4, /*rarity*/6584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/1500f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MAYAPPLE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","River","Lake","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/24, /*dispersion*/1, /*rarity*/1288, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-2f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MEDIUM_UNDERGROWTH + "_Savanna", AthsGlobal.MEDIUM_UNDERGROWTH, new int[] {0,1,2}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/20, /*dispersion*/2, /*rarity*/200, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/40f, /*minRain*/550f, /*maxRain*/700f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.MEDIUM_UNDERGROWTH, new int[] {0,1,2}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/8, /*dispersion*/4, /*rarity*/256, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/40f, /*minRain*/350f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.MEXICAN_GIANT_HORSETAIL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Estuary","Lakeshore","Riverbank","Swamp","Peat Bog","River"}, new String[]{"Americas"},
				/*size*/80, /*dispersion*/6, /*rarity*/8484, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/20f, /*maxTemp*/35f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/-0.2f);
		athsPlantHelper(AthsGlobal.MEXICAN_TREE_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/10, /*dispersion*/10, /*rarity*/5184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MISTLETOE + "_Desert", AthsGlobal.MISTLETOE, new int[] {1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/750, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/25f, /*minRain*/85f, /*maxRain*/200f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MISTLETOE + "_Dwarf", AthsGlobal.MISTLETOE, new int[] {2}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Americas","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/2700, /*minAltitude*/155, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/15f, /*minRain*/500f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MISTLETOE + "_European", AthsGlobal.MISTLETOE, new int[] {3}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/2500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/550f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MISTLETOE /*American*/, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/2500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/20f, /*minRain*/550f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MONKEY_CUP, new int[] {0,1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/26f, /*maxTemp*/40f, /*minRain*/1340f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MONKEY_CUP_EPIPHYTE, new int[] {0,1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/950, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/26f, /*maxTemp*/40f, /*minRain*/1340f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MOONWORT, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas", "Asia", "Europe","Africa"},
				/*size*/7, /*dispersion*/4, /*rarity*/3296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/17f, /*minRain*/400f, /*maxRain*/780f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.MOREL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas", "Asia", "Europe","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/9096, /*minAltitude*/0, /*maxAltitude*/220, /*minTemp*/-2f, /*maxTemp*/14f, /*minRain*/700f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.MOUNTAIN_AVENS + "_Mountain",AthsGlobal.MOUNTAIN_AVENS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[] {"Mountains","Mountains Edge","Mountain Range Edge","Mountain Range","Foothills"}, new String[]{"Americas","Asia","Europe"},
				/*size*/24, /*dispersion*/2, /*rarity*/5068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/3f, /*minRain*/250f, /*maxRain*/760f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.MOUNTAIN_AVENS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/28, /*dispersion*/2, /*rarity*/4368, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/-5f, /*minRain*/200f, /*maxRain*/760f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.MOUNTAIN_BLECHNUM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[] {"Mountains","Mountains Edge","Mountain Range Edge","Mountain Range","Foothills"}, new String[]{"Asia"},
				/*size*/31, /*dispersion*/2, /*rarity*/4068, /*minAltitude*/155, /*maxAltitude*/255, /*minTemp*/19f, /*maxTemp*/40f, /*minRain*/1250f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.MULCH_FIELDCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/14f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.NARBON_VETCH, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/34, /*dispersion*/3, /*rarity*/5384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/22f, /*minRain*/450f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.NETTLE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas","Africa"},
				/*size*/25, /*dispersion*/3, /*rarity*/3484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/23f, /*minRain*/470f, /*maxRain*/930f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.NIPA_PALM, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass","ore:block:Gravel"}, new String[]{"Salt_Swamp","Estuary"}, new String[]{"Asia"},
				/*size*/25, /*dispersion*/3, /*rarity*/1288, /*minAltitude*/0, /*maxAltitude*/150, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.NORTHERN_BUSH_HONEYSUCKLE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/2, /*rarity*/4484, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/15f, /*minRain*/650f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.NORTHERN_OAK_FERN + "_Oak", AthsGlobal.NORTHERN_OAK_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas"},
				/*size*/24, /*dispersion*/2, /*rarity*/4984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/15f, /*minRain*/500f, /*maxRain*/1200f, /*minEVT*/0.5f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.NORTHERN_OAK_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas"},
				/*size*/38, /*dispersion*/4, /*rarity*/7784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-12f, /*maxTemp*/13f, /*minRain*/680f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.OCOTILLO, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/10, /*dispersion*/17, /*rarity*/3050, /*minAltitude*/0, /*maxAltitude*/220, /*minTemp*/13f, /*maxTemp*/26f, /*minRain*/85f, /*maxRain*/230f, /*minEVT*/1f, /*maxEVT*/5.5f);
		athsPlantHelper(AthsGlobal.OLD_MANS_BEARD_LICHEN, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Africa","Europe","Americas"},
				/*size*/11, /*dispersion*/1, /*rarity*/4500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.OLD_MAN_OF_THE_WOODS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Mountains", "Mountains Edge","Foothills","Mountain Range Edge","Mountain Range"}, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/10f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ORANGE_MYCENA, new int[] {1}, new String[] {"whiteelm","willow","aspen","maple","birch","oak","sycamore","hickory","chestnut","ash","gingko"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/5600, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/-4f, /*maxTemp*/20f, /*minRain*/750f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ORANGE_PORE_FUNGUS, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/6000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/19f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ORGAN_PIPE_CACTUS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/24, /*rarity*/2048, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/16f, /*maxTemp*/30f, /*minRain*/100f, /*maxRain*/190f, /*minEVT*/0f, /*maxEVT*/3.5f);
		athsPlantHelper(AthsGlobal.OSTRICH_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe"},
				/*size*/25, /*dispersion*/6, /*rarity*/4868, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-5f, /*maxTemp*/20f, /*minRain*/800f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.3f);
		athsPlantHelper(AthsGlobal.OYSTER_MUSHROOM, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-9f, /*maxTemp*/24f, /*minRain*/820f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.OYSTER_MUSHROOM + "_Golden", AthsGlobal.OYSTER_MUSHROOM, new int[] {1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/6500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/820f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.OYSTER_MUSHROOM + "_Aspen", AthsGlobal.OYSTER_MUSHROOM, new int[] {2}, new String[] {"aspen"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/15f, /*minRain*/820f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.OYSTER_MUSHROOM + "_Summer", AthsGlobal.OYSTER_MUSHROOM, new int[] {3}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/4600, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/820f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.OYSTER_MUSHROOM + "_Pink", AthsGlobal.OYSTER_MUSHROOM, new int[] {4}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/6800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/820f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.OYSTER_MUSHROOM + "_King_Trumpet", AthsGlobal.OYSTER_MUSHROOM, new int[] {5}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/7500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/25f, /*minRain*/820f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.OYSTER_MUSHROOM + "_Lynx_Paw", AthsGlobal.OYSTER_MUSHROOM, new int[] {6}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/32f, /*minRain*/820f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PAINTED_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/16, /*dispersion*/4, /*rarity*/6468, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/800f, /*maxRain*/6000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.PAINTED_LADY, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/14, /*dispersion*/5, /*rarity*/1980, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/14f, /*maxTemp*/25f, /*minRain*/60f, /*maxRain*/250f, /*minEVT*/1f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.PALE_UMBRELLA_ORCHID, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/2500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/30f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PANTHER_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/11f, /*minRain*/550f, /*maxRain*/1500f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PAPYRUS + "_Swamp", AthsGlobal.PAPYRUS, new int[] {0}, new String[] {"ore:blockSoil", "terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Swamp"}, new String[]{"Africa"},
				/*size*/80, /*dispersion*/4, /*rarity*/1024, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/100f, /*maxRain*/3400f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.PAPYRUS, new int[] {0}, new String[] {"ore:blockSoil", "terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Estuary","Lakeshore","Riverbank","River"}, new String[]{"Africa"},
				/*size*/150, /*dispersion*/3, /*rarity*/524, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/80f, /*maxRain*/3400f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.PARROT_WAXCAP, new int[] {0,1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Africa","Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/13f, /*minRain*/980f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/9f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PEACE_LILY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/33f, /*minRain*/1650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.PEONY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/5, /*dispersion*/5, /*rarity*/6600, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/23f, /*minRain*/450f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.6f);
		athsPlantHelper(AthsGlobal.PERIWINKLE, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","River","Lake","Riverbank","Swamp","Peat Bog"}, new String[]{"Europe"},
				/*size*/18, /*dispersion*/1, /*rarity*/3028, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/8f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PERSIAN_IVY +"_Variegated", AthsGlobal.PERSIAN_IVY, new int[] {1}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/3400, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/24f, /*minRain*/190f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.PERSIAN_IVY /*Normal*/, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2200, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/24f, /*minRain*/190f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.PETUNIA + "_Inflata", AthsGlobal.PETUNIA, new int[] {1}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Americas"},
				/*size*/8, /*dispersion*/4, /*rarity*/8528, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/17f, /*minRain*/550f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.PETUNIA + "_White_Moon", AthsGlobal.PETUNIA, new int[] {3}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Americas"},
				/*size*/8, /*dispersion*/4, /*rarity*/7028, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/5f, /*minRain*/550f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.PETUNIA + "_Wild_Violet", AthsGlobal.PETUNIA, new int[] {2}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Americas"},
				/*size*/8, /*dispersion*/4, /*rarity*/7528, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/16f, /*minRain*/550f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.PETUNIA /*Brazilian_Red*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank"}, new String[]{"Americas"},
				/*size*/8, /*dispersion*/4, /*rarity*/12928, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/16f, /*minRain*/550f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.PINEDROPS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"High Hills","High Hills Edge","Plains","Rolling Hills","Estuary","Riverbank","Salt Swamp","Swamp","Peat Bog","Lakeshore"}, new String[]{"Americas"},
				/*size*/4, /*dispersion*/4, /*rarity*/5984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/24f, /*minRain*/650f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/8f);
		athsPlantHelper(AthsGlobal.PINWHEEL_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/5096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/32f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PLANTAIN_WEED, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"High Hills","High Hills Edge","Mountains","Mountain Range","Mountain Range Edge","Foothills","Mountains Edge","Estuary","Riverbank","Salt Swamp","Swamp","Peat Bog","Lakeshore"}, new String[]{"Americas","Europe","Asia"},
				/*size*/23, /*dispersion*/2, /*rarity*/3984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/35f, /*minRain*/450f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.POINSETTIA, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockGravel"}, new String[]{"Mountains Edge","Foothills","Mountain Range Edge","High Plains","High Hills","High Hills Edge"}, new String[]{"Americas"},
				/*size*/9, /*dispersion*/6, /*rarity*/5384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/28f, /*minRain*/460f, /*maxRain*/740f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.POISON_IVY, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","River","Lake","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas", "Asia"},
				/*size*/18, /*dispersion*/1, /*rarity*/4888, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-2f, /*maxTemp*/22f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.POKEWEED, new int[] {0,1}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","High Plains","High Hills","High Hills Edge","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/14, /*dispersion*/3, /*rarity*/3084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/14f, /*minRain*/700f, /*maxRain*/3000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.POND_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Mountains","Mountain Range Edge","Mountain Range","Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp","Estuary"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/20, /*dispersion*/2, /*rarity*/256, /*minAltitude*/0, /*maxAltitude*/146, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/250f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.POND_GRASS + "_Woodland", AthsGlobal.POND_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Mountains","Mountain Range Edge","Mountain Range","Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp","Estuary"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/20, /*dispersion*/2, /*rarity*/5436, /*minAltitude*/0, /*maxAltitude*/166, /*minTemp*/-5f, /*maxTemp*/40f, /*minRain*/900f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.PORCELAINFLOWER + "_Variegated", AthsGlobal.PORCELAINFLOWER, new int[] {1}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/26f, /*minRain*/1100f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PORCELAINFLOWER, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/26f, /*minRain*/1100f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PRAIRIE_GRASS + "_DensePrairie", AthsGlobal.PRAIRIE_GRASS, new int[] {0,1,2}, new String[] {"ore:blockSoil"}, new String[]{"Plains","High Hills","Mountains","Rolling Hills","High Hills Edge","Foothills","Mountain Range Edge","Mountain Range","High Plains"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/120, /*dispersion*/6, /*rarity*/2530, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/250f, /*maxRain*/420f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.PRAIRIE_GRASS, new int[] {0,1,2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Mountains","Rolling Hills","High Hills Edge","Foothills","Mountain Range Edge","Mountain Range","High Plains"}, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/100, /*dispersion*/4, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/200f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.PRICKLY_PEAR + "_Eastern", AthsGlobal.PRICKLY_PEAR, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains"}, new String[]{"Americas"},
				/*size*/12, /*dispersion*/10, /*rarity*/10980, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/7f, /*maxTemp*/18f, /*minRain*/200f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.PRICKLY_PEAR, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Americas"},
				/*size*/12, /*dispersion*/10, /*rarity*/980, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/13f, /*maxTemp*/25f, /*minRain*/70f, /*maxRain*/200f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.PUFFBALL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Americas","Africa","Asia"},
				/*size*/7, /*dispersion*/4, /*rarity*/4496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/17f, /*minRain*/550f, /*maxRain*/1700f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.PURPLE_FAIRY_CLUB, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Mountains", "Mountains Edge","Foothills","Mountain Range Edge","Mountain Range"}, new String[]{"Americas"},
				/*size*/4, /*dispersion*/2, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/16f, /*minRain*/530f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PURPUREORACHIS, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/2500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/35f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.QUAQUA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Africa"},
				/*size*/10, /*dispersion*/7, /*rarity*/1580, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/14f, /*maxTemp*/29f, /*minRain*/70f, /*maxRain*/320f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.QUEEN_ANNES_LACE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Africa"},
				/*size*/15, /*dispersion*/3, /*rarity*/2240, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/17f, /*minRain*/550f, /*maxRain*/810f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.QUEEN_OF_THE_ANDES, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Foothills","Mountains Edge","Mountains","Mountain Range Edge","Mountain Range"}, new String[]{"Americas"},
				/*size*/8, /*dispersion*/18, /*rarity*/1580, /*minAltitude*/210, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/27f, /*minRain*/200f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.RAFFLESIA, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Salt Swamp","Estuary"}, new String[]{"Asia"},
				/*size*/3, /*dispersion*/6, /*rarity*/10096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/25f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RATTLESNAKE_PLANT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains"}, new String[]{"Americas"},
				/*size*/16, /*dispersion*/5, /*rarity*/3580, /*minAltitude*/0, /*maxAltitude*/220, /*minTemp*/19f, /*maxTemp*/25f, /*minRain*/280f, /*maxRain*/820f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.RAY_FERN, new int[] {0}, new String[] {"ore:blockSoil","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Africa"},
				/*size*/24, /*dispersion*/3, /*rarity*/4768, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*/500f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.REDBUD + "_Europe", AthsGlobal.REDBUD, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
                /*size*/1, /*dispersion*/1, /*rarity*/8000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/18f, /*minRain*/450f, /*maxRain*/900f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.REDBUD /*Americas&Asia*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
                /*size*/1, /*dispersion*/1, /*rarity*/5000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/19f, /*minRain*/550f, /*maxRain*/1400f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.REDBUD + "_Weeping", AthsGlobal.REDBUD /*Americas&Asia*/, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/10000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/19f, /*minRain*/550f, /*maxRain*/1400f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);	
		athsPlantHelper(AthsGlobal.REDBUD + "_Weeping_Europe", AthsGlobal.REDBUD, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/13000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/18f, /*minRain*/450f, /*maxRain*/900f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);	
		athsPlantHelper(AthsGlobal.RED_CAPPED_SCABER_STALK, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/12f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RED_CORAL_FUNGUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/8596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/16f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RED_CUP_FUNGUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/5696, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/40f, /*minRain*780f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RED_GOYO, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Asiaa"},
				/*size*/2, /*dispersion*/1, /*rarity*/9580, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/-6f, /*maxTemp*/10f, /*minRain*/0f, /*maxRain*/220f, /*minEVT*/1f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.RED_HOT_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/6f, /*minRain*/750f, /*maxRain*/2200f, /*minEVT*/1f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.REED_CANARY_GRASS, new int[] {0}, new String[] {"ore:blockSoil", "terrafirmacraftplus:FreshWaterStationary", "terrafirmacraftplus:SaltWaterStationary","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Estuary","Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp","River"}, new String[]{"Europe","Asia","Americas"},
				/*size*/90, /*dispersion*/2, /*rarity*/684, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/2f, /*maxTemp*/26f, /*minRain*/450f, /*maxRain*/5400f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/-0.2f);
		athsPlantHelper(AthsGlobal.REINDEER_LICHEN + "_Stone", AthsGlobal.REINDEER_LICHEN, new int[] {0}, new String[] {"ore:blockStone"}, new String[] {"Mountain Range","Mountain Range Edge","High Hills","Mountains"}, new String[]{"Americas","Europe"},
				/*size*/250, /*dispersion*/1, /*rarity*/2696, /*minAltitude*/220, /*maxAltitude*/255, /*minTemp*/-30f, /*maxTemp*/4f, /*minRain*/150f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.REINDEER_LICHEN, new int[] {0}, new String[] {"ore:blockSoil","ore:blockStone"}, new String[] {"Mountain Range","Mountain Range Edge","High Hills","Mountains"}, new String[]{"Americas","Europe"},
				/*size*/150, /*dispersion*/2, /*rarity*/5296, /*minAltitude*/220, /*maxAltitude*/255, /*minTemp*/-30f, /*maxTemp*/4f, /*minRain*/150f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.REISHI, new int[] {0}, new String[] {"maple","sycamore","pine","spruce","redwood"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/7500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/25f, /*minRain*/750f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RESURRECTION_FERN, new int[] {0}, new String[] {"alltrees","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas"},
				/*size*/8, /*dispersion*/1, /*rarity*/1400, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/27f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RIVER_CANE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[] {"Swamp","Peat Bog","Salt Swamp","Riverbank","Lakeshore","Lake","River"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/2, /*rarity*/2584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/22f, /*minRain*/610f, /*maxRain*/1550f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0.4f);
		athsPlantHelper(AthsGlobal.ROCK_CAP_FERN, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Americas","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/950, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/-10f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ROOTING_SHANK, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ROSEBUSH, new int[] {0,1,2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp","Peat Bog","High Hills","High Hills Edge"}, new String[]{"Africa","Americas","Asia","Europe"},
				/*size*/8, /*dispersion*/3, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/180, /*minTemp*/-5f, /*maxTemp*/22f, /*minRain*/600f, /*maxRain*/900f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.ROUGH_HORSETAIL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Lake","Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp","Estuary"}, new String[]{"Americas", "Asia", "Europe"},
				/*size*/35, /*dispersion*/2, /*rarity*/5512, /*minAltitude*/0, /*maxAltitude*/150, /*minTemp*/-10f, /*maxTemp*/12f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.ROYAL_CATCHFLY, new int[] {0,1}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/3, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/8f, /*maxTemp*/19f, /*minRain*/430f, /*maxRain*/850f, /*minEVT*/0.25f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.ROYAL_FERN + "American", AthsGlobal.ROYAL_FERN, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/4, /*rarity*/4768, /*minAltitude*/0, /*maxAltitude*/180, /*minTemp*/-1f, /*maxTemp*/15f, /*minRain*/890f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.7f);
		athsPlantHelper(AthsGlobal.ROYAL_FERN + "American_Swamp", AthsGlobal.ROYAL_FERN, new int[] {1}, new String[] {"ore:blockSoil"}, new String[]{"Lake","Lakeshore","Riverbank", "River","Swamp","Peat Bog"}, new String[]{"Americas"},
				/*size*/18, /*dispersion*/2, /*rarity*/3968, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-1f, /*maxTemp*/15f, /*minRain*/890f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.7f);
		athsPlantHelper(AthsGlobal.ROYAL_FERN /*Eurasian*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/4, /*rarity*/4768, /*minAltitude*/0, /*maxAltitude*/180, /*minTemp*/-1f, /*maxTemp*/15f, /*minRain*/890f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.7f);
		athsPlantHelper(AthsGlobal.ROYAL_FERN /*Eurasian_Swamp*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Lake","Lakeshore","Riverbank", "River","Swamp","Peat Bog"}, new String[]{"Asia"},
				/*size*/18, /*dispersion*/2, /*rarity*/3968, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-1f, /*maxTemp*/15f, /*minRain*/890f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.7f);
		athsPlantHelper(AthsGlobal.ROYAL_JASMINE, new int[] {0,1,2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/50, /*dispersion*/3, /*rarity*/4028, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/30f, /*minRain*/580f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RUBBER_FIG, new int[] {0,1,2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/4, /*dispersion*/7, /*rarity*/4028, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/31f, /*minRain*/2080f, /*maxRain*/16000f, /*minEVT*/1.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SAGEBRUSH + "_Grassland", AthsGlobal.SAGEBRUSH, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","High Hills Edge","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/30, /*dispersion*/14, /*rarity*/6000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/25f, /*minRain*/200f, /*maxRain*/300f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SAGEBRUSH + "_Riverbank", AthsGlobal.SAGEBRUSH, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"Riverbank"}, new String[]{"Americas"},
				/*size*/20, /*dispersion*/20, /*rarity*/750, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/25f, /*minRain*/25f, /*maxRain*/135f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.SAGEBRUSH + "_Transition", AthsGlobal.SAGEBRUSH, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","High Hills Edge","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/20, /*dispersion*/20, /*rarity*/750, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/25f, /*minRain*/100f, /*maxRain*/135f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SAGEBRUSH, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/8, /*rarity*/450, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/25f, /*minRain*/135f, /*maxRain*/200f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SAGUARO, new int[] {0,1,2,3,4}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"Plains","High Hills Edge","Rolling Hills","High Plains","Foothills"}, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/456, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/17f, /*maxTemp*/31f, /*minRain*/100f, /*maxRain*/200f, /*minEVT*/1f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SALTWORT + "_AfricaDesert", AthsGlobal.SALTWORT, new int[] {0,1}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Africa"},
				/*size*/30, /*dispersion*/9, /*rarity*/4800, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/120f, /*maxRain*/200f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SALTWORT + "_AfricaExtremeDesert", AthsGlobal.SALTWORT, new int[] {0,1}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Africa"},
				/*size*/20, /*dispersion*/12, /*rarity*/6950, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/70f, /*maxRain*/120f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SALTWORT + "_Riverbank", AthsGlobal.SALTWORT, new int[] {0,1}, new String[] {"ore:blockSoil","ore:blockGravel","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Riverbank","Beach","Gravel Beach"}, new String[]{"Americas"},
				/*size*/15, /*dispersion*/10, /*rarity*/1550, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/25f, /*maxRain*/135f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.SALTWORT, new int[] {0,1}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Salt Swamp","Estuary"}, new String[]{"Africa","Asia","Europe","Americas"},
				/*size*/12, /*dispersion*/5, /*rarity*/750, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/100f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.SAPPHIRE_TOWER, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Foothills","Mountains Edge","Mountains","Mountain Range Edge","Mountain Range","High Hills","High Hills Edge"}, new String[]{"Americas"},
					/*size*/13, /*dispersion*/9, /*rarity*/10580, /*minAltitude*/175, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/21f, /*minRain*/200f, /*maxRain*/720f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SAXAUL + "_ExtremeDesert", AthsGlobal.SAXAUL, new int[] {0,1,2,3}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Asia"},
				/*size*/15, /*dispersion*/24, /*rarity*/3900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/60f, /*maxRain*/90f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.SAXAUL + "_Riverbank", AthsGlobal.SAXAUL, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"Riverbank"}, new String[]{"Asia"},
				/*size*/12, /*dispersion*/10, /*rarity*/2050, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/25f, /*maxRain*/135f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.SAXAUL, new int[] {0,1,2,3}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge","Mountains","Mountain Range","Mountain Range Edge"}, new String[]{"Asia"},
				/*size*/25, /*dispersion*/18, /*rarity*/3600, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/110f, /*maxRain*/200f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SAXIFRAGE + "_Purple", AthsGlobal.SAXIFRAGE, new int[] {1}, new String[] {"ore:blockSoil", "ore:blockSand","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/18, /*dispersion*/10, /*rarity*/3280, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/7f, /*minRain*/160f, /*maxRain*/810f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);	
		athsPlantHelper(AthsGlobal.SAXIFRAGE + "_Meadow", AthsGlobal.SAXIFRAGE, new int[] {2}, new String[] {"ore:blockSoil", "ore:blockSand","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Africa"},
				/*size*/18, /*dispersion*/10, /*rarity*/3280, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/7f, /*minRain*/160f, /*maxRain*/810f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);	
		athsPlantHelper(AthsGlobal.SAXIFRAGE + "_Yellow", AthsGlobal.SAXIFRAGE, new int[] {3}, new String[] {"ore:blockSoil", "ore:blockSand","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/18, /*dispersion*/10, /*rarity*/3280, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/7f, /*minRain*/160f, /*maxRain*/810f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);	
		athsPlantHelper(AthsGlobal.SAXIFRAGE /*London's Pride*/, new int[] {0}, new String[] {"ore:blockSoil", "ore:blockSand","ore:blockGravel","ore:stone"}, new String[] {"Mountains","Mountain Range","Mountains Edge","Mountain Range Edge", "Foothills"}, new String[]{"Europe"},
				/*size*/18, /*dispersion*/10, /*rarity*/3280, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/7f, /*minRain*/160f, /*maxRain*/810f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SCALY_SAWGILL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/7096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/15f, /*minRain*/250f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SCALY_TREE_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Asia"},
				/*size*/10, /*dispersion*/30, /*rarity*/8084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/30f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/2f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SCARLET_ELFCUP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/3, /*dispersion*/1, /*rarity*/7796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/35f, /*minRain*/900f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SENSITIVE_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Asia","Americas"},
				/*size*/17, /*dispersion*/5, /*rarity*/6084, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/0f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.SENSITIVE_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Lake","Lakeshore","Riverbank", "River","Swamp","Peat Bog"}, new String[]{"Asia","Americas"},
				/*size*/22, /*dispersion*/2, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/0f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.SESAME, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa", "Asia"},
				/*size*/95, /*dispersion*/6, /*rarity*/4556, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/30f, /*minRain*/250f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.SHAGGY_BRACKET, new int[] {0}, new String[] {"oak","birch","maple","hickory","ash","chestnut","aspen","gingko","sycamore","whiteelm","willow"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/22f, /*minRain*/600f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SHAGGY_MANE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/17f, /*minRain*/600f, /*maxRain*/950f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.SHITAKE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/4, /*dispersion*/2, /*rarity*/4896, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/24f, /*minRain*/800f, /*maxRain*/2000f, /*minEVT*/0.5f, /*maxEVT*/6f);
		athsPlantHelper(AthsGlobal.SHOESTRING_FERN, new int[] {0}, new String[] {"alltrees","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Asia"},
				/*size*/8, /*dispersion*/1, /*rarity*/1100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/40f, /*minRain*/850f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SIBERIAN_SQUILL, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","High Hills Edge","High Plains","Mountains","Mountain Range Edge","Mountain Range","Foothills"}, new String[]{"Asia"},
				/*size*/30, /*dispersion*/1, /*rarity*/6684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/22f, /*minRain*/440f, /*maxRain*/980f, /*minEVT*/0f, /*maxEVT*/3f, /*forestGen*/-0.4f);
		athsPlantHelper(AthsGlobal.SILVER_SQUILL, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/9, /*dispersion*/1, /*rarity*/5984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/21f, /*maxTemp*/30f, /*minRain*/100f, /*maxRain*/450f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-0.5f);
		athsPlantHelper(AthsGlobal.SNAKE_SANSEVERIA + "_Variegated", AthsGlobal.SNAKE_SANSEVERIA, new int[] {1}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Africa","Asia"},
				/*size*/60, /*dispersion*/5, /*rarity*/6980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/170f, /*maxRain*/2440f, /*minEVT*/0f, /*maxEVT*/5f/*forestGen*/-0.2f);
		athsPlantHelper(AthsGlobal.SNAKE_SANSEVERIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Africa","Asia"},
				/*size*/60, /*dispersion*/5, /*rarity*/6980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/170f, /*maxRain*/2440f, /*minEVT*/0f, /*maxEVT*/5f/*forestGen*/-0.2f);
		athsPlantHelper(AthsGlobal.SNOW_FLOWER, new int[] {0}, new String[] {"ore:blockSoil","ore:stone"}, new String[] {"Mountain Range","Mountain Range Edge"}, new String[]{"Americas"},
				/*size*/2, /*dispersion*/1, /*rarity*/9168, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/16f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.SOLOMONS_SEAL + "_Variegated", AthsGlobal.SOLOMONS_SEAL, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/10, /*dispersion*/3, /*rarity*/6684, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-7f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.SOLOMONS_SEAL /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/10, /*dispersion*/3, /*rarity*/4684, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-7f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.SORBUS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Mountains","Mountains Edge","Mountain Range Edge","Mountain Range","High Hills Edge","High Plains","Foothills"}, new String[]{"Africa","Europe","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/16f, /*minRain*/550f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SPANISH_MOSS, new int[] {0}, new String[] {"alltrees"}, new String [] {"Swamp","Peat Bog","Estuary","Salt Swamp", "River","Riverbank","Lake","Lakeshore"}, new String[]{"Americas"},
					/*size*/15, /*dispersion*/1, /*rarity*/1100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/23f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SPIDER_PLANT, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/16, /*dispersion*/4, /*rarity*/4784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/2300f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SPIRAL_ALOE, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[] {"Mountain Range","Foothills","Mountain Range Edge","High Plains","Mountains","High Hills","Mountains Edge"}, new String[]{"Africa"},
				/*size*/4, /*dispersion*/2, /*rarity*/8168, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/26f, /*minRain*/90f, /*maxRain*/210f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.SPLENDID_WAXCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-14f, /*maxTemp*/15f, /*minRain*/980f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/9f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SPLIT_LEAF_MONSTERA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/6, /*dispersion*/5, /*rarity*/5784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/24f, /*maxTemp*/40f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SPLIT_LEAF_MONSTERA_EPIPHYTE, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/5784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/24f, /*maxTemp*/40f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SPOTTED_LANGLOISIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/23f, /*minRain*/132f, /*maxRain*/300f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.SQUIRRELS_FOOT_FERN, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/1000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/28f, /*minRain*/800f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.STAGHORN_FERN, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/3000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/40f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.STARFISH_PLANT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Africa"},
				/*size*/6, /*dispersion*/5, /*rarity*/3980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/20f, /*minRain*/100f, /*maxRain*/240f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.STRAWBERRIES_AND_CREAM_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/8796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/18f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.STRAW_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/32f, /*minRain*/720f, /*maxRain*/1600f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.STRICT_BRANCH_CORAL_FUNGUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/15f, /*minRain*/960f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.STUDDED_PUFFBALL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/5096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/35f, /*minRain*/550f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ST_JOHNS_WORT, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia","Europe"},
				/*size*/9, /*dispersion*/5, /*rarity*/3207, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/18f, /*minRain*/462f, /*maxRain*/1109f, /*minEVT*/0f, /*maxEVT*/1f,/*forestGen*/-0.3f);
		athsPlantHelper(AthsGlobal.SUEDE_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-9f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/2600f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SULFUR_TUFT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/23f, /*minRain*/750f, /*maxRain*/1900f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SULPHUR_SHELF, new int[] {0}, new String[] {"oak","willow","yew","douglasfir","pine","spruce"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/3600, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SUMAC, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/15, /*dispersion*/6, /*rarity*/4668, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/4f, /*maxTemp*/18f, /*minRain*/300f, /*maxRain*/1200f, /*minEVT*/0.25f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.SUNDEW, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Mountains","Mountain Range", "Swamp","Peat Bog","Salt Swamp"}, new String[]{"Americas","Africa"},
				/*size*/3, /*dispersion*/1, /*rarity*/14384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/15f, /*minRain*/400f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.SUNFLOWER, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/28, /*dispersion*/5, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/22f, /*minRain*/400f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.SUN_PITCHER, new int[] {0}, new String[] {"ore:blockSoil","ore:stone"}, new String[]{"Mountains","Mountain Range"}, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/7584, /*minAltitude*/200, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/32f, /*minRain*/2000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.SWAMP_HORSETAIL, new int[] {0}, new String[] {"ore:blockSoil", "terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Estuary","Lakeshore","Riverbank","Swamp","Peat Bog","River"}, new String[]{"Europe","Asia","Americas"},
				/*size*/65, /*dispersion*/2, /*rarity*/2084, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/-4f, /*maxTemp*/23f, /*minRain*/550f, /*maxRain*/4400f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/-0.4f);
		athsPlantHelper(AthsGlobal.SWEET_JOE_PYE_WEED + "_Pink", AthsGlobal.SWEET_JOE_PYE_WEED, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/25, /*dispersion*/6, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/1f, /*maxTemp*/16f, /*minRain*/380f, /*maxRain*/700f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SWEET_JOE_PYE_WEED /*White*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/25, /*dispersion*/6, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/1f, /*maxTemp*/16f, /*minRain*/380f, /*maxRain*/700f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SWORD_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Swamp","Peat Bog","Mountains","Mountains Edge","Mountain Range Edge","Mountain Range"}, new String[]{"Americas"},
				/*size*/25, /*dispersion*/6, /*rarity*/1684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/18f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SWORD_SANSEVERIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Mountains Edge"}, new String[]{"Africa","Asia"},
				/*size*/80, /*dispersion*/5, /*rarity*/6980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/19f, /*maxTemp*/36f, /*minRain*/55f, /*maxRain*/440f, /*minEVT*/0f, /*maxEVT*/5f/*forestGen*/-0.5f);
		athsPlantHelper(AthsGlobal.TASSEL_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/9, /*dispersion*/4, /*rarity*/6969, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.TEDDY_BEAR_CACTUS, new int[] {0,1}, new String[] {"ore:blockSand","ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/28, /*dispersion*/6, /*rarity*/5024, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/24f, /*minRain*/70f, /*maxRain*/245f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.THALE_CRESS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Africa"},
				/*size*/35, /*dispersion*/4, /*rarity*/4556, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/23f, /*minRain*/350f, /*maxRain*/860f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.THISTLE, new int[] {0,1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/7, /*dispersion*/11, /*rarity*/556, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/15f, /*minRain*/350f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.TILLANDSIA_BROMELIAD, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/2000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/30f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.TITAN_ARUM, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Salt Swamp","Estuary"}, new String[]{"Asia"},
				/*size*/2, /*dispersion*/7, /*rarity*/17192, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/28f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.TOWER_OF_JEWELS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[] {"Mountains","Mountain Range"}, new String[]{"Africa"},
				/*size*/13, /*dispersion*/10, /*rarity*/7784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/22f, /*minRain*/85f, /*maxRain*/230f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.TRAVELERS_PALM, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/9, /*dispersion*/12, /*rarity*/4156, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/9f);
		athsPlantHelper(AthsGlobal.TRILLIUM, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/27, /*dispersion*/2, /*rarity*/2556, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/2150f, /*minEVT*/0f, /*maxEVT*/7f);
		athsPlantHelper(AthsGlobal.TRUMPET_PITCHER /*Cane_Brake*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[] {"Swamp","Peat Bog","Salt Swamp","Riverbank","Lakeshore","Lake","River"}, new String[]{"Americas"},
				/*size*/10, /*dispersion*/4, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/20f, /*minRain*/620f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.TRUMPET_PITCHER + "_Green", AthsGlobal.TRUMPET_PITCHER, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[] {"Swamp","Peat Bog","Salt Swamp","Riverbank","Lakeshore","Lake","River"}, new String[]{"Americas"},
				/*size*/10, /*dispersion*/4, /*rarity*/4184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/20f, /*minRain*/620f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.TRUMPET_PITCHER + "_Purple", AthsGlobal.TRUMPET_PITCHER, new int[] {2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[] {"Swamp","Peat Bog","Salt Swamp","Riverbank","Lakeshore","Lake","River"}, new String[]{"Americas"},
				/*size*/7, /*dispersion*/4, /*rarity*/5184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/12f, /*minRain*/620f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.TRUMPET_PITCHER + "_Sweet", AthsGlobal.TRUMPET_PITCHER, new int[] {3}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[] {"Swamp","Peat Bog","Salt Swamp","Riverbank","Lakeshore","Lake","River"}, new String[]{"Americas"},
				/*size*/10, /*dispersion*/4, /*rarity*/4884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/19f, /*minRain*/620f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.TRUMPET_VINE + "_Chinese", AthsGlobal.TRUMPET_VINE, new int[] {1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/3400, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/23f, /*minRain*/650f, /*maxRain*/1200f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.TRUMPET_VINE /*American*/, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/1, /*rarity*/3400, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/21f, /*minRain*/650f, /*maxRain*/1200f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.TURKEY_TAIL, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VALERIAN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/8, /*dispersion*/3, /*rarity*/3956, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/1f, /*maxTemp*/14f, /*minRain*/473f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.VANILLA_ORCHID, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/1, /*rarity*/2000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VENUS_FLYTRAP, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Lakeshore","Riverbank","Swamp","Peat Bog","Salt Swamp"}, new String[]{"Americas"},
				/*size*/2, /*dispersion*/1, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/16f, /*minRain*/700f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.VICTORIA_LILY_PAD, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, new String[] {"Riverbank","Lakeshore"}, new String[]{"Americas"},
				/*size*/30, /*dispersion*/4, /*rarity*/4968, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.VIOLET, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Africa","Europe"},
				/*size*/8, /*dispersion*/2, /*rarity*/1856, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/16f, /*minRain*/550f, /*maxRain*/2050f, /*minEVT*/0f, /*maxEVT*/7f);
		athsPlantHelper(AthsGlobal.VIRGINIA_BLUEBELL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/20, /*dispersion*/1, /*rarity*/4600, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/760f, /*maxRain*/1320f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VIRGINIA_CREEPER, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/1, /*rarity*/1900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-12f, /*maxTemp*/22f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VIRGINIA_CREEPER_TERRESTRIAL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/60, /*dispersion*/1, /*rarity*/5900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-12f, /*maxTemp*/22f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VISCID_VIOLET_CORT + "_Tropical", AthsGlobal.VISCID_VIOLET_CORT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/29f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VISCID_VIOLET_CORT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/14f, /*minRain*/780f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VISCID_VIOLET_CORT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/14f, /*minRain*/780f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VOMITING_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/4196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-11f, /*maxTemp*/12f, /*minRain*/830f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VOODOO_LILY, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Lake","Foothills","Lakeshore","Riverbank","Swamp","Salt Swamp","Estuary"}, new String[]{"Asia"},
				/*size*/3, /*dispersion*/6, /*rarity*/10096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/25f, /*minRain*/1200f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WALKING_FERN + "_Asian", AthsGlobal.WALKING_FERN, new int[] {1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/3650, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/-7f, /*maxTemp*/19f, /*minRain*/750f, /*maxRain*/2500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WALKING_FERN /*American*/, new int[] {0}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/1, /*rarity*/3650, /*minAltitude*/0, /*maxAltitude*/210, /*minTemp*/-7f, /*maxTemp*/19f, /*minRain*/750f, /*maxRain*/2500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WATER_HYACINTH, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary"}, new String[]{"Plains","Lakeshore","River","Riverbank"}, new String[]{"Americas"},
				/*size*/80, /*dispersion*/3, /*rarity*/1084, /*minAltitude*/0, /*maxAltitude*/170, /*minTemp*/18f, /*maxTemp*/35f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.WATER_PLANTAIN + "_River", AthsGlobal.WATER_PLANTAIN, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Riverbank","River","Lakeshore"}, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/9, /*dispersion*/3, /*rarity*/104, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.WATER_PLANTAIN, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/9, /*dispersion*/3, /*rarity*/204, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.WATER_SPANGLES, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas"},
				/*size*/64, /*dispersion*/1, /*rarity*/3268, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.WEEPING_MILK_CAP + "_Asian", AthsGlobal.WEEPING_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/27f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WEEPING_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/8796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/3000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WELWITSCHIA, new int[] {0,1}, new String[] {"ore:blockSand"}, new String[]{"Beach","Gravel Beach","Lake","Lakeshore","Riverbank","Swamp","Salt Swamp","Estuary","Plains","High Plains"}, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/1024, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/65f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.WESTERN_SKUNK_CABBAGE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Lakeshore","Riverbank","Swamp","Peat Bog","River","Mountain Range","Mountain Range Edge","Foothills","Mountais","Mountains Edge"}, new String[]{"Americas"},
				/*size*/43, /*dispersion*/2, /*rarity*/6012, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/10f, /*maxTemp*/19f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WHISK_FERN, new int[] {0}, new String[] {"alltrees","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Africa","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/1050, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WHITE_SKUNK_CABBAGE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Lakeshore","Riverbank","Swamp","Peat Bog","River"}, new String[]{"Asia"},
				/*size*/43, /*dispersion*/2, /*rarity*/7012, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-12f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WINE_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/14f, /*minRain*/730f, /*maxRain*/2400f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WISTERIA, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/35, /*dispersion*/1, /*rarity*/3600, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/1200f, /*minEVT*/1.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WISTERIA_TREE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/18, /*dispersion*/12, /*rarity*/9600, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/1200f, /*minEVT*/1.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOD_BITTER_VETCH, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/34, /*dispersion*/3, /*rarity*/4984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/20f, /*minRain*/450f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.WOOD_BLEWIT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5896, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/2800f, /*minEVT*/0f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOD_EAR + "_Oak", AthsGlobal.WOOD_EAR, new int[] {0}, new String[] {"oak"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/6500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOD_EAR, new int[] {0}, new String[] {"maple","ash","whiteelm","hickory","chestnut"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/9500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/10000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOD_FERN + "_Tropical", AthsGlobal.WOOD_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/20, /*dispersion*/8, /*rarity*/7068, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/24f, /*maxTemp*/40f, /*minRain*/950f, /*maxRain*/10000f, /*minEVT*/0.5f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOD_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/20, /*dispersion*/8, /*rarity*/4068, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-5f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/6000f, /*minEVT*/0.5f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOD_LILY + "_Red", AthsGlobal.WOOD_LILY, new int[] {1}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/6, /*dispersion*/3, /*rarity*/4884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/510f, /*maxRain*/1230f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.WOOD_LILY + "_Yellow", AthsGlobal.WOOD_LILY, new int[] {2}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/6, /*dispersion*/3, /*rarity*/9884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/510f, /*maxRain*/1230f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.WOOD_LILY /*Orange*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/6, /*dispersion*/3, /*rarity*/3884, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/510f, /*maxRain*/1230f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.WOOD_POPPY + "_Calcareous", AthsGlobal.WOOD_POPPY, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/16, /*dispersion*/4, /*rarity*/2784, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/3f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/2110f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.WOOD_POPPY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/16, /*dispersion*/4, /*rarity*/8384, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/3f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/2110f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.WOOD_SORREL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/35, /*dispersion*/1, /*rarity*/4900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/2300f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOLY_CHANTERELLE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-9f, /*maxTemp*/14f, /*minRain*/850f, /*maxRain*/6800f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOLY_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-18f, /*maxTemp*/12f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOOLY_TREE_FERN, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/10, /*dispersion*/10, /*rarity*/4684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/23f, /*minRain*/1130f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YARROW, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Americas"},
				/*size*/17, /*dispersion*/5, /*rarity*/6384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/12f, /*minRain*/450f, /*maxRain*/700f, /*minEVT*/0.5f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.YELLOW_JEWELWEED, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/13, /*dispersion*/3, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/1600f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YELLOW_PARASOL_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/930f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YUCCA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/20, /*dispersion*/12, /*rarity*/3284, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/38f, /*minRain*/105f, /*maxRain*/680f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.ZZ_PLANT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/12, /*dispersion*/4, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/370f, /*maxRain*/910f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsTreeHelper(AthsGlobal.DWARF_BAMBOO, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.BAMBOO);
		athsTreeHelper(AthsGlobal.YOUNG_ACACIA + "_Koa", AthsGlobal.YOUNG_ACACIA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.KOA);
		athsTreeHelper(AthsGlobal.YOUNG_ACACIA /*Utacacia*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.UTACACIA);
		athsTreeHelper(AthsGlobal.YOUNG_ASH, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.ASH);
		athsTreeHelper(AthsGlobal.YOUNG_ASPEN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.ASPEN);
		athsTreeHelper(AthsGlobal.YOUNG_BAOBAB, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.BAOBAB);
		athsTreeHelper(AthsGlobal.YOUNG_BIRCH, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.BIRCH);
		athsTreeHelper(AthsGlobal.YOUNG_CHESTNUT, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.CHESTNUT);
		athsTreeHelper(AthsGlobal.YOUNG_DOUGLAS_FIR, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.DOUGLASFIR);
		athsTreeHelper(AthsGlobal.YOUNG_EBONY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.EBONY);
		athsTreeHelper(AthsGlobal.YOUNG_FEVER, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.FEVERTREE);
		athsTreeHelper(AthsGlobal.YOUNG_GHAF, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.GHAF);
		athsTreeHelper(AthsGlobal.YOUNG_GINGKO, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.GINGKO);
		athsTreeHelper(AthsGlobal.YOUNG_HICKORY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.HICKORY);
		athsTreeHelper(AthsGlobal.YOUNG_KAPOK, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.KAPOK);
		athsTreeHelper(AthsGlobal.YOUNG_LAUREL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/1, /*dispersion*/5, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.LAUREL);
		athsTreeHelper(AthsGlobal.YOUNG_LIMBA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.LIMBA);
		athsTreeHelper(AthsGlobal.YOUNG_MAHOE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MAHOE);
		athsTreeHelper(AthsGlobal.YOUNG_MAHOGANY, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MAHOGANY);
		athsTreeHelper(AthsGlobal.YOUNG_MANGROVE, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel","terrafirmacraftplus:SaltWaterStationary"}, new String []{"Salt Swamp"}, new String[]{"Europe", "Asia","Africa","Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MANGROVE);
		athsTreeHelper(AthsGlobal.YOUNG_MAPLE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MAPLE);
		athsTreeHelper(AthsGlobal.YOUNG_OAK, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia" ,"Africa", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.OAK);
		athsTreeHelper(AthsGlobal.YOUNG_PALM, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia","Africa","Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.PALM);
		athsTreeHelper(AthsGlobal.YOUNG_PINE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.PINE);
		athsTreeHelper(AthsGlobal.YOUNG_SEQUOIA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.REDWOOD);
		athsTreeHelper(AthsGlobal.YOUNG_SPRUCE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.SPRUCE);
		athsTreeHelper(AthsGlobal.YOUNG_SYCAMORE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.SYCAMORE);
		athsTreeHelper(AthsGlobal.YOUNG_TEAK, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.TEAK);
		athsTreeHelper(AthsGlobal.YOUNG_WHITE_CEDAR, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.WHITECEDAR);
		athsTreeHelper(AthsGlobal.YOUNG_WHITE_ELM, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.WHITEELM);
		athsTreeHelper(AthsGlobal.YOUNG_WILLOW, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Europe", "Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.WILLOW);
		athsTreeHelper(AthsGlobal.YOUNG_YEW + "_Africa", AthsGlobal.YOUNG_YEW, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.AFRICANYEW);
		athsTreeHelper(AthsGlobal.YOUNG_YEW, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.YEW);

		athsPlantHelper(AthsGlobal.GAZANIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/15, /*dispersion*/10, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/18f, /*minRain*/190f, /*maxRain*/900f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.GAZANIA + "_Alpine", AthsGlobal.GAZANIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Foothills","Mountains Edge","Mountains","Mountain Range Edge","Mountain Range"}, new String[]{"Africa"},
				/*size*/15, /*dispersion*/10, /*rarity*/6584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/14f, /*minRain*/190f, /*maxRain*/900f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.GAZANIA + "_Cultivars", AthsGlobal.GAZANIA, new int[] {1,2,3,4}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/15, /*dispersion*/10, /*rarity*/6584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/18f, /*minRain*/190f, /*maxRain*/900f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.GAZANIA + "_Alpine_Cultivars", AthsGlobal.GAZANIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Foothills","Mountains Edge","Mountains","Mountain Range Edge","Mountain Range"}, new String[]{"Africa"},
				/*size*/15, /*dispersion*/10, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/14f, /*minRain*/190f, /*maxRain*/900f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.NEMESIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Estuary","Plains","Rolling Hills","Beach"}, new String[]{"Africa"},
				/*size*/10, /*dispersion*/4, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/19f, /*minRain*/250f, /*maxRain*/1800f, /*minEVT*/0.75f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.NEMESIA + "_Cultivars", AthsGlobal.NEMESIA, new int[] {1,2,3,4,5,6,7}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Estuary","Plains","Rolling Hills","Beach"}, new String[]{"Africa"},
				/*size*/10, /*dispersion*/4, /*rarity*/7584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/19f, /*minRain*/250f, /*maxRain*/1800f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.MOREA_IRIS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/6, /*dispersion*/3, /*rarity*/5184, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/22f, /*minRain*/320f, /*maxRain*/1200f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.CLIVIA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Mountains","Mountains Edge","Mountain Range","Mountain Range Edge","High Hills"}, new String[]{"Africa"},
				/*size*/4, /*dispersion*/3, /*rarity*/6684, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/18f, /*minRain*/620f, /*maxRain*/2200f, /*minEVT*/0.5f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.FERNY_ASPARAGUS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/12, /*dispersion*/3, /*rarity*/3384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/27f, /*minRain*/640f, /*maxRain*/3000f, /*minEVT*/0.25f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BLACK_WITCH_HAZEL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/22, /*dispersion*/6, /*rarity*/2784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/3200f, /*minEVT*/0.25f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LEATHERLEAF_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas"},
				/*size*/10, /*dispersion*/6, /*rarity*/3784, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/28f, /*minRain*/850f, /*maxRain*/16000f, /*minEVT*/0.0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BASKET_GRASS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Asia","Europe"},
				/*size*/50, /*dispersion*/1, /*rarity*/2284, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/550f, /*maxRain*/16000f, /*minEVT*/0.0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.CAPE_WILD_BANANA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/12, /*dispersion*/12, /*rarity*/5784, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/16f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.BISMARCKIA_PALM, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/9, /*dispersion*/12, /*rarity*/4156, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/24f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/9f);
		athsPlantHelper(AthsGlobal.WILDEGRANAAT, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/6284, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/20f, /*minRain*/450f, /*maxRain*/3200f, /*minEVT*/0.25f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.ROCKWOOD, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/20, /*dispersion*/15, /*rarity*/2084, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/18f, /*minRain*/150f, /*maxRain*/300f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.CAPE_GRAPE, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/20, /*dispersion*/1, /*rarity*/1984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/11f, /*maxTemp*/21f, /*minRain*/310f, /*maxRain*/1200f, /*minEVT*/0.25f, /*maxEVT*/4f);		
		

		athsPlantHelper(AthsGlobal.OAK_LOVING_GYMNOPUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/5796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COMMON_BROWN_CUP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/4, /*dispersion*/4, /*rarity*/5396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PLATTERFULL_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/12f, /*minRain*/790f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.JELLY_BABY_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/8796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-7f, /*maxTemp*/13f, /*minRain*/890f, /*maxRain*/1600f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		

		athsPlantHelper(AthsGlobal.HAZEL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/12, /*dispersion*/10, /*rarity*/5380, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/15f, /*minRain*/700f, /*maxRain*/1850f, /*minEVT*/0f, /*maxEVT*/0.5f);
		athsPlantHelper(AthsGlobal.WITCH_HAZEL, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas"},
				/*size*/12, /*dispersion*/10, /*rarity*/4980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/17f, /*minRain*/780f, /*maxRain*/1250f, /*minEVT*/0.25f, /*maxEVT*/2f);
		
		athsPlantHelper(AthsGlobal.YOUNG_NOOTKA_CYPRESS, new int[] {0}, new String[] {"ore:blockSoil","ore:stone","ore:blockGravel"}, new String[] {"Mountain Range","Mountain Range Edge","Foothills","Mountains",}, new String[]{"Europe"},
				/*size*/5, /*dispersion*/10, /*rarity*/8700, /*minAltitude*/180, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/7f, /*minRain*/2050f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/4f,/*forestGen*/1.0f);	
		athsPlantHelper(AthsGlobal.YOUNG_HINOKI_CYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"Riverbank","Lakeshore","Mountains","Mountain Range","Mountain Range Edge","Mountains Edge","High_Hills","High Hills Edge","Foothills"}, new String[]{"Asia"},
				/*size*/12, /*dispersion*/10, /*rarity*/8980, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/12f, /*minRain*/980f, /*maxRain*/2050f, /*minEVT*/0.5f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.YOUNG_SAWARA_FALSECYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"Swamp","Peat Bog","Riverbank","Lakeshore","Mountains","Mountain Range","Mountain Range Edge","Mountains Edge"}, new String[]{"Asia"},
				/*size*/12, /*dispersion*/10, /*rarity*/7980, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/17f, /*minRain*/780f, /*maxRain*/1850f, /*minEVT*/0.25f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.YOUNG_SAWARA_LEMON_THREAD_FALSECYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"Swamp","Peat Bog","Riverbank","Lakeshore","Mountains","Mountain Range","Mountain Range Edge","Mountains Edge"}, new String[]{"Asia"},
				/*size*/8, /*dispersion*/10, /*rarity*/10980, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/17f, /*minRain*/780f, /*maxRain*/1850f, /*minEVT*/0.25f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.YOUNG_SAWARA_BABY_BLUE_FALSECYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"Swamp","Peat Bog","Riverbank","Lakeshore","Mountains","Mountain Range","Mountain Range Edge","Mountains Edge"}, new String[]{"Asia"},
				/*size*/8, /*dispersion*/10, /*rarity*/10980, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/17f, /*minRain*/780f, /*maxRain*/1850f, /*minEVT*/0.25f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.YOUNG_MEDITERRANEAN_CYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/15, /*dispersion*/10, /*rarity*/8980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/19f, /*minRain*/200f, /*maxRain*/1150f, /*minEVT*/0.0f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.YOUNG_MEDITERRANEAN_CYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/15, /*dispersion*/10, /*rarity*/8980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/19f, /*minRain*/200f, /*maxRain*/1150f, /*minEVT*/0.0f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.YOUNG_MEDITERRANEAN_CYPRESS + "Iran", AthsGlobal.YOUNG_MEDITERRANEAN_CYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/15, /*dispersion*/10, /*rarity*/9980, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/9f, /*maxTemp*/19f, /*minRain*/160f, /*maxRain*/650f, /*minEVT*/0.5f, /*maxEVT*/2f);
		athsPlantHelper(AthsGlobal.YOUNG_MEDITERRANEAN_CYPRESS + "Laurel_Forest", AthsGlobal.YOUNG_MEDITERRANEAN_CYPRESS, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/7, /*dispersion*/10, /*rarity*/6969, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/38f, /*minRain*/1750f, /*maxRain*/2500f, /*minEVT*/1.0f, /*maxEVT*/4f);
		
		athsPlantHelper(AthsGlobal.BAY_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/5596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/14f, /*minRain*/850f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BICOLOR_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/1f, /*maxTemp*/18f, /*minRain*/670f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLACK_TRUMPET, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/9, /*dispersion*/6, /*rarity*/7796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/16f, /*minRain*/970f, /*maxRain*/1500f, /*minEVT*/1f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLUING_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/21f, /*minRain*/540f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLUSHER + "Eurasian", AthsGlobal.BLUSHER, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe","Africa"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/24f, /*minRain*/750f, /*maxRain*/2300f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLUSHER, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/24f, /*minRain*/750f, /*maxRain*/2300f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CAESAR_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/4996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/26f, /*minRain*/690f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CAESAR_MUSHROOM + "_Eurasian", AthsGlobal.CAESAR_MUSHROOM, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/4996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/26f, /*minRain*/690f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CAULIFLOWER_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/17f, /*minRain*/790f, /*maxRain*/3400f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DEER_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/3496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/20f, /*minRain*/750f, /*maxRain*/4400f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.EARTHY_INOCYBE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass","ore:blockGravel"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/4, /*rarity*/5096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-12f, /*maxTemp*/9f, /*minRain*/420f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FAIRY_FINGERS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe","Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/4196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/17f, /*minRain*/540f, /*maxRain*/3000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FALSE_MOREL, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"},  new String[] {"Mountains","High Hills","Mountain Rage","Mountain Range Edge","Foothills","Mountains Edge","High Hills Edge"}, new String[]{"Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/22f, /*minRain*/790f, /*maxRain*/1600f, /*minEVT*/1f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GREEN_QUILTED_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe","Asia"},
				/*size*/8, /*dispersion*/5, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/20f, /*minRain*/750f, /*maxRain*/1600f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VARIEGATED_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe","Asia"},
				/*size*/8, /*dispersion*/5, /*rarity*/8396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/18f, /*minRain*/850f, /*maxRain*/1900f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YELLOW_SWAMP_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe","Asia"},
				/*size*/8, /*dispersion*/5, /*rarity*/8096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/17f, /*minRain*/550f, /*maxRain*/1600f, /*minEVT*/0.25f, /*maxEVT*/2f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SHORT_STEMMED_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/8, /*dispersion*/5, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/18f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SHRIMP_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/8, /*dispersion*/5, /*rarity*/8596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/29f, /*minRain*/750f, /*maxRain*/1600f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CAMEMBERT_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/8, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/22f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PURPLE_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/8, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/11f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FRAGILE_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/6, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/10f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLACKENING_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/8, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/9f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GILDED_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/8, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MILK_WHITE_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/8, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/18f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ROSY_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/8, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/11f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BIRCH_RUSSULA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/8, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/8f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LOBSTER_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/10, /*dispersion*/5, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/29f, /*minRain*/550f, /*maxRain*/1900f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HEDGEHOG_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/1, /*rarity*/6696, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/14f, /*minRain*/760f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LIBERTY_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/6696, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/17f, /*minRain*/510f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PARASOL_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe"},
				/*size*/4, /*dispersion*/5, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/450f, /*maxRain*/870f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.THE_VOMITER, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe","Asia"},
				/*size*/4, /*dispersion*/5, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/26f, /*minRain*/450f, /*maxRain*/870f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SHRIMP_OF_THE_WOODS, new int[] {0,1}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/5796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIPPERY_JACK, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Africa","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5396, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/-6f, /*maxTemp*/40f, /*minRain*/770f, /*maxRain*/2800f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YELLOW_KNIGHT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/3, /*dispersion*/4, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/-10f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WINTER_CHANTERELLE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/8, /*dispersion*/7, /*rarity*/7396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/16f, /*minRain*/970f, /*maxRain*/1500f, /*minEVT*/1f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WALNUT_MYCENA, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/6969, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/15f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/2f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RED_CRACKING_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/3996, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/0f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/2800f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PEAR_SHAPED_PUFFBALL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/5096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/35f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		
		athsPlantHelper(AthsGlobal.HORSE_HOOF_FUNGUS, new int[] {0}, new String[] {"maple","aspen","sycamore","hickory","willow","birch","oak"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/2000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/18f, /*minRain*/500f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MAITAKE, new int[] {0}, new String[] {"oak","maple"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/1900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.NORTHERN_TOOTH_FUNGUS, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/3200, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/-6f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RESINOUS_POLYPORE, new int[] {0}, new String[] {"oak","willow","maple","aspen","ash","sycamore","hickory","birch","whiteelm","chestnut"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/3600, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/-3f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.VIOLET_TOOTH_FUNGUS, new int[] {0}, new String[] {"oak","willow","maple","aspen","ash","sycamore","hickory","birch","whiteelm","chestnut"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GREGS_MOUSTACHE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass","ore:blockSand","ore:blockGravel"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/50000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/0f, /*minRain*/450f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.BITTER_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/4496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/20f, /*minRain*/740f, /*maxRain*/3500f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BRICK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/3, /*dispersion*/1, /*rarity*/3896, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/17f, /*minRain*/590f, /*maxRain*/2000f, /*minEVT*/0.25f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SUGARBUSH, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/10, /*dispersion*/4, /*rarity*/2584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/18f, /*minRain*/150f, /*maxRain*/300f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.SILVERTREE, new int[] {0,1}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/4, /*dispersion*/6, /*rarity*/3584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/18f, /*minRain*/150f, /*maxRain*/300f, /*minEVT*/1f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.FRAILEJONE, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[]{"Foothills","Mountains Edge","Mountains","Mountain Range Edge","Mountain Range"}, new String[]{"Americas"},
				/*size*/50, /*dispersion*/3, /*rarity*/3880, /*minAltitude*/210, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/27f, /*minRain*/200f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.NORTHERN_BEECH_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas"},
				/*size*/32, /*dispersion*/2, /*rarity*/7984, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/14f, /*minRain*/680f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.WOODSIA, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/10, /*dispersion*/8, /*rarity*/6068, /*minAltitude*/144, /*maxAltitude*/160, /*minTemp*/-10f, /*maxTemp*/12f, /*minRain*/750f, /*maxRain*/6000f, /*minEVT*/0.5f, /*maxEVT*/6f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YELLOW_LOTUS, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Ice"}, AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas"},
				/*size*/30, /*dispersion*/6, /*rarity*/3264, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/-2f, /*maxTemp*/22f, /*minRain*/450f, /*maxRain*/6000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.WATER_LETTUCE, new int[] {0}, new String[] {"terrafirmacraftplus:FreshWaterStationary"}, new String[]{"Plains","Lakeshore","River","Riverbank"}, new String[]{"Americas","Africa","Asia"},
				/*size*/80, /*dispersion*/2, /*rarity*/2784, /*minAltitude*/0, /*maxAltitude*/170, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.ARCTIC_POPPY, new int[] {0}, new String[] {"ore:blockSoil", "ore:blockSand","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas","Africa"},
				/*size*/10, /*dispersion*/8, /*rarity*/1280, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-25f, /*maxTemp*/-3f, /*minRain*/160f, /*maxRain*/810f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/0.0f);	
		athsPlantHelper(AthsGlobal.ARCTIC_WILLOW, new int[] {0}, new String[] {"ore:blockSoil", "ore:blockSand","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas"},
				/*size*/5, /*dispersion*/5, /*rarity*/3580, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/0f, /*minRain*/160f, /*maxRain*/810f, /*minEVT*/0.5f, /*maxEVT*/10f,/*forestGen*/0.0f);	
		athsPlantHelper(AthsGlobal.TERMITE_MOUND, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, new String[] {"All","!Ocean","!Hell","!Deep Ocean", "!River","!Beach","!Gravel Beach"}, new String[]{"Africa","Americas"},
				/*size*/8, /*dispersion*/12, /*rarity*/7584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/6f,/*forestGen*/0.0f);
		athsPlantHelper(AthsGlobal.ALGAE_MAT_SARGASSUM, new int[] {0}, new String[] {"terrafirmacraftplus:SaltWaterStationary"}, new String[] {"Ocean"}, new String[]{"Americas","Africa","Asia","Europe"},
				/*size*/70, /*dispersion*/1, /*rarity*/10264, /*minAltitude*/140, /*maxAltitude*/160, /*minTemp*/13f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.SARGASSUM_CLUMP, new int[] {0}, new String[] {"ore:blockSoil", "ore:blockSand","ore:blockGravel","ore:stone"}, new String[] {"Beach","Gravel Beach"}, new String[]{"Americas","Africa","Asia","Europe"},
				/*size*/40, /*dispersion*/1, /*rarity*/8780, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.0f);	
		athsPlantHelper(AthsGlobal.SPIKEMOSS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Africa","Europe"},
				/*size*/29, /*dispersion*/8, /*rarity*/4584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/810f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.HORNWORT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/15, /*dispersion*/1, /*rarity*/6296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.QUILLWORT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:PeatGrass"},  AthsGlobal.SHALLOW_WATER_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/15, /*dispersion*/1, /*rarity*/9296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/40f, /*minRain*/500f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MONKEYFLOWER, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  new String[] {"Riverbank","Mountains","Mountain Range","Estuary","Lakeshore"}, new String[]{"Americas"},
				/*size*/23, /*dispersion*/2, /*rarity*/3296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/25f, /*minRain*/150f, /*maxRain*/350f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.3f);
		athsPlantHelper(AthsGlobal.RED_GYROPORUS, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/8796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/640f, /*maxRain*/1100f, /*minEVT*/0.5f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CHESTNUT_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/17f, /*minRain*/540f, /*maxRain*/1100f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ADMIRABLE_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/10f, /*minRain*/1000f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LILAC_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[]{"High Hills","Plains","High Hills Edge","Rolling Hills","High Plains","Foothills","Lakeshore","Riverbank","Swamp","Peat Bog"}, new String[]{"Americas", "Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/6969, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/15f, /*minRain*/700f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LILAC_BROWN_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLACK_VELVET_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BUTTER_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/1f, /*maxTemp*/21f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CANDY_APPLE_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/28f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CHICKEN_FAT_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/5996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DOTTED_STALK_SUILLUS, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/5396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PAINTED_SUILLUS, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIPPERY_JILL, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Africa"},
				/*size*/2, /*dispersion*/2, /*rarity*/8996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/26f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GHOST_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  new String[] {"Swamp","Peat Bog","Salt_Swamp","Riverbank","Lakeshore"}, new String[]{"Americas","Europe","Asia"},
				/*size*/3, /*dispersion*/5, /*rarity*/8096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/1f, /*maxTemp*/14f, /*minRain*/550f, /*maxRain*/1600f, /*minEVT*/0.25f, /*maxEVT*/2f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GILLED_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Africa","Europe"},
				/*size*/4, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RUSSELS_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-7f, /*maxTemp*/10f, /*minRain*/750f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SHAGGY_STALKED_BOLETE, new int[] {0}, new String[] {"ore:blockSoil"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/7596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/18f, /*minRain*/850f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BOULLION_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/4f, /*maxTemp*/19f, /*minRain*/670f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CHROME_FOOTED_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/5999, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/-5f, /*maxTemp*/22f, /*minRain*/670f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CORRUGATED_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/26f, /*minRain*/920f, /*maxRain*/2000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SAFFRON_MILK_CAP /*European*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/21f, /*minRain*/800f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SAFFRON_MILK_CAP + "_American", AthsGlobal.SAFFRON_MILK_CAP, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/21f, /*minRain*/800f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PEPPERY_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/22f, /*minRain*/690f, /*maxRain*/3000f, /*minEVT*/0.25f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LILAC_MILK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/8296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/16f, /*minRain*/790f, /*maxRain*/3200f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CANDY_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/9296, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/17f, /*minRain*/1000f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LEPIDELLA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/28f, /*minRain*/750f, /*maxRain*/4300f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GUNPOWDER_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/25f, /*minRain*/550f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.POISON_CHAMPAGNE_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/16f, /*minRain*/950f, /*maxRain*/1200f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SNAKESKIN_GRISETTE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/9196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/20f, /*minRain*/420f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SMITHS_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/18f, /*minRain*/1800f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.EAST_ASIAN_DEATH_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/17f, /*maxTemp*/32f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CITRON_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GUNPOWDER_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/25f, /*minRain*/550f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ROYAL_FLY_AGARIC, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-12f, /*maxTemp*/6f, /*minRain*/950f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WARTED_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/12f, /*minRain*/850f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FRANCHETS_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Africa","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/24f, /*minRain*/650f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SAFFRON_RINGLESS_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/1200f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.TAWNY_GRISETTE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-7f, /*maxTemp*/15f, /*minRain*/650f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GRISETTE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/165, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/22f, /*minRain*/550f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GEMMED_AMANITA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/14f, /*maxTemp*/36f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COCCORA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/19f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/4f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CHEPANG_SLENDER_CAESAR, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/30f, /*minRain*/950f, /*maxRain*/7000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ZAMBIAN_SLENDER_CAESAR, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/850f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YELLOW_PATCHES, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/5196, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/8f, /*minRain*/950f, /*maxRain*/1800f, /*minEVT*/0.25f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FLY_AGARIC + "_Yellow_American", AthsGlobal.FLY_AGARIC, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/2, /*rarity*/4696, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/20f, /*minRain*/650f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FLY_AGARIC, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/3, /*dispersion*/2, /*rarity*/4096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/25f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SHEATHED_WOODTUFT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/20f, /*minRain*/900f, /*maxRain*/6000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WRINKLED_PSATHYRELLA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/7996, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/850f, /*maxRain*/2000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FRIED_CHICKEN_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/7496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/25f, /*minRain*/700f, /*maxRain*/6000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WRINKLED_PEACH, new int[] {0}, new String[] {"whiteelm","ash","maple","ebony","acacia"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/10000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/20f, /*minRain*/600f, /*maxRain*/2000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GRAY_KNIGHT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/3, /*dispersion*/4, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/-6f, /*maxTemp*/12f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MATSUTAKE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/3, /*dispersion*/4, /*rarity*/8796, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/2f, /*maxTemp*/17f, /*minRain*/1050f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/3f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MEADOW_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe","Asia"},
				/*size*/4, /*dispersion*/5, /*rarity*/5396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/450f, /*maxRain*/870f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.COMMON_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/4, /*dispersion*/5, /*rarity*/4596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/550f, /*maxRain*/1270f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		
		athsPlantHelper(AthsGlobal.CYAN_STAINING_PLUTEUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/7496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/25f, /*minRain*/750f, /*maxRain*/4400f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.LION_SHIELD, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/7496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/22f, /*minRain*/600f, /*maxRain*/4400f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		
		athsPlantHelper(AthsGlobal.SCALY_INK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/3, /*dispersion*/1, /*rarity*/6496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MAGPIE_INK_CAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/3, /*dispersion*/1, /*rarity*/6496, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
	
		
		athsPlantHelper(AthsGlobal.VIOLET_WEBCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/19f, /*minRain*/1080f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GOLIATH_WEBCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/16f, /*minRain*/780f, /*maxRain*/1400f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLOOD_RED_WEBCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[]{"High Hills","Mountains","High Hills Edge","Mountains Edge","Mountain Range","Foothills","Mountain Range Edge"}, new String[]{"Americas","Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/8f, /*minRain*/590f, /*maxRain*/3000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DEADLY_WEBCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[]{"High Hills","Mountains","High Hills Edge","Mountains Edge","Mountain Range","Foothills","Mountain Range Edge"}, new String[]{"Americas","Europe","Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/12f, /*minRain*/690f, /*maxRain*/16000f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FOOLS_WEBCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2400f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SPONGEBOB_MUSHROOM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"}, new String[]{"High Hills","Mountains","High Hills Edge","Mountains Edge","Mountain Range","Foothills","Mountain Range Edge"}, new String[]{"Asia"},
				/*size*/2, /*dispersion*/2, /*rarity*/10796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/25f, /*maxTemp*/40f, /*minRain*/2080f, /*maxRain*/16000f, /*minEVT*/0.5f, /*maxEVT*/7f,/*forestGen*/1.0f);
		
		athsPlantHelper(AthsGlobal.RED_BELTED_CONK, new int[] {0}, new String[] {"douglasfir","aspen","pine","spruce","yew","whitecedar","gingko"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/3000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/19f, /*minRain*/900f, /*maxRain*/7000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.RED_BELTED_CONK + "_American", AthsGlobal.RED_BELTED_CONK, new int[] {1}, new String[] {"douglasfir","aspen","pine","spruce","yew","whitecedar","gingko"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/3000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/19f, /*minRain*/900f, /*maxRain*/7000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		
		athsPlantHelper(AthsGlobal.FALSE_SOLOMONS_SEAL /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/10, /*dispersion*/3, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-11f, /*maxTemp*/20f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.BLUEBEAD_LILY /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Mountains","High Hills Edge","Mountains Edge","High Plains","Rolling Hills"}, new String[]{"Americas"},
				/*size*/20, /*dispersion*/0, /*rarity*/6684, /*minAltitude*/155, /*maxAltitude*/200, /*minTemp*/-11f, /*maxTemp*/10f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.DUTCHMANS_BREECHES /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/10, /*dispersion*/3, /*rarity*/6584, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/-2f, /*maxTemp*/14f, /*minRain*/900f, /*maxRain*/2200f, /*minEVT*/0.25f, /*maxEVT*/5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.FAWN_LILY /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/6584, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/1f, /*maxTemp*/21f, /*minRain*/390f, /*maxRain*/2500f, /*minEVT*/0f, /*maxEVT*/1f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.VIBIRNUM /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/7, /*dispersion*/3, /*rarity*/7584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.VIBIRNUM + "_Tropical_Mountain", AthsGlobal.VIBIRNUM /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Mountains","High Hills Edge","Mountains Edge","Mountain Range","Foothills","Mountain Range Edge"}, new String[]{"Americas","Asia"},
				/*size*/7, /*dispersion*/3, /*rarity*/8584, /*minAltitude*/170, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/35f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.VIBIRNUM + "_Africa", AthsGlobal.VIBIRNUM /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Mountains","High Hills Edge","Mountains Edge","Mountain Range","Foothills","Mountain Range Edge"}, new String[]{"Africa"},
				/*size*/7, /*dispersion*/3, /*rarity*/8584, /*minAltitude*/170, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/14f, /*minRain*/550f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.PAWPAW /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/8, /*dispersion*/5, /*rarity*/6084, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/10f, /*maxTemp*/16f, /*minRain*/800f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/0.5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.PINCUSHION_PLANT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/28, /*dispersion*/2, /*rarity*/4368, /*minAltitude*/144, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/0f, /*minRain*/200f, /*maxRain*/760f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);	
		athsPlantHelper(AthsGlobal.PINCUSHION_PLANT + "_Mountain",AthsGlobal.PINCUSHION_PLANT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel","ore:stone"}, new String[] {"Mountains","Mountains Edge","Mountain Range Edge","Mountain Range","Foothills"}, new String[]{"Americas","Asia","Europe"},
				/*size*/24, /*dispersion*/2, /*rarity*/5068, /*minAltitude*/160, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/8f, /*minRain*/250f, /*maxRain*/760f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);	
		
		athsPlantHelper(AthsGlobal.SHRUBBY_CINQUEFOIL, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel","ore:stone"}, new String[] {"Mountains","Mountains Edge","Mountain Range Edge","Mountain Range","Foothills","High Plains"}, new String[]{"Americas","Asia","Europe"},
				/*size*/8, /*dispersion*/6, /*rarity*/6068, /*minAltitude*/150, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/8f, /*minRain*/290f, /*maxRain*/960f, /*minEVT*/0.25f, /*maxEVT*/6f,/*forestGen*/0f);	
		
		athsPlantHelper(AthsGlobal.HONEYSUCKLE /*Normal*/, new int[] {0,1,2,3}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/12, /*dispersion*/3, /*rarity*/5584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/23f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.STRINGY_STONECROP /*Normal*/, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/16, /*dispersion*/1, /*rarity*/8584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/13f, /*maxTemp*/32f, /*minRain*/480f, /*maxRain*/3000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.YOUNG_BLACK_POPLAR /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat_Grass"},  new String[] {"Plains","Swamp","Rolling Hills","Riverbank","Lakeshore","High Plains"}, new String[]{"Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/5969, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/4f, /*maxTemp*/20f, /*minRain*/500f, /*maxRain*/2100f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.YOUNG_BLACK_POPLAR + "_Lombardy", AthsGlobal.YOUNG_BLACK_POPLAR /*Normal*/, new int[] {1}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat_Grass"},  new String[] {"Plains","Swamp","Rolling Hills","Riverbank","Lakeshore","High Plains"}, new String[]{"Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/6969, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/10f, /*maxTemp*/18f, /*minRain*/500f, /*maxRain*/960f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.YOUNG_BLACK_POPLAR + "_Afghan", AthsGlobal.YOUNG_BLACK_POPLAR /*Normal*/, new int[] {2}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat_Grass"},  new String[] {"Foothills","Mountain Range Edge","Mountains Edge","Mountain_Range","Mountains","Riverbank","Lakeshore","High Plains"}, new String[]{"Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/8969, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/14f, /*maxTemp*/19f, /*minRain*/300f, /*maxRain*/660f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.MAPLE_SEEDLING /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Africa","Europe"},
				/*size*/90, /*dispersion*/3, /*rarity*/5284, /*minAltitude*/155, /*maxAltitude*/200, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/950f, /*maxRain*/16000, /*minEVT*/0.25f, /*maxEVT*/4f,/*forestGen*/1f);
		
		athsPlantHelper(AthsGlobal.AMERICAN_VETCH, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/34, /*dispersion*/3, /*rarity*/5384, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/19f, /*minRain*/450f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/4f);
		
		athsPlantHelper(AthsGlobal.SCALYCAP, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/4600, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/35f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		
		athsPlantHelper(AthsGlobal.YOUNG_KATSURA /*Normal*/, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat_Grass"},  new String[] {"Plains","Mountains","Rolling Hills","Mountain Range","Mountains Edge","High Plains","Mountain Range Edge", "Foothills","High Hills","High Hills Edge"}, new String[]{"Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/7469, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/8f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.YOUNG_KATSURA + "_Red_Fox", AthsGlobal.YOUNG_KATSURA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat_Grass"},  new String[] {"Plains","Mountains","Rolling Hills","Mountain Range","Mountains Edge","High Plains","Mountain Range Edge", "Foothills","High Hills","High Hills Edge"}, new String[]{"Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/9969, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/8f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.YOUNG_KATSURA + "_Weeping", AthsGlobal.YOUNG_KATSURA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat_Grass"},  new String[] {"Plains","Mountains","Rolling Hills","Mountain Range","Mountains Edge","High Plains","Mountain Range Edge", "Foothills","High Hills","High Hills Edge"}, new String[]{"Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/8969, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/8f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.YOUNG_KATSURA + "_Weeping_Red_Fox", AthsGlobal.YOUNG_KATSURA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat_Grass"},  new String[] {"Plains","Mountains","Rolling Hills","Mountain Range","Mountains Edge","High Plains","Mountain Range Edge", "Foothills","High Hills","High Hills Edge"}, new String[]{"Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/10969, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/8f, /*maxTemp*/16f, /*minRain*/750f, /*maxRain*/4000f, /*minEVT*/0.5f, /*maxEVT*/5f,/*forestGen*/1f);

		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_ACACIA + "_Koa", AthsGlobal.YOUNG_ACACIA, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.KOA);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_ACACIA /*Utacacia*/, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.UTACACIA);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_ASH, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.ASH);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_ASPEN, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.ASPEN);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_BAOBAB, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.BAOBAB);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_BIRCH, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.BIRCH);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_CHESTNUT, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.CHESTNUT);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_DOUGLAS_FIR, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.DOUGLASFIR);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_EBONY, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.EBONY);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_FEVER, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.FEVERTREE);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_GHAF, new int[] {0,1,2,3}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.GHAF);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_GINGKO, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.GINGKO);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_HICKORY, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.HICKORY);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_KAPOK, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.KAPOK);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_LAUREL, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/1, /*dispersion*/5, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.LAUREL);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_LIMBA, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.LIMBA);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_MAHOE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MAHOE);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_MAHOGANY, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MAHOGANY);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_MANGROVE, new int[] {0,1,2}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel","terrafirmacraftplus:SaltWaterStationary"}, new String []{"Salt Swamp"}, new String[]{"Europe", "Asia","Africa","Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MANGROVE);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_MAPLE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.MAPLE);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_OAK, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia" ,"Africa", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.OAK);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_PALM, new int[] {0,1,2,3}, new String[] {"ore:blockSoil","ore:blockSand"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia","Africa","Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.PALM);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_PINE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.PINE);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_SEQUOIA, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.REDWOOD);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_SPRUCE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa", "Europe"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.SPRUCE);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_SYCAMORE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.SYCAMORE);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_TEAK, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.TEAK);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_WHITE_CEDAR, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.WHITECEDAR);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_WHITE_ELM, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.WHITEELM);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_WILLOW, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Europe", "Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.WILLOW);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_YEW + "_Africa", AthsGlobal.FALLEN_BRANCH_YEW, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.AFRICANYEW);
		athsTreeHelper(AthsGlobal.FALLEN_BRANCH_YEW, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe", "Asia"},
				/*size*/1, /*dispersion*/1, /*minAltitude*/0, /*maxAltitude*/255, EnumTree.YEW);

		athsPlantHelper(AthsGlobal.WOODY_DEBRIS /*Normal*/, new int[] {0,1,2,3}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/5, /*dispersion*/15, /*rarity*/3500, /*minAltitude*/0, /*maxAltitude*/225, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/550f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1f);

		athsPlantHelper(AthsGlobal.BARNACLES /*Normal*/, new int[] {0}, new String[] {"ore:stone"}, new String[] {"Beach","Shore","Gravel_Beach"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/8, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/165, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.BARNACLES_EPIPHYTE /*Normal*/, new int[] {0}, new String[] {"ore:stone"}, new String[] {"Beach","Shore","Gravel_Beach"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/8, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/165, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.DEAD_FISH /*Normal*/, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel","ore:blockSoil"}, new String[] {"Beach","Shore","Gravel_Beach","Riverbank","Lakeshore"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/6000, /*minAltitude*/0, /*maxAltitude*/146, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.DRIFTWOOD /*Normal*/, new int[] {0,1,2,3}, new String[] {"ore:blockSand","ore:blockGravel","ore:blockSoil"}, new String[] {"Beach","Shore","Gravel_Beach","Riverbank","Lakeshore","Estuary"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/5, /*dispersion*/7, /*rarity*/3500, /*minAltitude*/0, /*maxAltitude*/146, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/1f);
		athsPlantHelper(AthsGlobal.KELP_DEBRIS /*Normal*/, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel","ore:blockSoil"}, new String[] {"Beach","Shore","Gravel_Beach"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/5, /*dispersion*/6, /*rarity*/4500, /*minAltitude*/0, /*maxAltitude*/146, /*minTemp*/-10f, /*maxTemp*/22f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.SEAWEED_DEBRIS /*Normal*/, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel","ore:blockSoil"}, new String[] {"Beach","Shore","Gravel_Beach"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/5, /*dispersion*/6, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/146, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.SHARK_EGGS /*Normal*/, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel","ore:blockSoil"}, new String[] {"Beach","Shore","Gravel_Beach"}, new String[]{"Americas","Asia","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/6900, /*minAltitude*/0, /*maxAltitude*/146, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);

		athsPlantHelper(AthsGlobal.SLIME_MOLD /*Chocolate_Tube*/, new int[] {0}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIME_MOLD + "_Diachea", AthsGlobal.SLIME_MOLD, new int[] {1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/30f, /*minRain*/600f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIME_MOLD + "_False_Puffball", AthsGlobal.SLIME_MOLD, new int[] {2}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIME_MOLD + "_Jasmine", AthsGlobal.SLIME_MOLD, new int[] {3}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIME_MOLD + "_Raspberry", AthsGlobal.SLIME_MOLD, new int[] {4}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/25f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIME_MOLD + "_The_Blob", AthsGlobal.SLIME_MOLD, new int[] {5}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/5000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIME_MOLD + "_Trichia", AthsGlobal.SLIME_MOLD, new int[] {6}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/30f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SLIME_MOLD + "_Wolfs_Milk", AthsGlobal.SLIME_MOLD, new int[] {7}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/3500, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);

		athsPlantHelper(AthsGlobal.SOUTBOSSIE, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary"}, new String[]{"Africa"},
				/*size*/4, /*dispersion*/2, /*rarity*/4256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/19f, /*minRain*/120f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SEA_LAVENDER, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary","Plains"}, new String[]{"Africa","Americas","Asia","Europe"},
				/*size*/4, /*dispersion*/2, /*rarity*/4256, /*minAltitude*/145, /*maxAltitude*/155, /*minTemp*/-3f, /*maxTemp*/40f, /*minRain*/220f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.BRASS_BUTTONS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary","Swamp","Peat Bog","Salt_Swamp"}, new String[]{"Africa"},
				/*size*/4, /*dispersion*/2, /*rarity*/4256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/17f, /*minRain*/180f, /*maxRain*/950f, /*minEVT*/0f, /*maxEVT*/5f);

		athsPlantHelper(AthsGlobal.MARRAM_GRASS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary"}, new String[]{"Americas","Africa","Europe"},
				/*size*/12, /*dispersion*/4, /*rarity*/256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/-1f, /*maxTemp*/20f, /*minRain*/220f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SEA_OATS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary"}, new String[]{"Americas"},
				/*size*/12, /*dispersion*/4, /*rarity*/256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/10f, /*maxTemp*/25f, /*minRain*/220f, /*maxRain*/2500f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SEA_COUCH_GRASS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary","Salt Swamp"}, new String[]{"Asia","Europe"},
				/*size*/12, /*dispersion*/4, /*rarity*/556, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/18f, /*minRain*/100f, /*maxRain*/600f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SEA_COUCH_GRASS + "_Terrestrial", AthsGlobal.SEA_COUCH_GRASS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Plains","Rolling Hills"}, new String[]{"Asia","Europe"},
				/*size*/12, /*dispersion*/4, /*rarity*/3556, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/18f, /*minRain*/100f, /*maxRain*/600f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SEA_LYME_GRASS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary"}, new String[]{"Europe"},
				/*size*/12, /*dispersion*/4, /*rarity*/256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/220f, /*maxRain*/5000f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SPINIFEX, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Beach","Gravel Beach","Shore","Estuary"}, new String[]{"Asia","Africa"},
				/*size*/12, /*dispersion*/4, /*rarity*/256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/40f, /*minRain*/120f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.SPINIFEX + "_Terrestrial", AthsGlobal.SPINIFEX, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel"}, new String[]{"Plains","Rolling Hills"}, new String[]{"Asia","Africa"},
				/*size*/12, /*dispersion*/4, /*rarity*/3256, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/40f, /*minRain*/80f, /*maxRain*/300f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.WATER_CORDGRASS, new int[] {0}, new String[] {"ore:blockSand","ore:blockGravel","ore:blockSoil","terrafirmacraftplus:PeatGrass","terrafirmacraftplus:SaltWaterStationary"}, new String[]{"Estuary","Salt Swamp"}, new String[]{"Americas"},
				/*size*/150, /*dispersion*/3, /*rarity*/530, /*minAltitude*/145, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/24f, /*minRain*/550f, /*maxRain*/1400f, /*minEVT*/0f, /*maxEVT*/5f);
		athsPlantHelper(AthsGlobal.RUSHES, new int[] {0}, new String[] {"ore:blockSoil", "terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Lake","Lakeshore","Riverbank","Swamp","Peat Bog","River","Mountains","Mountains Edge","High Hills","High Hills Edge"}, new String[]{"Europe","Asia","Americas","Africa"},
				/*size*/90, /*dispersion*/2, /*rarity*/984, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/-15f, /*maxTemp*/24f, /*minRain*/350f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.RUSHES + "_Tropics", AthsGlobal.RUSHES, new int[] {0}, new String[] {"ore:blockSoil", "terrafirmacraftplus:FreshWaterStationary","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Mountains","Mountains Edge","High Hills","High Hills Edge"}, new String[]{"Europe","Asia","Americas","Africa"},
				/*size*/90, /*dispersion*/2, /*rarity*/984, /*minAltitude*/0, /*maxAltitude*/145, /*minTemp*/24f, /*maxTemp*/40f, /*minRain*/350f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/6f,/*forestGen*/0f);

		athsPlantHelper(AthsGlobal.BOOTSTRAP_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Asia","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/225, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BOOTSTRAP_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Asia","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/225, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOLF_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WOLF_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone","pine","spruce","douglasfir","whitecedar","yew"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SULPHUR_DUST_LICHEN, new int[] {0,1}, new String[] {"alltrees","ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/8f, /*minRain*/250f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.GREENSHIELD_LICHEN, new int[] {0,1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia","Africa"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/40f, /*minRain*/450f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BRITISH_SOLDIERS_LICHEN, new int[] {0,1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/22f, /*minRain*/650f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MELANOHALEA_LICHEN, new int[] {0,1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/30f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WHITEWASH_LICHEN, new int[] {0,1}, new String[] {"alltrees"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/350f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ORANGE_SEA_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, new String[] {"Beach","Shore","Ocean","Gravel Beach","Estuary"}, new String[]{"Americas","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.ORANGE_SEA_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, new String[] {"Beach","Shore","Ocean","Gravel Beach","Estuary"}, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-10f, /*maxTemp*/15f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.HORSEHAIR_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/19f, /*minRain*/950f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SALTED_SHIELD_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SALTED_SHIELD_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLOODSTAIN_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/40f, /*minRain*/250f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.BLOODSTAIN_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/40f, /*minRain*/250f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.JEWEL_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/40f, /*minRain*/50f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.JEWEL_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/40f, /*minRain*/50f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.MAP_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/180, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/11f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MAP_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/4100, /*minAltitude*/180, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/11f, /*minRain*/650f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MOONGLOW_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Asia","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/210, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/16f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.MOONGLOW_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Americas","Asia","Europe"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/210, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/16f, /*minRain*/200f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.ROCK_OLIVE_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.ROCK_OLIVE_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/0f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.ROCK_TRIPE_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/19f, /*minRain*/100f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.ROCK_TRIPE_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/19f, /*minRain*/100f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.WREATH_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/550f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.WREATH_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/550f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.ROSETTE_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/90f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.ROSETTE_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/90f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.FRECKLE_PELT_LICHEN, new int[] {0,1}, new String[] {"ore:stone","ore:blockSand","ore:blockGravel","ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Asia"},
				/*size*/10, /*dispersion*/4, /*rarity*/5100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-40f, /*maxTemp*/-5f, /*minRain*/0f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.SUNBURST_LICHEN, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/20f, /*minRain*/350f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SUNBURST_LICHEN_EPIPHYTE, new int[] {0,1}, new String[] {"ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/3100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8f, /*maxTemp*/20f, /*minRain*/350f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);

		athsPlantHelper(AthsGlobal.MORNING_GLORY /*Bindweed*/, new int[] {0}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia", "Africa","Europe"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4, /*maxTemp*/40f, /*minRain*/450f, /*maxRain*/16000f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MORNING_GLORY + "_Mexican", AthsGlobal.MORNING_GLORY, new int[] {1}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/23f, /*maxTemp*/40f, /*minRain*/550f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MORNING_GLORY + "_Tiger_Foot", AthsGlobal.MORNING_GLORY, new int[] {2}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Africa"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MORNING_GLORY + "_Turpeth", AthsGlobal.MORNING_GLORY, new int[] {3}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/19, /*maxTemp*/30f, /*minRain*/330f, /*maxRain*/920f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.3f);
		athsPlantHelper(AthsGlobal.MORNING_GLORY + "_Woodrose", AthsGlobal.MORNING_GLORY, new int[] {4}, new String[] {"alltrees", "ore:stone"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia"},
				/*size*/15, /*dispersion*/1, /*rarity*/2100, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/16, /*maxTemp*/40f, /*minRain*/670f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);

		athsPlantHelper(AthsGlobal.SWITCHGRASS, new int[] {0,1,2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Mountains","Rolling Hills","High Hills Edge","Foothills","Mountain Range Edge","Mountain Range","High Plains"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/4, /*rarity*/7128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/260f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.TALL_FESCUE, new int[] {0,1,2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Mountains","Rolling Hills","High Hills Edge","Foothills","Mountain Range Edge","Mountain Range","High Plains"}, new String[]{"Europe"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/200f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SIGNALGRASS, new int[] {0,1,2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Mountains","Rolling Hills","High Hills Edge","Foothills","Mountain Range Edge","Mountain Range","High Plains"}, new String[]{"Asia", "Africa"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/22f, /*maxTemp*/40f, /*minRain*/260f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SWEET_VERNAL_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia", "Africa", "Europe"},
				/*size*/80, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/200f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.BLUEGRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia","Americas","Africa","Europe"},
				/*size*/40, /*dispersion*/4, /*rarity*/2128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/21f, /*minRain*/300f, /*maxRain*/950f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/0.0f);
		athsPlantHelper(AthsGlobal.FALSE_OAT_GRASS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge", "Riverbank","Beach"}, new String[]{"Asia", "Africa", "Europe"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/15f, /*minRain*/300f, /*maxRain*/950f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.REED_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[] {"Plains","Rolling Hills","Mountains","Mountains Edge","Foothills","Mountain Range Edge","Mountain Range","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia", "Americas", "Europe"},
				/*size*/80, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/24f, /*minRain*/270f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.FESCUE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia", "Americas","Africa", "Europe"},
				/*size*/100, /*dispersion*/4, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/250f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.COCKS_FOOT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","Swamp","Riverbank","Lakeshore"}, new String[]{"Asia", "Africa", "Europe"},
				/*size*/60, /*dispersion*/4, /*rarity*/6528, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/7f, /*maxTemp*/25, /*minRain*/190f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.COMMON_BENT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank"}, new String[]{"Asia", "Europe"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/16f, /*minRain*/400, /*maxRain*/1000, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.MEADOW_FOXTAIL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia", "Europe"},
				/*size*/80, /*dispersion*/1, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/320f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.FEATHER_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"America"},
				/*size*/60, /*dispersion*/4, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/40f, /*minRain*/250f, /*maxRain*/950f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.WAVY_HAIR_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia", "Africa", "Europe", "Americas"},
				/*size*/80, /*dispersion*/4, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/26f, /*minRain*/200f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.CANADA_WILD_RYE, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","Beach","High Plains"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/400, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.BIG_BLUESTEM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/400, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.LITTLE_BLUESTEM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/500f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.INDIAN_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/400, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.BUFFALOGRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/145f, /*maxRain*/500, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.BLUE_GRAMA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Americas"},
				/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/18f, /*minRain*/145f, /*maxRain*/500, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.AFRICAN_BRISTLE_GRASS, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Africa"},
		/*size*/100, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/300f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.SORGHUM, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Africa"},
				/*size*/70, /*dispersion*/4, /*rarity*/6128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/16f, /*maxTemp*/28f, /*minRain*/170f, /*maxRain*/500f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.GUINEA_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Africa"},
				/*size*/60, /*dispersion*/4, /*rarity*/4128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/15f, /*maxTemp*/25f, /*minRain*/130f, /*maxRain*/670f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.GUINEA_GRASS + "_Asia",AthsGlobal.GUINEA_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","High Plains"}, new String[]{"Asia"},
				/*size*/40, /*dispersion*/4, /*rarity*/7128, /*minAltitude*/146, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/25f, /*minRain*/130f, /*maxRain*/160f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.PURPLE_MOOR_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Peat Bog","Lakeshore","Peat Bog"}, new String[]{"Asia", "Europe","Africa"},
				/*size*/80, /*dispersion*/1, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/155, /*minTemp*/-1f, /*maxTemp*/13f, /*minRain*/320f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.BLACK_SPEARGRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia", "Africa"},
				/*size*/80, /*dispersion*/1, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/20f, /*maxTemp*/40f, /*minRain*/320f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.PAMPAS_GRASS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Lakeshore","Rolling Hills","Riverbank"}, new String[]{"Americas"},
				/*size*/80, /*dispersion*/1, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/17f, /*maxTemp*/26f, /*minRain*/320f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.BLEEDING_FAIRY_HELMET, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/8128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/30f, /*minRain*/800f, /*maxRain*/4000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CROWN_TIPPED_CORAL_FUNGUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/6928, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/19f, /*minRain*/720f, /*maxRain*/8000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BOG_ASPHODEL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Peat Bog","Lakeshore","Peat Bog"}, new String[]{"Europe"},
				/*size*/12, /*dispersion*/1, /*rarity*/7128, /*minAltitude*/0, /*maxAltitude*/155, /*minTemp*/-1f, /*maxTemp*/13f, /*minRain*/320f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0.0f);
		athsPlantHelper(AthsGlobal.DOCK, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge"}, new String[]{"Asia", "Africa", "Europe", "Americas"},
				/*size*/15, /*dispersion*/8, /*rarity*/8128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-6f, /*maxTemp*/40f, /*minRain*/320f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/-0.3f);
		athsPlantHelper(AthsGlobal.POISON_HEMLOCK /*Poison Hemlock*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Lakeshore","Riverbank","Rolling Hills"}, new String[]{"Europe","Africa"},
				/*size*/8, /*dispersion*/1, /*rarity*/6528, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/21f, /*minRain*/420f, /*maxRain*/1600/, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.POISON_HEMLOCK + "_Water_Hemlock", AthsGlobal.POISON_HEMLOCK, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Riverbank","Lakeshore","Rolling Hills"}, new String[]{"Europe","Americas"},
				/*size*/8, /*dispersion*/1, /*rarity*/6528, /*minAltitude*/0, /*maxAltitude*/150, /*minTemp*/6f, /*maxTemp*/21f, /*minRain*/420f, /*maxRain*/1600/, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.POISON_HEMLOCK + "_Water_Dropwort", AthsGlobal.POISON_HEMLOCK, new int[] {2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Riverbank","Lakeshore","Rolling Hills"}, new String[]{"Europe","Asia","Africa"},
				/*size*/8, /*dispersion*/1, /*rarity*/6528, /*minAltitude*/0, /*maxAltitude*/150, /*minTemp*/6f, /*maxTemp*/21f, /*minRain*/420f, /*maxRain*/1600/, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.ORPINE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia"},
				/*size*/4, /*dispersion*/4, /*rarity*/7128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/16f, /*minRain*/520f, /*maxRain*/900f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0.2f);
		athsPlantHelper(AthsGlobal.WOLFS_BANE /*Blue*/, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String [] {"Mountains","High Hills","Mountain Range","Mountains Edge","Mountain Range Edge","High Hills Edge","Rolling Hills","Plains","High Plains","Foothills"}, new String[]{"Europe","Americas","Asia"},
				/*size*/10, /*dispersion*/4, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/14f, /*minRain*/410f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.WOLFS_BANE + "_Yellow",AthsGlobal.WOLFS_BANE, new int[] {1}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String [] {"Mountains","High Hills","Mountain Range","Mountains Edge","Mountain Range Edge","High Hills Edge","Foothills"}, new String[]{"Europe","Asia"},
				/*size*/10, /*dispersion*/4, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-4f, /*maxTemp*/14f, /*minRain*/410f, /*maxRain*/850f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.WILD_DAGGA, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa"},
				/*size*/6, /*dispersion*/4, /*rarity*/6584, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/12f, /*maxTemp*/20f, /*minRain*/250f, /*maxRain*/650f, /*minEVT*/1f, /*maxEVT*/4f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.COMMON_MUGWORT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Africa","Asia"},
				/*size*/6, /*dispersion*/4, /*rarity*/5568, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/14f, /*minRain*/420f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.COMMON_MUGWORT + "_Alaska",AthsGlobal.COMMON_MUGWORT, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/6, /*dispersion*/4, /*rarity*/5992, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/0f, /*minRain*/420f, /*maxRain*/800f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.COMMON_WORMWOOD, new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand","ore:blockGravel"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Africa","Asia"},
				/*size*/6, /*dispersion*/4, /*rarity*/5128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/15f, /*minRain*/460f, /*maxRain*/870f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.COMMON_KNAPWEED, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/7, /*dispersion*/11, /*rarity*/756, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/18f, /*minRain*/250f, /*maxRain*/600f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.WILLOW_HERB, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe", "Asia"},
				/*size*/19, /*dispersion*/3, /*rarity*/4356, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/17f, /*minRain*/450f, /*maxRain*/780f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.WILLOW_HERB + "_Tropics", AthsGlobal.WILLOW_HERB, new int[] {0}, new String[] {"ore:blockSoil"}, new String [] {"Mountains","Mountain Range","Mountain Range Edge""}, new String[]{"Americas","Africa","Europe", "Asia"},
				/*size*/19, /*dispersion*/3, /*rarity*/4356, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-5f, /*maxTemp*/17f, /*minRain*/450f, /*maxRain*/780f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.ANGELICA, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Peat Bog","Lakeshore","Rolling Hills","Plains","Peat Bog"}, new String[]{"Asia", "Americas","Europe","Africa"},
				/*size*/15, /*dispersion*/2, /*rarity*/7128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-13f, /*maxTemp*/14f, /*minRain*/420f, /*maxRain*/950, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.DOGS_MERCURY, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","High Plains","High Hills","High Hills Edge","Lakeshore","Riverbank","Swamp"}, new String[]{"Europe"},
				/*size*/200, /*dispersion*/1, /*rarity*/9884, /*minAltitude*/144, /*maxAltitude*/220, /*minTemp*/-1f, /*maxTemp*/15f, /*minRain*/850f, /*maxRain*/2500, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.JACOBS_LADDER, new int[] {0}, new String[] {"ore:blockSoil","ore:blockGravel","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Peat Bog","Lakeshore","Rolling Hills","Plains","High Hills","High Hills Edge","Peat Bog"}, new String[]{"Asia","Americas","Europe"},
				/*size*/5, /*dispersion*/2, /*rarity*/5128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-15f, /*maxTemp*/11f, /*minRain*/530, /*maxRain*/1050, /*minEVT*/0.25f, /*maxEVT*/5f,/*forestGen*/0.3f);
		athsPlantHelper(AthsGlobal.RED_CATCHFLY, new int[] {0,1}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","High Hills","High Hills Edge","Swamp"}, new String[]{"Americas"},
				/*size*/6, /*dispersion*/3, /*rarity*/4384, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/6f, /*maxTemp*/14f, /*minRain*/430f, /*maxRain*/850f, /*minEVT*/0.25f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.WATER_AVENS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Lakeshore","Riverbank","Peat Bog"}, new String[]{"Europe","Asia","Americas"},
				/*size*/3, /*dispersion*/1, /*rarity*/5528, /*minAltitude*/0, /*maxAltitude*/155, /*minTemp*/4f, /*maxTemp*/15f, /*minRain*/420f, /*maxRain*/2000/, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0.1f);
		athsPlantHelper(AthsGlobal.MARSH_FOXTAIL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Swamp","Peat Bog","Lakeshore","Peat Bog"}, new String[]{"Europe","Asia"},
				/*size*/12, /*dispersion*/1, /*rarity*/7128, /*minAltitude*/0, /*maxAltitude*/155, /*minTemp*/-5f, /*maxTemp*/17f, /*minRain*/520f, /*maxRain*/1850f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0.0f);
		athsPlantHelper(AthsGlobal.BIRDS_FOOT_TREFOIl, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Africa","Asia","Europe"},
				/*size*/7, /*dispersion*/11, /*rarity*/4756, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/22f, /*minRain*/350f, /*maxRain*/700f, /*minEVT*/0f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.LADYS_MANTLE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","Rolling Hills","High Hills","High Hills Edge","Foothills","Mountains Edge","Mountain Range Edge","Plains"}, new String[]{"Asia","Europe"},
				/*size*/10, /*dispersion*/1, /*rarity*/7828, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8, /*maxTemp*/12f, /*minRain*/520f, /*maxRain*/720, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.LADYS_MANTLE + "_Africa_and_Americas",AthsGlobal.LADYS_MANTLE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Foothills","Mountains Edge","Mountain Range Edge","Mountains","Mountain Range"}, new String[]{"Americas","Africa"},
				/*size*/10, /*dispersion*/1, /*rarity*/7828, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-8, /*maxTemp*/12f, /*minRain*/520f, /*maxRain*/720, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/0f);
		athsPlantHelper(AthsGlobal.PIGNUT, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe"},
				/*size*/7, /*dispersion*/2, /*rarity*/6128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/2f, /*maxTemp*/16f, /*minRain*/580f, /*maxRain*/1000f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/0.5f);
		athsPlantHelper(AthsGlobal.GREAT_BURNET, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Europe","Asia","Americas"},
				/*size*/14, /*dispersion*/7, /*rarity*/5128, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-1, /*maxTemp*/13f, /*minRain*/390f, /*maxRain*/710f, /*minEVT*/0f, /*maxEVT*/5f,/*forestGen*/-0.3f);
		athsPlantHelper(AthsGlobal.PRAIRIE_SMOKE, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Mountains","Mountains Edge","High Hills","High Hills Edge"}, new String[]{"Americas"},
				/*size*/4, /*dispersion*/2, /*rarity*/8984, /*minAltitude*/0, /*maxAltitude*/225, /*minTemp*/-6f, /*maxTemp*/12f, /*minRain*/630f, /*maxRain*/790f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.PURPLE_LOOSESTRIFE, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/14, /*dispersion*/2, /*rarity*/5984, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-3f, /*maxTemp*/24f, /*minRain*/630f, /*maxRain*/790f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.COMMON_MILKWEED, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/10, /*dispersion*/3, /*rarity*/3984, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-3f, /*maxTemp*/24f, /*minRain*/630f, /*maxRain*/790f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.CONEFLOWER, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/4, /*dispersion*/2, /*rarity*/4984, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/2f, /*maxTemp*/20f, /*minRain*/630f, /*maxRain*/790f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.PASQUE_FLOWER, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Lakeshore","Riverbank","Swamp"}, new String[]{"Americas"},
				/*size*/3, /*dispersion*/7, /*rarity*/5284, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/-6f, /*maxTemp*/17f, /*minRain*/430f, /*maxRain*/680f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.SMOKETREE, new int[] {0,1}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills"}, new String[]{"Americas","Europe","Asia"},
				/*size*/4, /*dispersion*/12, /*rarity*/10084, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/15f, /*maxTemp*/23f, /*minRain*/590f, /*maxRain*/830f, /*minEVT*/0.5f, /*maxEVT*/3f);
		athsPlantHelper(AthsGlobal.PRIMROSE, new int[] {0,1,2,3}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Riverbank"}, new String[]{"Europe","Asia"},
				/*size*/8, /*dispersion*/4, /*rarity*/10084, /*minAltitude*/0, /*maxAltitude*/200, /*minTemp*/7f, /*maxTemp*/8f, /*minRain*/620f, /*maxRain*/1030f, /*minEVT*/0f, /*maxEVT*/4f);
		athsPlantHelper(AthsGlobal.LOBELIA /*Blue*/, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","High Hills","High Hills Edge","Mountains Edge","Mountains","Foothills","Swamp","Riverbank","Peat Bog","Lakeshore"}, new String[]{"Europe","Americas","Africa","Asia"},
				/*size*/5, /*dispersion*/5, /*rarity*/4208, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/567, /*maxRain*/2100, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.LOBELIA + "_White",AthsGlobal.LOBELIA, new int[] {1}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","High Hills","High Hills Edge","Mountains Edge","Mountains","Foothills","Swamp","Riverbank","Peat Bog","Lakeshore"}, new String[]{"Europe","Asia","Americas","Africa"},
				/*size*/5, /*dispersion*/5, /*rarity*/4208, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/567, /*maxRain*/2100, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.LOBELIA + "_Cardinal",AthsGlobal.LOBELIA, new int[] {2}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Swamp","Riverbank","Peat Bog","Lakeshore"}, new String[]"Americas"},
				/*size*/5, /*dispersion*/5, /*rarity*/6208, /*minAltitude*/0, /*maxAltitude*/160, /*minTemp*/5f, /*maxTemp*/32f, /*minRain*/467, /*maxRain*/4000, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.LOBELIA + "_Giant",AthsGlobal.LOBELIA, new int[] {3}, new String[] {"ore:blockSoil"}, new String[]{"High Hills","Mountains Edge","Mountains","Foothills","Mountain Range","Mountain Range Edge"}, new String[]{"Africa"},
				/*size*/5, /*dispersion*/5, /*rarity*/3608, /*minAltitude*/170, /*maxAltitude*/255, /*minTemp*/18f, /*maxTemp*/40f, /*minRain*/90, /*maxRain*/340, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.MARSH_FERN, new int[] {0}, new String[] {"ore:blockSoil"}, new String[]{"Plains","Rolling Hills","Swamp","Peat Bog"}, new String[]"Americas","Europe","Asia"},
				/*size*/15, /*dispersion*/3, /*rarity*/5208, /*minAltitude*/0, /*maxAltitude*/155, /*minTemp*/-3f, /*maxTemp*/15f, /*minRain*/650, /*maxRain*/2500, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.BLUE_SAGE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","Rolling Hills","High Hills Edge","Swamp","Riverbank","Lakeshore"}, new String[]{"Asia", "Europe"},
				/*size*/11, /*dispersion*/10, /*rarity*/58528, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-7f, /*maxTemp*/17, /*minRain*/170f, /*maxRain*/750f, /*minEVT*/0f, /*maxEVT*/2f,/*forestGen*/-1.0f);
		athsPlantHelper(AthsGlobal.DWARF_BIRCH, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","ore:stone","ore:blockGravel","terrafirmacraftplus:PeatGrass"}, new String[]{"Plains","High Hills","High Plains","Mountains","Mountain Range","Mountains Edge","Mountain Range Edge","Foothills","Rolling Hills","High Hills Edge","Swamp","Riverbank","Lakeshore"}, new String[]{"Asia", "Africa", "Europe"},
				/*size*/8, /*dispersion*/20, /*rarity*/4228, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-20, /*maxTemp*/8, /*minRain*/100f, /*maxRain*/750f, /*minEVT*/0.25, /*maxEVT*/2f,/*forestGen*/-0.5f);

		athsPlantHelper(AthsGlobal.MOSS_CARPET_GREEN, new int[] {0,1,2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","ore:stone","ore:blockGravel","terrafirmacraftplus:PeatGrass"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/32, /*dispersion*/3, /*rarity*/2968, /*minAltitude*/0, /*maxAltitude*/235, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);
		athsPlantHelper(AthsGlobal.MOSS_CARPET_PEAT, new int[] {0,1,2}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat","terrafirmacraftplus:PeatGrass"}, new String[]{"Peat Bog"}, new String[]{"Americas", "Asia","Europe","Africa"},
				/*size*/32, /*dispersion*/3, /*rarity*/2968, /*minAltitude*/0, /*maxAltitude*/235, /*minTemp*/-20f, /*maxTemp*/40f, /*minRain*/800f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f);

		athsPlantHelper(AthsGlobal.SULPHUR_SHELF + "_White_Pored",AthsGlobal.SULPHUR_SHELF, new int[] {0}, new String[] {"oak","willow"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SULPHUR_SHELF_ROSETTE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/8600, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SULPHUR_SHELF_ROSETTE + "_White_Pored_Rosette",AthsGlobal.SULPHUR_SHELF_ROSETTE, new int[] {0}, new String[] {"ore:blockSoil"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/7600, /*minAltitude*/0, /*maxAltitude*/185, /*minTemp*/-1f, /*maxTemp*/14f, /*minRain*/750f, /*maxRain*/1300f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.DISTANT_WEBCAP, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/3f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/2400f, /*minEVT*/1f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.YELLOW_STAINING_AGARICUS, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe","Americas","Asia","Africa"},
				/*size*/4, /*dispersion*/5, /*rarity*/6596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/550f, /*maxRain*/1270f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.THE_PRINCE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Europe","Africa","Asia","Americas"},
				/*size*/4, /*dispersion*/5, /*rarity*/7596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/550f, /*maxRain*/1270f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.MOSSY_MAZE_POLYPORE, new int[] {0}, new String[] {"oak","willow","maple","aspen","ash","sycamore","hickory","birch","whiteelm","chestnut","spruce","pine"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.FALSE_TURKEY_TAIL, new int[] {0}, new String[] {"oak","willow","maple","aspen","ash","sycamore","hickory","birch","whiteelm","chestnut"}, AthsGlobal.LAND_BIOMES, new String[]{"Asia","Americas","Europe","Africa"},
				/*size*/1, /*dispersion*/1, /*rarity*/4000, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/4f, /*maxTemp*/17f, /*minRain*/750f, /*maxRain*/2000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.PINKMOTTLE_WOODWAX, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe"},
				/*size*/8, /*dispersion*/5, /*rarity*/8396, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/5f, /*maxTemp*/18f, /*minRain*/950f, /*maxRain*/4100f, /*minEVT*/0.25f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.CURY_STALK_BOLETE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/2, /*dispersion*/2, /*rarity*/6796, /*minAltitude*/0, /*maxAltitude*/190, /*minTemp*/1f, /*maxTemp*/18f, /*minRain*/670f, /*maxRain*/1800f, /*minEVT*/0f, /*maxEVT*/7f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BERKELEYS_POLYPORE, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:PeatGrass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Asia","Europe"},
				/*size*/1, /*dispersion*/1, /*rarity*/6096, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/0f, /*maxTemp*/20f, /*minRain*/790f, /*maxRain*/3400f, /*minEVT*/0f, /*maxEVT*/8f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.SHAGGY_PARASOL, new int[] {0}, new String[] {"ore:blockSoil","terrafirmacraftplus:Peat_Grass"},  AthsGlobal.LAND_BIOMES, new String[]{"Americas","Africa","Europe"},
				/*size*/4, /*dispersion*/5, /*rarity*/7596, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/6f, /*maxTemp*/18f, /*minRain*/450f, /*maxRain*/870f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.BLACK_STAINING_POLYPORE, new int[] {0}, new String[] {"oak","maple","chestnut","ash"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas"},
				/*size*/1, /*dispersion*/1, /*rarity*/1900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-2f, /*maxTemp*/13f, /*minRain*/750f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		athsPlantHelper(AthsGlobal.TENDER_NESTING_POLYPORE, new int[] {0}, new String[] {"oak","spruce","pine","birch"}, AthsGlobal.LAND_BIOMES, new String[]{"Americas","Europe","Africa","Asia"},
				/*size*/1, /*dispersion*/1, /*rarity*/1900, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/-3f, /*maxTemp*/18f, /*minRain*/750f, /*maxRain*/1500f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f);
		


		
		AthsWorldGenPlants.plantList.put("shrub", getPlantData("shrub", Reference.MOD_ID+":"+TFCBlocks.shrub.getUnlocalizedName().substring(5), new int[] {0}, new String[] {"ore:blockSoil","ore:blockSand"}, new String[]{"Riverbank"}, new String[]{"Americas","Europe","Africa","Asia"},
		        /*size*/15, /*dispersion*/10, /*rarity*/1550, /*minAltitude*/0, /*maxAltitude*/255, /*minTemp*/8f, /*maxTemp*/30f, /*minRain*/25f, /*maxRain*/135f, /*minEVT*/0f, /*maxEVT*/4f,/*forestGen*/1.0f));
		for(int i = 0; i < numCustomGenerators; i++) {
			String name = "_z" + i;
			AthsWorldGenPlants.plantList.put(name, getCustomPlantData(name, Reference.MOD_ID+":"+TFCBlocks.undergrowth.getUnlocalizedName().substring(5), new int[] {0}, new String[] {"ore:blockSoil"}, new String[] {"All", "!Hell"}, new String[]{"Americas", "Asia"}, 
					/*size*/3, /*dispersion*/1, /*rarity*/424, /*minAltitude*/0, /*maxAltitude*/180, /*minTemp*/0f, /*maxTemp*/40f, /*minRain*/750f, /*maxRain*/16000f, /*minEVT*/0f, /*maxEVT*/10f,/*forestGen*/1.0f));
		}
		
		for(PlantSpawnData d : AthsWorldGenPlants.plantList.values()) {
			if(d.block instanceof BlockPlantCactus) {
				rarityHelper(d, rarityCactus);
			}
			else if(d.block instanceof BlockPlantEpiphyte3d) {
				rarityHelper(d, rarityEpiphyte);
			}
			else if(d.block instanceof ILilyPad) {
				rarityHelper(d, rarityLilyPad);
			}
			else if(d.block instanceof ITree) {
				rarityHelper(d, rarityTree);
			}
			else {
				rarityHelper(d, rarityOther);
			}
		}

		
		if (config.hasChanged())
			config.save();
	}

	private static void rarityHelper(PlantSpawnData d, float rarity) {
		if(rarity == 1f){
			return;
		}
		else if (rarity <= 0f) {
			d.size = 0;
		}
		else {
			d.rarity *= rarity;
		}
	}
	
	private static PlantSpawnData getPlantData(String category, String blockName, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int rarity, int minAltitude, int maxAltitude, float minTemp, float maxTemp, float minRainfall, float maxRainfall, float minEVT, float maxEVT, float forestGen){
		return new PlantSpawnData(
			blockName,
			config.get(category, "metas", metas).getIntList(),
			config.get(category, "growOnBlocks", growOnBlocks).getStringList(),
			config.get(category, "biomes", biomes).setValidValues(AthsGlobal.ALLOWED_BIOMES).getStringList(),
			config.get(category, "regions", regions).getStringList(),
			config.get(category, "size", size).setMinValue(0).getInt(),
			config.get(category, "dispersion", dispersion).setMinValue(1).getInt(),
			config.get(category, "rarity", rarity).setMinValue(1).getInt(),
			config.get(category, "minAltitude", minAltitude).setMinValue(0).setMaxValue(255).getInt(),
			config.get(category, "maxAltitude", maxAltitude).setMinValue(0).setMaxValue(255).getInt(),
			(float)config.get(category, "minTemp", minTemp).getDouble(),
			(float)config.get(category, "maxTemp", maxTemp).getDouble(),
			(float)config.get(category, "minRainfall", minRainfall).getDouble(),
			(float)config.get(category, "maxRainfall", maxRainfall).getDouble(),
			(float)config.get(category, "minEVT", minEVT).getDouble(),
			(float)config.get(category, "maxEVT", maxEVT).getDouble(),
			(float)config.get(category, "forestGen", forestGen).getDouble());
	}
	
	private static PlantSpawnData getCustomPlantData(String category, String blockName, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int rarity, int minAltitude, int maxAltitude, float minTemp, float maxTemp, float minRainfall, float maxRainfall, float minEVT, float maxEVT, float forestGen){
		return new PlantSpawnData(
			config.get(category, "blockName", blockName).getString(),
			config.get(category, "metas", metas).getIntList(),
			config.get(category, "growOnBlocks", growOnBlocks).getStringList(),
			config.get(category, "biomes", biomes).setValidValues(AthsGlobal.ALLOWED_BIOMES).getStringList(),
			config.get(category, "regions", regions).getStringList(),
			config.get(category, "size", size).setMinValue(0).getInt(),
			config.get(category, "dispersion", dispersion).setMinValue(1).getInt(),
			config.get(category, "rarity", rarity).setMinValue(1).getInt(),
			config.get(category, "minAltitude", minAltitude).setMinValue(0).setMaxValue(255).getInt(),
			config.get(category, "maxAltitude", maxAltitude).setMinValue(0).setMaxValue(255).getInt(),
			(float)config.get(category, "minTemp", minTemp).getDouble(),
			(float)config.get(category, "maxTemp", maxTemp).getDouble(),
			(float)config.get(category, "minRainfall", minRainfall).getDouble(),
			(float)config.get(category, "maxRainfall", maxRainfall).getDouble(),
			(float)config.get(category, "minEVT", minEVT).getDouble(),
			(float)config.get(category, "maxEVT", maxEVT).getDouble(),
			(float)config.get(category, "forestGen", forestGen).getDouble());
	}
	
	private static CrystalSpawnData getCrystalData(String category, String blockName, String blockName2, String[] growOnBlocks, int size, int dispersion, int rarity) {
		return new CrystalSpawnData(
			config.get(category, "blockName", blockName).getString(),
			config.get(category, "blockName2", blockName2).getString(),
			config.get(category, "growOnBlocks", growOnBlocks).setValidValues(AthsGlobal.ALLOWED_ROCKS).getStringList(),
			config.get(category, "size", size).setMinValue(1).getInt(),
			config.get(category, "dispersion", dispersion).setMinValue(1).getInt(),
			config.get(category, "rarity", rarity).setMinValue(1).getInt());
	}
	
	public static void athsCrystalHelper(String name, String[] growOnBlocks, int size, int dispersion, int rarity) {
		AthsWorldGenCrystals.crystalList.put(name, getCrystalData("~" + name, AthsMod.MODID+":"+name, "", growOnBlocks, size, dispersion, rarity));
	}
	
	public static void athsCrystalClusterHelper(String name, String[] growOnBlocks, int size, int dispersion, int rarity) {
		AthsWorldGenCrystals.crystalList.put(name, getCrystalData("~" + name, AthsMod.MODID+":"+name, AthsMod.MODID+":"+name+"_Cluster", growOnBlocks, size, dispersion, rarity));
	}
	
	public static void athsPlantHelper(String name, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int rarity, int minAltitude, int maxAltitude, float minTemp, float maxTemp, float minRainfall, float maxRainfall, float minEVT, float maxEVT) {
		athsPlantHelper(name, name, metas, growOnBlocks, biomes, regions, size, dispersion,
				rarity, minAltitude, maxAltitude, minTemp, maxTemp, minRainfall, maxRainfall, minEVT, maxEVT, 0.0f);
	}
	//category version
	public static void athsPlantHelper(String category, String name, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int rarity, int minAltitude, int maxAltitude, float minTemp, float maxTemp, float minRainfall, float maxRainfall, float minEVT, float maxEVT) {
		athsPlantHelper(category, name, metas, growOnBlocks, biomes, regions, size, dispersion,
				rarity, minAltitude, maxAltitude, minTemp, maxTemp, minRainfall, maxRainfall, minEVT, maxEVT, 0.0f);
	}
	
	/* forestGen version*/
	public static void athsPlantHelper(String name, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int rarity, int minAltitude, int maxAltitude, float minTemp, float maxTemp, float minRainfall, float maxRainfall, float minEVT, float maxEVT, float forestGen) {
		athsPlantHelper(name, name, metas, growOnBlocks, biomes, regions, size, dispersion,
				rarity, minAltitude, maxAltitude, minTemp, maxTemp, minRainfall, maxRainfall, minEVT, maxEVT, forestGen);
	}
	
	/* forestGen category version*/
	public static void athsPlantHelper(String category, String name, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int rarity, int minAltitude, int maxAltitude, float minTemp, float maxTemp, float minRainfall, float maxRainfall, float minEVT, float maxEVT, float forestGen) {
		AthsWorldGenPlants.plantList.put(category, getPlantData(category, AthsMod.MODID+":"+name, metas, growOnBlocks, biomes, regions, size, dispersion,
				rarity, minAltitude, maxAltitude, minTemp, maxTemp, minRainfall, maxRainfall, minEVT, maxEVT, forestGen));
	}
	
	public static void athsTreeHelper(String name, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int minAltitude, int maxAltitude, EnumTree tree) {
		athsTreeHelper(name, name, metas, growOnBlocks, biomes, regions, size, dispersion, minAltitude, maxAltitude, tree);
	}
	
	/* alternate spawn parameters for same tree*/
	public static void athsTreeHelper(String category, String name, int[] metas, String[] growOnBlocks, String[] biomes, String[] regions, int size, int dispersion,
			int minAltitude, int maxAltitude, EnumTree tree) {
		AthsWorldGenPlants.plantList.put(category, getPlantData(category, AthsMod.MODID+":"+name, metas, growOnBlocks, biomes, regions, size, dispersion,
				(int)(AthsGlobal.TREE_BASE_RARITY / tree.rarity), minAltitude, maxAltitude, tree.minTemp, tree.maxTemp, tree.minRain, tree.maxRain, tree.minEVT, tree.maxEVT, 1.0f));
	}
}
