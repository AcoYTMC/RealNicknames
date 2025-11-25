package com.nitron.nickname;

import com.nitron.nickname.commands.NicknameCommand;
import com.nitron.nickname.config.Config;
import com.nitron.nitrogen.Nitrogen;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RealNickname implements ModInitializer {
	public static final String MOD_ID = "nickname";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Nitrogen.registerNitronMod(MOD_ID);
		MidnightConfig.init(MOD_ID, Config.class);
		CommandRegistrationCallback.EVENT.register(NicknameCommand::register);
	}
}
