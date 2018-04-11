package controllers.importutilities;

public final class UrlLinks {
    private static long RACEID;
    private static final String BASE_URL = "https://tlng.cnlab.ch/";
    public static final String RACE = BASE_URL + "api/getAllGlobalSettings";
    public static final String RIDERS = BASE_URL + "api/masterdata/riders/stage/";
    public static final String STAGES = BASE_URL + "public/stages/";
    public static final String JUDGEMENTS = BASE_URL + "api/masterdata/judgements/";
    public static final String STATES = BASE_URL + "json_public/status.php";
    public static final String GLOBALSETTINGS = BASE_URL + "api/getAllGlobalSettings";
    public static final String MAILLOTS = BASE_URL + "api/masterdata/jerseys/race/";
    public static final String RIDERJERSEY = BASE_URL + "json_public/riderjerseystartstage.php?stage=";
    private UrlLinks() { throw new IllegalStateException("Static class"); }
    public static void setRaceId(long raceId){ RACEID = raceId;}
    public static long getRaceId(){return RACEID;}
}
