package cartmod;

import net.fabricmc.api.ModInitializer;
import cartmod.settings.Setting;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CartMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");

	public static final String SETTING_COMMAND = "cartmod";

	public static final List<Setting> SETTINGS = new ArrayList<>();

	public static Setting addSetting(Setting setting) {
		SETTINGS.add(setting);
		return setting;
	}

	public static Setting ACCURATE_CLIENT_MINECARTS = addSetting(new Setting("AccurateClientMinecarts", false, new TranslatableText("cartmod.accurate_client_minecarts.description")));
	public static Setting NO_CLIENT_CART_INTERPOLATION = addSetting(new Setting("NoClientCartInterpolation", false, new TranslatableText("cartmod.no_cart_interpolation.description")));
	public static Setting ALWAYS_SYNC_CART_POSITION = addSetting(new Setting("AlwaysSyncCartPosition", false, new TranslatableText("cartmod.always_sync_cart_position.description")));
	public static Setting DISPLAY_CART_POSITION = addSetting(new Setting("DisplayCartPosition", false, new TranslatableText("cartmod.display_cart_position.description")));
	public static Setting DISPLAY_CART_DATA = addSetting(new Setting("DisplayCartData", false, new TranslatableText("cartmod.display_cart_data.description")));
	//Issue: Needs client side code

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	}
}
