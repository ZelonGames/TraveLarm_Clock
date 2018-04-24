package zelongames.travelarm_clock.Helpers;

import android.os.Build;

public final class VersionHelper {
    public static final boolean isVersionLolipopOrLater(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
