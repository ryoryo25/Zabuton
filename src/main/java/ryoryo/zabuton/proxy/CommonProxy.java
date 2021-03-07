package ryoryo.zabuton.proxy;

import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ryoryo.polishedlib.util.enums.EnumColor;
import ryoryo.zabuton.Zabuton;
import ryoryo.zabuton.entity.EntityZabuton;
import ryoryo.zabuton.handler.DispenseZabutonHandler;
import ryoryo.zabuton.item.ModItems;
import ryoryo.zabuton.util.References;

public class CommonProxy {
	public void preInit(FMLPreInitializationEvent event) {
		ModItems.init();
		Zabuton.REGISTER.registerModEntity(EntityZabuton.class, "zabuton", References.ENTITY_ID_ZABUTON, Zabuton.INSTANCE, 80, 3, true);

		// Dispenser Registry
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ModItems.ITEM_ZABUTON, new DispenseZabutonHandler());
	}

	public void init(FMLInitializationEvent event) {
		for (int i = 0; i < EnumColor.getLength(); i ++) {
			addRecipe("zabuton_" + EnumColor.byDyeDamage(i).getName(), new ItemStack(ModItems.ITEM_ZABUTON, 1, i), "s ", "WW", 's', Items.STRING, 'W', new ItemStack(Blocks.WOOL, 1, EnumColor.byDyeDamage(i).getWoolNumber()));
		}
	}

	public void postInit(FMLPostInitializationEvent event) {}

	public void loadComplete(FMLLoadCompleteEvent event) {}

	public static void addRecipe(String name, ItemStack output, Object... params) {
		Zabuton.REGISTER.addRecipe(name, output, params);
	}
}