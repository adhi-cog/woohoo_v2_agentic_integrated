package com.sb.woohoo.ui.main.fragments.contacts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.databinding.ItemContactFamilyBinding;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Recycler View Adapter for Family.
 *
 * @author Ritwik Jamuar.
 */
public class FamilyContactsRecyclerAdapter extends RecyclerView.Adapter<FamilyContactsRecyclerAdapter.FamilyContactsViewHolder> {
    private List<StaffMember> mStaffMembers = new ArrayList<>();
    private ContactsFragment mListener;
    private Picasso mPicasso;

    /**
     * Constructor Call of {@link FamilyContactsRecyclerAdapter}.
     *
     * @param picasso  Instance of {@link Picasso}.
     * @param listener Instance of {@link ContactsFragment}.
     */
    @Inject
    FamilyContactsRecyclerAdapter(Picasso picasso, ContactsFragment listener) {
        this.mPicasso = picasso;
        this.mListener = listener;
    }

    /**
     * Updates the Family Member from the API Response.
     *
     * @param staffMembers List of Family Members to be added.
     */
    public void updateFamilyMembers(List<StaffMember> staffMembers) {
        this.mStaffMembers.clear();
        this.mStaffMembers.addAll(staffMembers);
        notifyDataSetChanged();
    }

    @Override
    public FamilyContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemContactFamilyBinding binding = ItemContactFamilyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FamilyContactsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(FamilyContactsViewHolder holder, int position) {
        holder.onBind(mStaffMembers.get(position), mPicasso);
    }

    @Override
    public int getItemCount() {
        return mStaffMembers.size();
    }

    /**
     * ViewHolder for {@link FamilyContactsRecyclerAdapter}
     */
    class FamilyContactsViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ItemContactFamilyBinding binding;

        FamilyContactsViewHolder(ItemContactFamilyBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
            binding.getRoot().setOnClickListener(FamilyContactsViewHolder.this);
        }

        void onBind(StaffMember member, Picasso picasso) {
            binding.itemContactFamilyName.setText(member.getName());
            picasso
                    .load(member.getImageUrl())
                    .placeholder(R.drawable.ic_contact_default_48dp)
                    .fit()
                    .transform(new CircleImageTransform())
                    .into(binding.itemContactFamilyImage);
        }

        @Override
        public void onClick(View view) {
            mListener.onFamilyMemberClicked(mStaffMembers.get(getAdapterPosition()));
        }
    }
}