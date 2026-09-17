package com.yuan.test.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * 玉净瓶：功能类似水桶，但可以无限地装水和倒水。
 * <ul>
 *     <li>装水：右键点击水源方块，把它吸走（瓶子永远是满的，不会消耗）。</li>
 *     <li>倒水：右键点击其它方块，在点击面的相邻位置放置一个水源方块。</li>
 * </ul>
 *
 * <p>注意：装水和倒水都放在 {@link #use} 里处理，不要用 {@code useOnBlock}。
 * 水不是固体方块，右键点水时若准星后面还有方块，游戏会先走 {@code useOnBlock}；
 * 一旦它返回成功，{@code use} 就不会被调用，导致装不上水。</p>
 */
public class JadeBottleItem extends Item {
	public JadeBottleItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		// 装水：水不是固体方块，右键点水时走 use() 而不是 useOnBlock()。
		// 因此这里主动探测准星所指的水源方块，命中就把它吸走。
		BlockHitResult waterHit = Item.raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
		if (waterHit.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = waterHit.getBlockPos();
			FluidState fluid = world.getFluidState(pos);
			if (fluid.isIn(FluidTags.WATER)) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
				user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
				return TypedActionResult.success(stack, world.isClient);
			}
		}

		// 倒水：没有命中水源时，探测固体方块，在点击面的相邻位置放一个水源方块。
		BlockHitResult blockHit = Item.raycast(world, user, RaycastContext.FluidHandling.NONE);
		if (blockHit.getType() == HitResult.Type.BLOCK) {
			BlockPos placePos = blockHit.getBlockPos().offset(blockHit.getSide());
			BlockState placeState = world.getBlockState(placePos);
			if (placeState.isAir() || placeState.isReplaceable()) {
				world.setBlockState(placePos, Fluids.WATER.getDefaultState().getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
				world.playSound(user, placePos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
				return TypedActionResult.success(stack, world.isClient);
			}
		}

		return TypedActionResult.pass(stack);
	}
}
