package com.sb.woohoo.ui.main.fragments.faq.questions;

import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.Answer;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.AnswerData;
import com.sb.woohoo.ui.main.fragments.faq.questions.pojo.FaqQuestion;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class QuestionExpandableGroup extends ExpandableGroup<AnswerData> {

    public QuestionExpandableGroup(String title, List<AnswerData> items) {
        super(title, items);
    }
}
