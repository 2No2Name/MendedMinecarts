package net.fabricmc.example;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.example.settings.Setting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExampleMod implements ModInitializer {
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

	public static Setting ACCURATE_CLIENT_MINECARTS = addSetting(new Setting("AccurateClientMinecarts", true));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

	}
}
