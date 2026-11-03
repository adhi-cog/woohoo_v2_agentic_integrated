package com.sb.woohoo.ui.main.fragments.qna.di;

import com.sb.woohoo.ui.main.fragments.qna.QnaFragment;

import dagger.Component;

@Component(modules = QnaModule.class)
@QnaScope
public interface QnaComponent {
    void injectQnaFragment(QnaFragment qnaFragment);
}