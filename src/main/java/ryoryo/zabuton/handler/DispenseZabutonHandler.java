package ryoryo.zabuton.handler;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import ryoryo.zabuton.entity.EntityZabuton;

public class DispenseZabutonHandler extends BehaviorProjectileDispense {
	protected ItemStack zabuton;

	@Override
	public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
		// 色を識別するためにItemStackを確保
		this.zabuton = stack;
		return super.dispenseStack(source, stack);
	}

	@Override
	protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
		return new EntityZabuton(world, position.getX(), position.getY(), position.getZ(), (byte) this.zabuton.getItemDamage());
	}
}