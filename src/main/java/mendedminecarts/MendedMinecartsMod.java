package mendedminecarts;

import mendedminecarts.settings.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.Text;
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

	public static final int SETTING_VERSION = 9;
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

	public static final BooleanSetting DISPLAY_CART_DATA_POS = new BooleanSetting("Position", false, Text.translatable("mendedminecarts.display_cart_data.position.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_POS_BINARY = new BooleanSetting("BinaryPosition", false, Text.translatable("mendedminecarts.display_cart_data.binary_position.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_VELOCITY = new BooleanSetting("Velocity", false, Text.translatable("mendedminecarts.display_cart_data.velocity.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_SPEED = new BooleanSetting("Speed", false, Text.translatable("mendedminecarts.display_cart_data.speed.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_FILL_LEVEL = new BooleanSetting("FillLevel", false, Text.translatable("mendedminecarts.display_cart_data.fill_level.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_ON_GROUND = new BooleanSetting("OnGround", false, Text.translatable("mendedminecarts.display_cart_data.on_ground.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_IN_WATER = new BooleanSetting("InWater", false, Text.translatable("mendedminecarts.display_cart_data.in_water.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_SLOWDOWN_RATE = new BooleanSetting("SlowdownRate", false, Text.translatable("mendedminecarts.display_cart_data.slowdown_rate.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_ESTIMATED_DISTANCE = new BooleanSetting("EstimatedDistance", false, Text.translatable("mendedminecarts.display_cart_data.estimated_distance.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_BOX = new BooleanSetting("BoundingBox", false, Text.translatable("mendedminecarts.display_cart_data.bounding_box.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_ENTITY_PICKUP_VOLUME = new BooleanSetting("EntityPickupVolume", false, Text.translatable("mendedminecarts.display_cart_data.entity_pickup_box.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_HOPPER_PICKUP_VOLUME = new BooleanSetting("HopperPickupVolume", false, Text.translatable("mendedminecarts.display_cart_data.hopper_pickup_box.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_HOPPER_EXTRACT_VOLUME = new BooleanSetting("HopperExtractVolume", false, Text.translatable("mendedminecarts.display_cart_data.hopper_extract_volume.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_HOPPER_EXTRACT_BLOCK = new BooleanSetting("HopperExtractBlock", false, Text.translatable("mendedminecarts.display_cart_data.hopper_extract_block.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_HOPPER_CART_LOCKED = new BooleanSetting("HopperLocked", false, Text.translatable("mendedminecarts.display_cart_data.hopper_locked.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_WOBBLE = new BooleanSetting("Wobble", false, Text.translatable("mendedminecarts.display_cart_data.wobble.description"));
	public static final BooleanSetting DISPLAY_CART_DATA_RIDEABLE = new BooleanSetting("Rideable", false, Text.translatable("mendedminecarts.display_cart_data.is_rideable.description"));
	public static final IntegerSetting DISPLAY_CART_DATA_PRECISION = new IntegerSetting("DataPrecision", 4, Text.translatable("mendedminecarts.data_precision.description"));


	public static final SettingGroup DISPLAY_CART_DATA = addSetting(
			new SettingGroup("DisplayCartData", false, Text.translatable("mendedminecarts.display_cart_data.description"))
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
							DISPLAY_CART_DATA_ENTITY_PICKUP_VOLUME,
							DISPLAY_CART_DATA_HOPPER_CART_LOCKED,
							DISPLAY_CART_DATA_HOPPER_PICKUP_VOLUME,
							DISPLAY_CART_DATA_HOPPER_EXTRACT_VOLUME,
							DISPLAY_CART_DATA_HOPPER_EXTRACT_BLOCK,
							DISPLAY_CART_DATA_WOBBLE,
							DISPLAY_CART_DATA_RIDEABLE,
							DISPLAY_CART_DATA_PRECISION,
							DISPLAY_CART_DATA_POS_BINARY
					)
	);

	public static final BooleanSetting ACCURATE_CLIENT_BOATS = addSetting(new BooleanSetting("AccurateClientBoats", false, Text.translatable("mendedminecarts.accurate_client_boats.description")));
	public static final BooleanSetting ACCURATE_CLIENT_MINECARTS = addSetting(new BooleanSetting("AccurateClientMinecarts", false, Text.translatable("mendedminecarts.accurate_client_minecarts.description")));
	public static final BooleanSetting NO_CLIENT_CART_INTERPOLATION = addSetting(new BooleanSetting("NoClientCartInterpolation", false, Text.translatable("mendedminecarts.no_cart_interpolation.description")));
	public static final BooleanSetting ALWAYS_SYNC_CART_POSITION = addSetting(new BooleanSetting("AlwaysSyncCartPosition", false, Text.translatable("mendedminecarts.always_sync_cart_position.description")));
	public static final DoubleSetting CART_SPEED = addSetting(new DoubleSetting("CartSpeed", 8d / 20d, Text.translatable("mendedminecarts.custom_cart_speed.description"), 0d, 5000d));
	public static final BooleanSetting DERAILING_CART_FIX = addSetting(new BooleanSetting("DerailingCartFix", false, Text.translatable("mendedminecarts.derailing_cart_fix.description")));
	//	public static BooleanSetting DERAILING_CART_FIX_DEMO = new BooleanSetting("DerailingCartFixDemo", false, Text.translatable("mendedminecarts.derailing_cart_fix_demo.description")));
	public static final BooleanSetting ROTATE_CART_TO_RAIL = addSetting(new BooleanSetting("RotateCartToRail", false, Text.translatable("mendedminecarts.rotate_cart_to_rail.description")));
	public static final BooleanSetting EXPLODING_CART_FIX = addSetting(new BooleanSetting("ExplodeRailsFix", false, Text.translatable("mendedminecarts.explode_rail_fix.description")));
	public static final BooleanSetting NO_CART_ITEM_CAP = addSetting(new BooleanSetting("NoCartItemCap", false, Text.translatable("mendedminecarts.no_cart_item_cap.description")));

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
