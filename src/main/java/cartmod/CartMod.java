package cartmod;

import cartmod.settings.BooleanSetting;
import cartmod.settings.DoubleSetting;
import cartmod.settings.Setting;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CartMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("cartmod");
	//TODO USE MENDEDMINECARTS as name with bandaid logo

	public static final String SETTING_COMMAND = "cartmod";

	public static final List<Setting> SETTINGS = new ArrayList<>();

	public static <T extends Setting> T addSetting(T setting) {
		SETTINGS.add(setting);
		return setting;
	}

	public static BooleanSetting ACCURATE_CLIENT_MINECARTS = addSetting(new BooleanSetting("AccurateClientMinecarts", false, new TranslatableText("cartmod.accurate_client_minecarts.description")));
	public static BooleanSetting NO_CLIENT_CART_INTERPOLATION = addSetting(new BooleanSetting("NoClientCartInterpolation", false, new TranslatableText("cartmod.no_cart_interpolation.description")));
	public static BooleanSetting ALWAYS_SYNC_CART_POSITION = addSetting(new BooleanSetting("AlwaysSyncCartPosition", false, new TranslatableText("cartmod.always_sync_cart_position.description")));
	public static BooleanSetting DISPLAY_CART_POSITION = addSetting(new BooleanSetting("DisplayCartPosition", false, new TranslatableText("cartmod.display_cart_position.description")));
	public static BooleanSetting DISPLAY_CART_DATA = addSetting(new BooleanSetting("DisplayCartData", false, new TranslatableText("cartmod.display_cart_data.description")));
	public static DoubleSetting CUSTOM_RAIL_SPEED = addSetting(new DoubleSetting("CartSpeed", 8d / 20d, new TranslatableText("cartmod.custom_cart_speed.description")));
	public static BooleanSetting DERAILING_CART_FIX = addSetting(new BooleanSetting("DerailingCartFix", false, new TranslatableText("cartmod.derailing_cart_fix.description")));
	public static BooleanSetting DERAILING_CART_FIX_DEMO = addSetting(new BooleanSetting("DerailingCartFixDemo", false, new TranslatableText("cartmod.derailing_cart_fix_demo.description")));
	public static BooleanSetting ROTATE_CART_TO_RAIL = addSetting(new BooleanSetting("RotateCartToRail", false, new TranslatableText("cartmod.rotate_cart_to_rail.description")));
	//todo Issue: Needs client side code for client side rules

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	}
}
