package com.sb.woohoo.ui.main.fragments.contacts.di;

import com.sb.woohoo.ui.main.fragments.contacts.ContactsFragment;

import dagger.Component;

/**
 * {@link Component} for {@link ContactsFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Component(modules = ContactsModule.class)
@ContactsScope
public interface ContactsComponent {
    void injectContactsFragment(ContactsFragment fragment);
}