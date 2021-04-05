package ryoryo.zabuton.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import ryoryo.zabuton.client.model.ModelZabuton;
import ryoryo.zabuton.entity.EntityZabuton;
import ryoryo.zabuton.util.References;

public class RenderZabuton extends Render<EntityZabuton> {
	protected ModelBase baseZabuton;
	protected static final ResourceLocation[] textures = new ResourceLocation[] {
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_black.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_red.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_green.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_brown.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_blue.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_purple.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_cyan.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_light_gray.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_gray.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_pink.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_lime.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_yellow.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_light_blue.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_magenta.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_orange.png"),
			new ResourceLocation(References.MOD_ID, "textures/entity/zabuton_white.png")
	};

	protected RenderZabuton(RenderManager renderManager) {
		super(renderManager);
		shadowSize = 0.0F;
		baseZabuton = new ModelZabuton();
	}

	@Override
	public void doRender(EntityZabuton entityzabuton, double x, double y, double z, float entityYaw, float partialTicks) {
		if (entityzabuton.color >= 0 && entityzabuton.color < 16) {
			shadowSize = 0.5F;
			// レンダリング実装
			// レンダリング
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x, (float) y, (float) z);
			GL11.glRotatef(180F - entityYaw, 0.0F, 1.0F, 0.0F);

			bindEntityTexture(entityzabuton);
			GL11.glScalef(-1F, -1F, 1.0F);
			baseZabuton.render(entityzabuton, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			GL11.glPopMatrix();
		} else {
			// Entityがスポーン後、サーバから色情報を取得するまで描画しない。どの色で描画すればいいかわからないため
			shadowSize = 0.0F;
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityZabuton entity) {
		return textures[entity.color & 0x0F];
	}

	public static class Factory implements IRenderFactory<EntityZabuton> {
		@Override
		public Render<? super EntityZabuton> createRenderFor(RenderManager manager) {
			return new RenderZabuton(manager);
		}
	}
}
