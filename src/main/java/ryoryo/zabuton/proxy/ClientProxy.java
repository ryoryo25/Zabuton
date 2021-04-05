package ryoryo.zabuton.proxy;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ryoryo.polishedlib.util.RegistryUtils;
import ryoryo.zabuton.client.render.RenderZabuton;
import ryoryo.zabuton.entity.EntityZabuton;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		RegistryUtils.registerEntityRendering(EntityZabuton.class, new RenderZabuton.Factory());
	}
}