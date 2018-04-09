package zelongames.travelarm_clock.Helpers;

import android.view.View;

public final class ViewHelper {

    public static void switchBetweenViews(View view1, View view2) {
        if (view1.getVisibility() == view2.getVisibility())
            return;

        final int view1Visibility = view1.getVisibility();
        final int view2Visibility = view2.getVisibility();

        if (view1.getVisibility() == View.VISIBLE) {
            view1.setVisibility(view2.getVisibility());
            view2.setVisibility(view1Visibility);
        } else if (view2.getVisibility() == View.VISIBLE) {
            view2.setVisibility(view1.getVisibility());
            view1.setVisibility(view2Visibility);
        }
    }
}
