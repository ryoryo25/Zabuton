package ryoryo.zabuton.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelZabuton extends ModelBase {
	public ModelRenderer zabuton;

	public ModelZabuton() {
		zabuton = new ModelRenderer(this, 0, 0);
		zabuton.addBox(-6, -3, -6, 12, 3, 12);
	}

	@Override
	public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		zabuton.render(scale);
	}
}
