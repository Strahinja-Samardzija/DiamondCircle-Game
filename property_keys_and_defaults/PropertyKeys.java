package property_keys_and_defaults;

import java.io.File;

public class PropertyKeys {

    public static final String LOG_DIR_KEY = "Log_DIR";
    public static final String N_HOLES_KEY = "N_Holes";
    public static final String IMAGES_DIR_KEY = "Images_DIR";
    public static final String RESULTS_DIR_KEY = "Results_DIR";
    public static final String LOG_DIR_DEFAULT = "." + File.separator + "log" + File.separator;
    public static final String CONFIG_DEFAULT_CONFIG_PROPERTIES = "./config/default_config.properties";
    public static final String CONFIG_USER_CONFIG_PROPERTIES = "config/user_config.properties";
    public static final String DEFAULT_TXT_READER_KEY = "TXT_Reader_EXE";

    private PropertyKeys() {}
    
}
