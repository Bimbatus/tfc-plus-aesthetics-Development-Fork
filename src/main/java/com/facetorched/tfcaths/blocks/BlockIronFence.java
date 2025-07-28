package net.minecraft.block.Blocks;

public class BlockIronFence extends BlockIronBars{
	protected IIcon[] icons;
	
	public BlockIronFence() {
		super(Material.metal);
		this.setHardness(1F);
		this.setHarvestLevel("pickaxe", 0);
		this.setCreativeTab(TFCTabs.TFC_BUILDING_BLOCKS);
		this.setStepSound(Block.soundTypeMetal);
  }
