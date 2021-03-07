package ryoryo.zabuton.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ryoryo.polishedlib.util.RegistryUtils;
import ryoryo.zabuton.client.render.RenderZabuton;
import ryoryo.zabuton.entity.EntityZabuton;
import ryoryo.zabuton.item.ItemZabuton;
import ryoryo.zabuton.item.ModItems;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		RegistryUtils.registerEntityRendering(EntityZabuton.class, new RenderZabuton.Factory());
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		RegistryUtils.registerItemColor(new ItemZabuton(), ModItems.ITEM_ZABUTON);
	}
}