package com.bestspa.spa.client.map;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import java.util.HashMap;

public class CustomAutoCompleteTextView extends AutoCompleteTextView {
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    protected CharSequence convertSelectionToString(Object selectedItem) {
//        return (CharSequence) ((HashMap) selectedItem).get(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_DESCRIPTION);
//    }
}
