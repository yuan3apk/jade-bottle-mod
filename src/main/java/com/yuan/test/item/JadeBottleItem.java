package com.yuan.test.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * 玉净瓶：功能类似水桶，但可以无限地装水和倒水。
 * <ul>
 *     <li>装水：右键点击水源方块，把它吸走（瓶子永远是满的，不会消耗）。</li>
 *     <li>倒水：右键点击其它方块，在点击面的相邻位置放置一个水源方块。</li>
 *     <li>吸水：长按右键，持续吸走准心及其周围（半径 {@link #ABSORB_RADIUS} 格）的水源。</li>
 * </ul>
 *
 * <p>注意：装水和倒水都放在 {@link #use} 里处理，不要用 {@code useOnBlock}。
 * 水不是固体方块，右键点水时若准星后面还有方块，游戏会先走 {@code useOnBlock}；
 * 一旦它返回成功，{@code use} 就不会被调用，导致装不上水。</p>
 */
public class JadeBottleItem extends Item {
	/** 吸水的最远距离：准心对准的水源必须在此范围内（格）。 */
	private static final double ABSORB_RANGE = 32.0;
	/** 每次吸水的半径（格）。 */
	private static final int ABSORB_RADIUS = 3;

	public JadeBottleItem(Settings settings) {
		super(settings);
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		// 允许长按右键持续使用，吸水逻辑放在 usageTick 里逐帧执行。
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		// 吸水：探测准心所指的水源（32格内），命中就进入长按吸水状态。
		// 水不是固体方块，右键点水时走 use() 而不是 useOnBlock()。
		BlockHitResult waterHit = raycastWater(world, user);
		if (waterHit.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = waterHit.getBlockPos();
			if (world.getFluidState(pos).isIn(FluidTags.WATER)) {
				user.setCurrentHand(hand);
				user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
				return TypedActionResult.consume(stack);
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

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		// 只在服务端改世界，客户端只做动画/预测，避免双重修改。
		if (world.isClient || !(user instanceof PlayerEntity player)) {
			return;
		}

		BlockHitResult waterHit = raycastWater(world, player);
		if (waterHit.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockPos center = waterHit.getBlockPos();
		if (!world.getFluidState(center).isIn(FluidTags.WATER)) {
			return;
		}

		absorbWaterAround(world, center);
	}

	/** 从玩家视线出发，向前 {@link #ABSORB_RANGE} 格探测水源方块。 */
	private static BlockHitResult raycastWater(World world, PlayerEntity user) {
		Vec3d start = user.getCameraPosVec(1.0F);
		Vec3d end = start.add(user.getRotationVec(1.0F).multiply(ABSORB_RANGE));
		return world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, user));
	}

	/** 吸走 center 周围（半径 {@link #ABSORB_RADIUS} 的球体内）的所有水方块。 */
	private static void absorbWaterAround(World world, BlockPos center) {
		int r = ABSORB_RADIUS;
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				for (int dz = -r; dz <= r; dz++) {
					if (dx * dx + dy * dy + dz * dz > r * r) {
						continue;
					}
					pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					if (world.getBlockState(pos).isOf(Blocks.WATER)) {
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
					}
				}
			}
		}
	}
}
