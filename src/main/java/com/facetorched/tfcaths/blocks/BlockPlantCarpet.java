package com.facetorched.tfcaths.blocks;

import java.util.Random;

import com.facetorched.tfcaths.AthsGlobal;
import com.facetorched.tfcaths.items.itemblocks.ItemPlantCarpet;
import com.facetorched.tfcaths.util.AthsRandom;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

public class BlockPlantCarpet extends BlockPlantLayer{
	public float redMult;
	public int redShift;
	public float greenMult;
	public int greenShift;
	public float blueMult;
	public int blueShift;
	
	public BlockPlantCarpet() {
		this(Material.vine);
	}
	public BlockPlantCarpet(Material m) {
		super(m);
		setItemBlock(ItemPlantCarpet.class);
		setHasNoDrops();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess bAccess, int x, int y, int z)
	{
		Random random = AthsRandom.getRandom(x, z);
		int rgb = 2 * random.nextInt(256);
		
		// compute average of deterministic colors of surrounding carpet color (poor man's convolution)
		random = AthsRandom.getRandom(x+1, z);
		rgb += random.nextInt(256);
		
		random = AthsRandom.getRandom(x-1, z);
		rgb += random.nextInt(256);
		
		random = AthsRandom.getRandom(x, z+1);
		rgb += random.nextInt(256);
		
		random = AthsRandom.getRandom(x, z-1);
		rgb += random.nextInt(256);
		rgb /= 6;
			
		return  Math.min((int)(rgb * redMult) + redShift, 255) << 16 | 
				Math.min((int)(rgb * greenMult) + greenShift, 255) << 8 | 
				Math.min((int)(rgb * blueMult) + blueShift, 255);
	}
	
	@Override
	public BlockPlant setExtraNames(String name) {
		setNames(new String[] {AthsGlobal.MOSS_CARPET, AthsGlobal.MOSS_CARPET + "_Thick", AthsGlobal.MOSS_CARPET + "_Sparse"});
		setKeyName(name);
		return this;
	}
	
	public BlockPlantCarpet setColorRange(float redMult, int redShift, float greenMult, int greenShift, float blueMult, int blueShift) {
		this.redMult = redMult;
		this.redShift = redShift;
		this.greenMult = greenMult;
		this.greenShift = greenShift;
		this.blueMult = blueMult;
		this.blueShift = blueShift;
		return this; 
	}
}
