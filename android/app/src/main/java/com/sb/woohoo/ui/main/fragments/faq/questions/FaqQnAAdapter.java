package com.sb.woohoo.ui.main.fragments.faq.questions;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sb.woohoo.R;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.AnswerData;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.listeners.GroupExpandCollapseListener;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import java.util.List;

public class FaqQnAAdapter extends ExpandableRecyclerViewAdapter<QuestionGroupViewHolder, FaqQnAAdapter.AnswerChildViewHolder> {

    public FaqQnAAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public QuestionGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq_group, parent, false);
        return new QuestionGroupViewHolder(view);
    }

    @Override
    public AnswerChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_faq_child, parent, false);
        return new AnswerChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(AnswerChildViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final AnswerData faqAnswer = ((QuestionExpandableGroup) group).getItems().get(childIndex);
        holder.setAnswer(faqAnswer.getData());
    }

    @Override
    public void onBindGroupViewHolder(QuestionGroupViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setQuestion(flatPosition, group);
        setOnGroupExpandCollapseListener(new GroupExpandCollapseListener() {
            @Override
            public void onGroupExpanded(ExpandableGroup group) {
                Log.e("groupListener","onGroupExpanded");
            }

            @Override
            public void onGroupCollapsed(ExpandableGroup group) {
                Log.e("groupListener","onGroupCollapsed");
                notifyDataSetChanged();
            }
        });
    }

    class AnswerChildViewHolder extends ChildViewHolder{
        private TextView childTextView;

        public AnswerChildViewHolder(View itemView) {
            super(itemView);
            childTextView = (TextView) itemView.findViewById(R.id.list_item_artist_name);
        }
        public void setAnswer(String name) {
            childTextView.setText(name);
        }
    }
}
