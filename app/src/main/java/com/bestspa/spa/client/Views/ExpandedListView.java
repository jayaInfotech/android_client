package com.bestspa.spa.client.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ExpandedListView extends ExpandableListView {
    private Context context;
    private int old_count = 0;
    private ViewGroup.LayoutParams params;

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onDraw(Canvas canvas) {
        if (getCount() != this.old_count) {
            int index = 0;
            int groupcount = getExpandableListAdapter().getGroupCount();
            for (int i = 0; i < groupcount; i++) {
                if (isGroupExpanded(i)) {
                    index = i;
                }
            }
            this.old_count = getCount();
            this.params = getLayoutParams();
            int groupheight = getChildAt(0).getHeight() + 2;
            int subrowheight = 0;
            try {
                if (getCount() != groupcount) {
                    subrowheight = getChildAt(index + 1).getHeight() + 2;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.params.height = (groupcount * groupheight) + ((getCount() - groupcount) * subrowheight);
            setLayoutParams(this.params);
        }
        super.onDraw(canvas);
    }
}
