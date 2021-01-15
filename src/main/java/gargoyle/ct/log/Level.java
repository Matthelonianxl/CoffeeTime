package gargoyle.ct.log;

public enum Level {
    TRACE("FINEST"),
    DEBUG("FINE"),
    WARNING("WARNING"),
    INFO("INFO"),
    ERROR("SEVERE");
    private final String level;

    Level(String level) {
        this.level = level;
    }

    String getLevel() {
        return level;
    }
}
