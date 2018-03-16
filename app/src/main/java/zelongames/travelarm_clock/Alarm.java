package zelongames.travelarm_clock;

public class Alarm {

    private String name = "";

    public String getName() {
        return name;
    }

    private String location = null;

    public String getLocation() {
        return location;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    private boolean isEnabled = false;

    public Alarm(String name, String location) {
        this.name = name;
        this.location = location;
    }
}
