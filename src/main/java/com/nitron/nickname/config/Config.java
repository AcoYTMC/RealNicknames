package com.nitron.nickname.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class Config extends MidnightConfig{
    public static final String MAIN = "main";

    @Entry(category = MAIN) public static int maxNick = 32;
}
