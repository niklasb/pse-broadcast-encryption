package cryptocast.client;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * This class is used to pop up an error message.
 */
public class MessageFragment extends DialogFragment {
    private String message;
    private DialogInterface.OnClickListener clickHandler;

    /**
     * Creates a new ErrorFragment which can be used to print the given error message.
     * @param message Error message describing the error which occured before this fragment pops up.
     */
    public MessageFragment(String message, DialogInterface.OnClickListener clickHandler) {
        super();
        this.message = message;
        this.clickHandler = clickHandler;
    }
    
    public MessageFragment(String message) {
        super();
        this.message = message;
        this.clickHandler = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // do nothing
            }
        };
    }

    /** @return The error message */
    public String getMessage() { return message; }

    @Override
    public Dialog onCreateDialog(Bundle b) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message).setPositiveButton(R.string.ok, clickHandler);
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
