package controllers;

public final class GlobalConstants {
    private GlobalConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String INDEXOUTOFBOUNDEXCEPETION = "IndexOutOfBoundsException";
    public static final String NULLPOINTEREXCEPTION = "NullPointerException";
    public static final String NORESULTEXCEPTION = "NoResultException";
    public static final int CACHE_DURATION = 10;
    public static final int LONG_CACHE_DURATION = 90000;
}
