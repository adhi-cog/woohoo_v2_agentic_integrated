package com.sb.woohoo.ui.main.fragments.todo.di;

import com.sb.woohoo.ui.main.fragments.todo.ToDoFragment;

import dagger.Component;

/**
 * {@link Component} for {@link ToDoFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Component(modules = ToDoModule.class)
@ToDoScope
public interface ToDoComponent {
    void injectToDoFragment(ToDoFragment fragment);
}