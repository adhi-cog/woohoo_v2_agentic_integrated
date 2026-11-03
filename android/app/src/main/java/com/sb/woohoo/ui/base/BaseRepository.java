package com.sb.woohoo.ui.base;

public class BaseRepository<T extends BasePresenter> {

    private T actions;

    public void onAttach(T actions) {
        this.actions = actions;
    }

    public T getActions() {
        return actions;
    }

    //TODO make this abstract later
    public void repositoryCleanUps() {

    }
}
