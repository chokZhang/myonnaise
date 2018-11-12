package com.mengyuan1998.finger_dancing.Utilities.auto_complete;

import android.text.Editable;
import android.util.Log;

import com.otaliastudios.autocomplete.AutocompleteCallback;

/**
 * Created by boyzhang on 2018/10/1.
 */

public class SimpleAutocompleteCallback implements AutocompleteCallback <String>{

    private static final String TAG = "AutocompleteCallback";

    public SimpleAutocompleteCallback(){

    }

    /**
     * Called when an item inside your list is clicked.
     * This works if your presenter has dispatched a click event.
     * At this point you can edit the text, e.g. {@code editable.append(item.toString())}.
     *
     * @param editable editable text that you can work on
     * @param item item that was clicked
     * @return true if the action is valid and the popup can be dismissed
     */
    public boolean onPopupItemClicked(Editable editable, String item) {
        String content = editable.toString();
        item = item + "。";
        int index = content.lastIndexOf("。");
        int length = content.length();

        String before = content.substring(index + 1, length);
        editable.replace(index + 1, length, item);

        Log.i(TAG, "onPopupItemClicked: change " + before + " to " + item);
        return true;
    }

    /**
     * Called when popup visibility state changes.
     *
     * @param shown true if the popup was just shown, false if it was just hidden
     */
    public void onPopupVisibilityChanged(boolean shown) {

    }
}
