package mendedminecarts;

import mendedminecarts.settings.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.TranslatableText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MendedMinecartsMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("mendedminecarts");
	//TODO USE MENDEDMINECARTS as name with bandaid logo

	public static final int SETTING_VERSION = 6;
	public static final String SETTING_COMMAND = "mendedminecarts";
	public static final double DATA_RENDER_DISTANCE_SQ = 25d * 25d;

	public static final List<Setting> SETTINGS = new ArrayList<>();
	public static final List<Setting> FLAT_SETTINGS = new ArrayList<>();
	private static final String CONFIG_FILE = "./config/mendedminecarts.properties";

	public static <T extends Setting> T addSetting(T setting) {
		SETTINGS.add(setting);
		if (setting instanceof SettingGroup group) {
			FLAT_SETTINGS.addAll(Arrays.asList(group.getChildren()));
		} else {
			FLAT_SETTINGS.add(setting);
		}

		return setting;
	}

	public static final BooleanSetting DISPLAY_CART_DATA_POS = new BooleanSetting("Position", false, new TranslatableText("mendedminecarts.display_cart_data.position.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_VELOCITY = new BooleanSetting("Velocity", false, new TranslatableText("mendedminecarts.display_cart_data.velocity.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_SPEED = new BooleanSetting("Speed", false, new TranslatableText("mendedminecarts.display_cart_data.speed.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_FILL_LEVEL = new BooleanSetting("FillLevel", false, new TranslatableText("mendedminecarts.display_cart_data.fill_level.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_ON_GROUND = new BooleanSetting("OnGround", false, new TranslatableText("mendedminecarts.display_cart_data.on_ground.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_IN_WATER = new BooleanSetting("InWater", false, new TranslatableText("mendedminecarts.display_cart_data.in_water.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_SLOWDOWN_RATE = new BooleanSetting("SlowdownRate", false, new TranslatableText("mendedminecarts.display_cart_data.slowdown_rate.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_ESTIMATED_DISTANCE = new BooleanSetting("EstimatedDistance", false, new TranslatableText("mendedminecarts.display_cart_data.estimated_distance.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_BOX = new BooleanSetting("BoundingBox", false, new TranslatableText("mendedminecarts.display_cart_data.bounding_box.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_HOPPER_CART_LOCKED = new BooleanSetting("HopperLocked", false, new TranslatableText("mendedminecarts.display_cart_data.hopper_locked.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_WOBBLE = new BooleanSetting("Wobble", false, new TranslatableText("mendedminecarts.display_cart_data.wobble.description"));
	public static final IntegerSetting DISPLAY_CART_DATA_PRECISION = new IntegerSetting("DataPrecision", 4, new TranslatableText("mendedminecarts.data_precision.description"));


	public static final SettingGroup DISPLAY_CART_DATA = addSetting(
			new SettingGroup("DisplayCartData", false, new TranslatableText("mendedminecarts.display_cart_data.description"))
					.children(
							DISPLAY_CART_DATA_BOX,
							DISPLAY_CART_DATA_POS,
							DISPLAY_CART_DATA_VELOCITY,
							DISPLAY_CART_DATA_SPEED,
							DISPLAY_CART_DATA_FILL_LEVEL,
							DISPLAY_CART_DATA_ON_GROUND,
							DISPLAY_CART_DATA_IN_WATER,
							DISPLAY_CART_DATA_SLOWDOWN_RATE,
							DISPLAY_CART_DATA_ESTIMATED_DISTANCE,
							DISPLAY_CART_DATA_HOPPER_CART_LOCKED,
							DISPLAY_CART_DATA_WOBBLE,
							DISPLAY_CART_DATA_PRECISION
					)
	);

	public static final BooleanSetting ACCURATE_CLIENT_MINECARTS = addSetting(new BooleanSetting("AccurateClientMinecarts", false, new TranslatableText("mendedminecarts.accurate_client_minecarts.description")));
	public static final BooleanSetting NO_CLIENT_CART_INTERPOLATION = addSetting(new BooleanSetting("NoClientCartInterpolation", false, new TranslatableText("mendedminecarts.no_cart_interpolation.description")));
	public static final BooleanSetting ALWAYS_SYNC_CART_POSITION = addSetting(new BooleanSetting("AlwaysSyncCartPosition", false, new TranslatableText("mendedminecarts.always_sync_cart_position.description")));
	public static final DoubleSetting CART_SPEED = addSetting(new DoubleSetting("CartSpeed", 8d / 20d, new TranslatableText("mendedminecarts.custom_cart_speed.description"), 0d, 5000d));
	public static final BooleanSetting DERAILING_CART_FIX = addSetting(new BooleanSetting("DerailingCartFix", false, new TranslatableText("mendedminecarts.derailing_cart_fix.description")));
	//	public static BooleanSetting DERAILING_CART_FIX_DEMO = new BooleanSetting("DerailingCartFixDemo", false, new TranslatableText("mendedminecarts.derailing_cart_fix_demo.description")));
	public static final BooleanSetting ROTATE_CART_TO_RAIL = addSetting(new BooleanSetting("RotateCartToRail", false, new TranslatableText("mendedminecarts.rotate_cart_to_rail.description")));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		this.loadSettings(new File(CONFIG_FILE));
	}

	private void loadSettings(File file) {
		if (file.exists()) {
			Properties properties = new Properties();
			try (FileInputStream fileInputStream = new FileInputStream(file)) {
				properties.load(fileInputStream);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			for (Setting setting : SETTINGS) {
                try {
                    setting.loadFromProperties(properties, "");
                } catch (Exception e) {
                    LOGGER.error("Setting " + setting.getName() + " could not be read from config");
                }
            }
		}
	}

	public static void saveSettings() {
		Properties properties = new Properties();
		for (Setting setting : SETTINGS) {
			setting.writeToProperties(properties, "");
		}
		File file = new File(CONFIG_FILE);
		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			properties.store(fileOutputStream, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
