package me.joshua.crudetechmod.Blocks;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Init.ModTileEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class FurnaceGeneratorBlock extends Block{
	public FurnaceGeneratorBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntityTypes.FURNACE_GENERATOR.get().create();
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult result) {
		if(!worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if(tile instanceof FurnaceGeneratorTileEntity) {
				ServerPlayerEntity spe = (ServerPlayerEntity) player;
				FurnaceGeneratorTileEntity gen = (FurnaceGeneratorTileEntity) tile;
				if(spe.isSneaking()) {
					gen.updateNegPos(gen.getPos());
				}
				else {
					NetworkHooks.openGui(spe, gen, pos);
				}
			}
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		CrudeTechMod.log("onReplace");
		if(state.getBlock() != newState.getBlock()) {
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof FurnaceGeneratorTileEntity) {
				InventoryHelper.dropItems(worldIn, pos, ((FurnaceGeneratorTileEntity) te).getItems());
			}
		}
	}
}