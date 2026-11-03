package com.sb.woohoo.ui.main.fragments.faq.questions;

import android.util.Log;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sb.woohoo.R;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import static android.view.animation.Animation.RELATIVE_TO_SELF;

public class QuestionGroupViewHolder extends GroupViewHolder {
    private TextView groupName, list_item_group_position;
    private ImageView arrow;
    public QuestionGroupViewHolder(View itemView) {
        super(itemView);
        groupName = (TextView) itemView.findViewById(R.id.groupName);
        list_item_group_position = (TextView) itemView.findViewById(R.id.list_item_group_position);
        arrow = (ImageView) itemView.findViewById(R.id.list_item_group_arrow);
    }

    public void setQuestion(int position, ExpandableGroup group) {
        if (group instanceof QuestionExpandableGroup) {
            groupName.setText(group.getTitle());
            Log.e("position", "setQuestion - " + position);
            list_item_group_position.setText("" + (position + 1) + ".");
        }
    }

    @Override
    public void expand() {
        animateExpand();
    }

    @Override
    public void collapse() {
        animateCollapse();
    }

    private void animateExpand() {
        RotateAnimation rotate =
                new RotateAnimation(360, 180, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
        arrow.setBackgroundResource(R.drawable.bg_arrow_expand_less);
    }

    private void animateCollapse() {
        RotateAnimation rotate =
                new RotateAnimation(180, 360, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        arrow.setAnimation(rotate);
        arrow.setBackgroundResource(R.drawable.bg_arrow_expand_more);
    }
}
