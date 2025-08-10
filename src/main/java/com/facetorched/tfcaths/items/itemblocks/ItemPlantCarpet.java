package com.facetorched.tfcaths.items.itemblocks;

import com.facetorched.tfcaths.blocks.BlockPlantCarpet;
import com.facetorched.tfcaths.enums.EnumVary;
import com.facetorched.tfcaths.util.AthsLogger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemPlantCarpet extends ItemPlantLilyPad{

	public ItemPlantCarpet(Block b) {
		super(b);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack is, int par2)
	{
		try {
			BlockPlantCarpet carpet = ((BlockPlantCarpet)this.field_150939_a);
			if(carpet.isVary(is.getItemDamage(), EnumVary.SNOW))return super.getColorFromItemStack(is, par2);
			int rgb = 128;
			return Math.min((int)(rgb * carpet.redMult) + carpet.redShift, 255) << 16 | 
					Math.min((int)(rgb * carpet.greenMult) + carpet.greenShift, 255) << 8 | 
					Math.min((int)(rgb * carpet.blueMult) + carpet.blueShift, 255);
		}
		catch (ClassCastException e) {
			AthsLogger.error("Unable to get carpet color");
		}
		return 0;
	}

}
