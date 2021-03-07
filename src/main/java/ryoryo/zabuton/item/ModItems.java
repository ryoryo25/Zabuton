package ryoryo.zabuton.item;

import net.minecraft.item.Item;
import ryoryo.zabuton.Zabuton;

public class ModItems {

	public static final Item ITEM_ZABUTON = new ItemZabuton();

	public static void init() {
		Zabuton.REGISTER.registerItem(ITEM_ZABUTON, "zabuton", 16);
	}
}