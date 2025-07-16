package com.facetorched.tfcaths.blocks;

import java.util.ArrayList;
import java.util.Random;

import com.dunk.tfc.Blocks.BlockTerra;
import com.dunk.tfc.Core.TFCTabs;
import com.dunk.tfc.api.TFCOptions;
import com.facetorched.tfcaths.AthsBlockSetup;
import com.facetorched.tfcaths.AthsGlobal;
import com.facetorched.tfcaths.AthsMod;
import com.facetorched.tfcaths.WorldGen.Generators.AthsWorldGenCrystals;
import com.facetorched.tfcaths.WorldGen.Generators.CrystalSpawnData;
import com.facetorched.tfcaths.util.AthsParser;
import com.facetorched.tfcaths.util.Point3D;
import com.facetorched.tfcaths.util.Point3DD;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import net.minecraftforge.common.util.ForgeDirection;

public class BlockLichen extends BlockTerra{
	@SideOnly(Side.CLIENT)
	protected IIcon[] icons;
	
	public Item lichenItem;
	public String lichenName;
	public int[] lichenMetas;
	public boolean isTransparent;
	
	public BlockLichen() {
		super(Material.plant);
		this.setHardness(0.25F);
		this.setCreativeTab(TFCTabs.TFC_DECORATION);
		this.setStepSound(Block.soundTypePlant);
		this.isTransparent = false;
	}
	
	public static ForgeDirection getDirection(int meta) {
		return ForgeDirection.getOrientation(meta % 6);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)  
	{
		super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
		if(AthsParser.isHolding(world, player, "itemKnife")) {
			if(!world.isRemote) {
				dropBlockAsItem(world, x, y, z, new ItemStack(this, 1, 0));
				world.setBlock(x, y, z, Blocks.air, 0, 2);
			}
			else {
				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "dig.glass", 1.0F, world.rand.nextFloat() * 0.1F + 0.9F);
			}
		}
		
		if(TFCOptions.enableDebugMode){
			if (!world.isRemote) {
				LichenSpawnData data = new LichenSpawnData(AthsMod.MODID+":"+this.lichenName, AthsMod.MODID+":"+this.lichenName, new String[] {"All"}, 1, 1, 1);
				Point3D p = new Point3D(x, y, z);
				ArrayList<Point3D> points = AthsWorldGenLichen.getValidOpenings(p.add(AthsGlobal.NEIGHBORS), data, world);
				for(Point3D point : points) {
					AthsWorldGenLichen.placeLichen(point, data, world, new Random());
				}
			}
		}
		return false;
	}
	
    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z){
		this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}
	
	/**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z){
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z){
    	int meta = world.getBlockMetadata(x, y, z);
    	ForgeDirection d = getDirection(meta);
		Point3DD pt1 = new Point3DD(0,0,0).rotateToFace(d);
		Point3DD pt2 = new Point3DD(1,0.0625,1).rotateToFace(d);
		if(AthsParser.isNegativeDirection(d)) { // max and mins get flipped
			this.setBlockBounds((float)pt2.x, (float)pt2.y, (float)pt2.z, (float)pt1.x, (float)pt1.y, (float)pt1.z);
		}
		else {
			this.setBlockBounds((float)pt1.x, (float)pt1.y, (float)pt1.z, (float)pt2.x, (float)pt2.y, (float)pt2.z);
		}
    }
    
    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit. Args: world,
     * x, y, z, startVec, endVec
     */
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 v1, Vec3 v2)
    {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.collisionRayTrace(world, x, y, z, v1, v2);
    }
	
	@Override
	public boolean isOpaqueCube(){
		return false;
	}
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}
	@Override
	public int getRenderType(){
		return AthsBlockSetup.directionalLayerRenderID;
	}
	// World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
		
		//world.setBlockMetadataWithNotify(x, y, z, side, 2);
		return side;
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
		ForgeDirection d = ForgeDirection.getOrientation(side).getOpposite();
		if (world.isSideSolid(x + d.offsetX, y + d.offsetY, z + d.offsetZ, ForgeDirection.getOrientation(side))) {
			return super.canPlaceBlockOnSide(world, x, y, z, side);
		}
		return false;
	}
	
	@Override
	public boolean canBlockStay(World world, int x, int y, int z){
		ForgeDirection d = getDirection(world.getBlockMetadata(x, y, z)).getOpposite();
		return world.isSideSolid(x + d.offsetX, y + d.offsetY, z + d.offsetZ, d.getOpposite());
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block){
		this.checkAndDropBlock(world, x, y, z);
	}

	protected void checkAndDropBlock(World world, int x, int y, int z){
		if (!world.isRemote && !this.canBlockStay(world, x, y, z)){
			dropBlockAsItem(world, x, y, z, new ItemStack(this, 1, 0));
			world.setBlock(x, y, z, Blocks.air, 0, 2);
		}
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		checkAndDropBlock(world, x, y, z);
	}
	
	/**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderBlockPass(){
        return isTransparent ? 1 : 0;
    }
    
    @Override
    public Item getItemDropped(int meta, Random random, int fortune){
        return null; // can be null
    }
    
    @Override
    public int damageDropped(int meta){
    	if(lichenMetas == null) {
    		return 0;
    	}
    
    public BlockLichen setItem(Item item, int meta) {
    	return setItem(item, new int[] {meta});
    }
    
    public BlockLichen setItem(Item item, int[] metas) {
    	this.lichenItem = item;
    	this.lichenMetas = metas;
    	return this;
    
	
	public BlockLichen setName(String name) {
		this.lichenName = name;
		this.setBlockName(name);
		this.setBlockTextureName(AthsMod.MODID + ":plants/" + lichenName);
		return this;
	}
	
	public BlockLichen setIsTransparent() {
		this.isTransparent = true;
		return this;
	}
}
