package com.facetorched.tfcaths.blocks;

import java.util.ArrayList;
import java.util.Random;

import com.dunk.tfc.Blocks.BlockTerra;
import com.dunk.tfc.Core.TFCTabs;
import com.dunk.tfc.api.TFCOptions;
import com.facetorched.tfcaths.AthsBlockSetup;
import com.facetorched.tfcaths.AthsGlobal;
import com.facetorched.tfcaths.util.AthsParser;
import com.facetorched.tfcaths.util.Point3D;
import com.facetorched.tfcaths.util.Point3DD;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockIronFence extends BlockIronBars{
	protected IIcon[] icons;
	
	public BlockIronFence() {
		super(Material.metal);
		this.setHardness(1F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(TFCTabs.TFC_BUILDING_BLOCKS);
		this.setStepSound(Block.soundTypeMetal);
  }
