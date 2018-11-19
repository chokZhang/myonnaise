package com.mengyuan1998.finger_dancing.Utilities.auto_complete;

import android.util.Log;
import android.widget.TextView;


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
    public boolean onPopupItemClicked(TextView editable, String item) {
        //TODO 日后需要统一补全格式
        String content = editable.getText().toString();
        Log.d(TAG, "onPopupItemClicked: content is " + content);
        int index = content.lastIndexOf("。");
        if(index != -1){
            content = content.substring(0, index + 1);
            Log.d(TAG, "onPopupItemClicked: index : " + index + "content is " + content);
        }
        else{
            content = "";
        }
        content = content + item;
        editable.setText(content);

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
