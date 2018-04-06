package zelongames.travelarm_clock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogHelper {

    public static AlertDialog.Builder createDialogBuilder(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(message)
                .setTitle(title);

        return alertDialogBuilder;
    }

    public static Dialog createPositiveNegativeDialog(
            Context context, String title, String message, String positiveBtnText, String negativeBtnText,
            DialogInterface.OnClickListener positiveOnClick, DialogInterface.OnClickListener negativeOnClick) {
        AlertDialog.Builder builder = createDialogBuilder(context, title, message);

        builder.setPositiveButton(positiveBtnText, positiveOnClick);
        builder.setNegativeButton(negativeBtnText, negativeOnClick);

        Dialog dialog = builder.create();
        dialog.setCancelable(false);

        return dialog;
    }
}
