package com.sb.woohoo.ui.main.fragments.contacts;

import static com.sb.woohoo.utils.Constants.IMAGE_BASE_URL;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.databinding.ItemContactBinding;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.ContactsData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Recycler View Adapter for Contacts.
 *
 * @author Ritwik Jamuar.
 */
public class ContactsRecyclerViewAdapter
        extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactsViewHolder> {
    private List<ContactsData> mContacts = new ArrayList<>();
    private ContactsFragment mListener;
    private Picasso mPicasso;

    /**
     * Constructor Call of {@link ContactsRecyclerViewAdapter}.
     *
     * @param picasso  Instance of {@link Picasso}.
     * @param listener Instance of {@link ContactsFragment}.
     */
    @Inject
    ContactsRecyclerViewAdapter(Picasso picasso, ContactsFragment listener) {
        this.mPicasso = picasso;
        this.mListener = listener;
    }

    /**
     * Updates the Contacts from the API Response.
     *
     * @param contacts List of Contacts to be added.
     */
    void updateContactList(List<ContactsData> contacts) {
        mContacts.clear();
        mContacts.addAll(contacts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        ItemContactBinding binding = ItemContactBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ContactsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ContactsViewHolder holder,
            int position) {
        holder.onBind(mContacts.get(position), mPicasso);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {

        public ItemContactBinding binding;

        ContactsViewHolder(ItemContactBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
        }

        void onBind(ContactsData contact, Picasso picasso) {
            // Get Contact Name and display it on UI.
            String contactName = contact.getName();
            if (contactName == null || contactName.isEmpty()) {
                List<String> phoneNumbers = contact.getPhoneNos();
                if (phoneNumbers != null) {
                    String phoneNumber = phoneNumbers.get(0);
                    if (phoneNumber == null || phoneNumber.isEmpty()) {
                        contactName = "NA";
                    } else {
                        contactName = phoneNumber;
                    }
                } else contactName = "NA";
            }
            binding.itemContactName.setText(contactName);

            // Get Contact Image and display it on UI.
            String imageURL = contact.getImageUrl();
            if (imageURL == null || imageURL.length() == 0) return;
            picasso.load(IMAGE_BASE_URL.concat(contact.getImageUrl()))
                    .placeholder(R.drawable.ic_contact_default_48dp)
                    .transform(new CircleImageTransform())
                    .into( binding.itemContactImage);

            binding.itemContactLinearLayout.setOnClickListener(view -> onContactClicked());
        }

        void onContactClicked() {
            mListener.onContactClicked(mContacts.get(getAdapterPosition()));
        }
    }
}