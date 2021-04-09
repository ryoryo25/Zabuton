package ryoryo.zabuton.item;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ryoryo.polishedlib.item.ItemBaseMeta;
import ryoryo.polishedlib.util.RegistryUtils;
import ryoryo.polishedlib.util.Utils;
import ryoryo.polishedlib.util.enums.EnumColor;
import ryoryo.polishedlib.util.handlers.ModelHandler;
import ryoryo.polishedlib.util.interfaces.IItemColorProvider;
import ryoryo.zabuton.entity.EntityZabuton;

public class ItemZabuton extends ItemBaseMeta implements IItemColorProvider {

	public ItemZabuton() {
		super("zabuton", CreativeTabs.DECORATIONS);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		float one = 1.0F;
		float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * one;
		float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * one;
		double d = player.prevPosX + (player.posX - player.prevPosX) * (double) one;
		double d1 = (player.prevPosY + (player.posY - player.prevPosY) * (double) one + 1.6200000000000001D) - (double) player.getYOffset();
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) one;
		Vec3d vec3d = new Vec3d(d, d1, d2);
		float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.141593F);
		float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.141593F);
		float f5 = -MathHelper.cos(-f1 * 0.01745329F);
		float f6 = MathHelper.sin(-f1 * 0.01745329F);
		float f7 = f4 * f5;
		float f8 = f6;
		float f9 = f3 * f5;
		double d3 = 5D;
		Vec3d vec3d1 = vec3d.addVector((double) f7 * d3, (double) f8 * d3, (double) f9 * d3);
		RayTraceResult result = world.rayTraceBlocks(vec3d, vec3d1, true);
		if (result == null) {
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, stack);
		}
		if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos pos = result.getBlockPos();
			if (world.getBlockState(pos.up()).getMaterial() == Material.AIR) {
				if (!world.isRemote) {
					EntityZabuton zabuton = new EntityZabuton(world, (float) pos.getX() + 0.5F, (float) pos.getY() + 1.0F, (float) pos.getZ() + 0.5F, (byte) (stack.getItemDamage() & 0x0f));
					// 方向ぎめはここに入れる
					zabuton.rotationYaw = (MathHelper.floor((double) ((player.rotationYaw * 4F) / 360F) + 2.50D) & 3) * 90;
					world.spawnEntity(zabuton);
				}

				if (!Utils.isCreative(player)) {
					stack.shrink(1);
				}
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
	}

	@Override
	public String getUnlocalizedNameImpl(ItemStack stack) {
		return this.getUnlocalizedName() + "_" + EnumColor.NAMES_DYE[stack.getItemDamage()];
	}

	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		return EnumColor.byDyeDamage(stack.getItemDamage()).getColorValue();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItemsImpl(CreativeTabs tab, NonNullList<ItemStack> items) {
		RegistryUtils.registerSubItems(this, EnumColor.getLength(), tab, items);
	}

	@Override
	public void registerModelsImpl() {
		ModelHandler.registerItemModel(this, EnumColor.getLength());
	}
}
