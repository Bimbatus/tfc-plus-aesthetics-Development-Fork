package net.minecraft.block.Blocks;

public class BlockIronFence extends BlockIronBars{
	public BlockIronFence () {
		super();
		this.setIsWoody(); // has collision!
		this.setIsDamaging();
	}
}
