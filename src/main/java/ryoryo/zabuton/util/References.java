package ryoryo.zabuton.util;

public class References {
	public static final String MOD_ID = "zabuton";
	public static final String MOD_NAME = "Zabuton";

	public static final String MOD_VERSION_MAJOR = "GRADLE.VERSION_MAJOR";
	public static final String MOD_VERSION_MINOR = "GRADLE.VERSION_MINOR";
	public static final String MOD_VERSION_PATCH = "GRADLE.VERSION_PATCH";
	public static final String MOD_VERSION = MOD_VERSION_MAJOR + "." + MOD_VERSION_MINOR + "." + MOD_VERSION_PATCH;

	public static final String MOD_DEPENDENCIES = "required-after:forge@[14.23.5.2768,);"
			// + "required-after:polishedlib@[1.0.2,);"
			+ "required-after:polishedlib;";

	public static final String MOD_ACCEPTED_MC_VERSIONS = "[1.12.2]";

	public static final String PROXY_CLIENT = "ryoryo.zabuton.proxy.ClientProxy";
	public static final String PROXY_COMMON = "ryoryo.zabuton.proxy.CommonProxy";

	public static final int ENTITY_ID_ZABUTON = 0;
}