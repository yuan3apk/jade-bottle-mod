package com.yuan.test.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
 * 玉净瓶：功能类似水桶，但可以无限地装液体和倒液体，液体种类由构造参数决定。
 * <ul>
 *     <li>装液体：单击液体源方块，只吸走准心这一格（瓶子永远是满的，不会消耗）。</li>
 *     <li>倒液体：右键点击空炼药锅会把它装满；点击其它方块则在点击面的相邻位置放置一个液体源方块。</li>
 *     <li>装锅液体：右键点击装着同种液体的炼药锅，把锅里的液体吸进瓶子（锅变空）。</li>
 *     <li>吸取：长按右键超过 {@link #HOLD_TICKS} 刻，持续吸走准心及其周围（半径 {@link #ABSORB_RADIUS} 格）的液体源。</li>
 * </ul>
 *
 * <p>注意：装液体和倒液体都放在 {@link #use} 里处理，不要用 {@code useOnBlock}。
 * 液体不是固体方块，右键点液体时若准星后面还有方块，游戏会先走 {@code useOnBlock}；
 * 一旦它返回成功，{@code use} 就不会被调用，导致装不上液体。</p>
 */
public class JadeBottleItem extends Item {
	/** 吸取的最远距离：准心对准的液体源必须在此范围内（格）。 */
	private static final double ABSORB_RANGE = 32.0;
	/** 每次吸取的半径（格）。 */
	private static final int ABSORB_RADIUS = 3;
	/** 长按多少刻后才启动范围吸取（短按只吸准心那一格）。 */
	private static final int HOLD_TICKS = 5;

	/** 瓶子装的液体：{@link Fluids#WATER} 或 {@link Fluids#LAVA}。 */
	private final Fluid fluid;
	/** 该液体对应的液体方块（水源/岩浆源）。 */
	private final Block fluidBlock;
	/** 该液体对应的炼药锅状态；空锅与满锅。 */
	private final BlockState fullCauldron;
	private final BlockState emptyCauldron;

	public JadeBottleItem(Settings settings) {
		this(settings, Fluids.WATER);
	}

	public JadeBottleItem(Settings settings, Fluid fluid) {
		super(settings);
		this.fluid = fluid;
		this.fluidBlock = fluid.getDefaultState().getBlockState().getBlock();
		this.fullCauldron = cauldronFor(fluid);
		this.emptyCauldron = this.fullCauldron == null ? null : Blocks.CAULDRON.getDefaultState();
	}

	/** 液体对应的满炼药锅状态；不支持的液体返回 null。 */
	public static BlockState cauldronFor(Fluid fluid) {
		if (fluid == Fluids.WATER) {
			return Blocks.WATER_CAULDRON.getDefaultState().with(LeveledCauldronBlock.LEVEL, LeveledCauldronBlock.MAX_LEVEL);
		}
		if (fluid == Fluids.LAVA) {
			return Blocks.LAVA_CAULDRON.getDefaultState();
		}
		return null;
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		// 允许长按右键持续使用，吸取逻辑放在 usageTick 里逐帧执行。
		return 72000;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);

		// 装液体：探测准心所指的液体源（32格内）。
		// 单击只吸走准心这一格；继续按住则进入长按吸取（见 usageTick）。
		// 液体不是固体方块，右键点液体时走 use() 而不是 useOnBlock()。
		BlockHitResult fluidHit = raycastFluid(world, user);
		if (fluidHit.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = fluidHit.getBlockPos();
			if (world.getFluidState(pos).getFluid() == fluid) {
				if (world.getBlockState(pos).isOf(fluidBlock)) {
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
				}
				user.setCurrentHand(hand);
				user.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
				return TypedActionResult.consume(stack);
			}
		}

		// 倒液体：没有命中液体源时，探测固体方块。
		BlockHitResult blockHit = Item.raycast(world, user, RaycastContext.FluidHandling.NONE);
		if (blockHit.getType() == HitResult.Type.BLOCK) {
			BlockPos hitPos = blockHit.getBlockPos();
			BlockState hitState = world.getBlockState(hitPos);

			// 炼药锅：空锅倒满；锅里有同种液体则把液体装走。别的液体不动锅（比如水瓶不碰岩浆锅）。
			// 这里必须进入“使用中”状态，否则按住右键会每 4 刻重复触发，把锅来回切换。
			if (fullCauldron != null) {
				if (hitState.isOf(Blocks.CAULDRON)) {
					world.setBlockState(hitPos, fullCauldron, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
					world.playSound(user, hitPos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
					user.setCurrentHand(hand);
					return TypedActionResult.consume(stack);
				}
				if (hitState.isOf(fullCauldron.getBlock())) {
					world.setBlockState(hitPos, emptyCauldron, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
					world.playSound(user, hitPos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					user.setCurrentHand(hand);
					return TypedActionResult.consume(stack);
				}
			}

			// 否则在点击面的相邻位置放一个液体源方块。
			BlockPos placePos = hitPos.offset(blockHit.getSide());
			BlockState placeState = world.getBlockState(placePos);
			if (placeState.isAir() || placeState.isReplaceable()) {
				world.setBlockState(placePos, fluid.getDefaultState().getBlockState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
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

		// 短按（未超过 HOLD_TICKS）不触发范围吸取：那一格已在 use() 里吸掉。
		if (user.getItemUseTime() < HOLD_TICKS) {
			return;
		}

		BlockHitResult fluidHit = raycastFluid(world, player);
		if (fluidHit.getType() != HitResult.Type.BLOCK) {
			return;
		}
		BlockPos center = fluidHit.getBlockPos();
		if (world.getFluidState(center).getFluid() != fluid) {
			return;
		}

		absorbFluidAround(world, center);
	}

	/** 从玩家视线出发，向前 {@link #ABSORB_RANGE} 格探测液体源方块。 */
	private static BlockHitResult raycastFluid(World world, PlayerEntity user) {
		Vec3d start = user.getCameraPosVec(1.0F);
		Vec3d end = start.add(user.getRotationVec(1.0F).multiply(ABSORB_RANGE));
		return world.raycast(new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.SOURCE_ONLY, user));
	}

	/** 吸走 center 周围（半径 {@link #ABSORB_RADIUS} 的球体内）的所有本种液体方块。 */
	private void absorbFluidAround(World world, BlockPos center) {
		int r = ABSORB_RADIUS;
		BlockPos.Mutable pos = new BlockPos.Mutable();
		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				for (int dz = -r; dz <= r; dz++) {
					if (dx * dx + dy * dy + dz * dz > r * r) {
						continue;
					}
					pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
					if (world.getBlockState(pos).isOf(fluidBlock)) {
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
					}
				}
			}
		}
	}
}
