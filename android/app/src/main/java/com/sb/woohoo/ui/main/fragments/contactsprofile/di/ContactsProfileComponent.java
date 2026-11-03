package com.sb.woohoo.ui.main.fragments.contactsprofile.di;

import com.sb.woohoo.ui.main.fragments.contactsprofile.ContactsProfileFragment;

import dagger.Component;

/**
 * {@link Component} for {@link ContactsProfileFragment}.
 *
 * @author Ritwik Jamuar.
 */
@Component(modules = ContactsProfileModule.class)
@ContactsProfileScope
public interface ContactsProfileComponent {
    void injectContactsProfileFragment(ContactsProfileFragment fragment);
}