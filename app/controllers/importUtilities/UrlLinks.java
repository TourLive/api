package controllers.importUtilities;

public final class UrlLinks {
    public static final String RIDERS = "api/masterdata/riders/stage/";
    public static final String STAGES = "public/stages/";
    public static final String JUDGEMENTS = "api/masterdata/judgements/";
    public static final String STATES = "json_public/status.php";
    public static final String GLOBALSETTINGS = "api/getAllGlobalSettings";
    public static final String MAILLOTS = "api/masterdata/jerseys/race/";
    public static final String RIDERJERSEY = "json_public/riderjerseystartstage.php?stage=";
    private UrlLinks() { throw new IllegalStateException("Static class"); }
}
