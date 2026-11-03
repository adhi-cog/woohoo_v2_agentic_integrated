package com.sb.woohoo.ui.main.fragments.dialer;

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
 * Created by Shrey on 18/03/18.
 */

public class ContactRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ALL_CONTACTS_VIEW = 1, ITEM_VIEW = 2;
    private List familyMemberList = new ArrayList(0);
    private DialerFragment mListener;
    private Picasso mPicasso;

    /**
     * Constructor Call of {@link ContactRecyclerAdapter}.
     *
     * @param picasso  Instance of {@link Picasso}.
     * @param listener Instance of {@link DialerFragment}.
     */
    @Inject
    ContactRecyclerAdapter(Picasso picasso, DialerFragment listener) {
        this.mPicasso = picasso;
        this.mListener = listener;
    }

    /**
     * Updates the Family Member from the API Response.
     *
     * @param staffMembers List of Family Members to be added.
     */
    public void updateFamilyMembers(List<StaffMember> staffMembers) {
        if (staffMembers != null) {

//            if (staffMembers.size() <=5) {
//                familyMemberList = sortFamilyMember(staffMembers.size(),staffMembers);
//            } else {
//                familyMemberList = sortFamilyMember(4, staffMembers);
//            }
            this.familyMemberList=staffMembers;
        } else
            familyMemberList = sortFamilyMember(0, null);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemContactFamilyBinding binding = ItemContactFamilyBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        if (viewType == ITEM_VIEW) {
            return new FamilyViewHolder(binding);
        } else {
            return new AllContactsViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FamilyViewHolder) {
            FamilyViewHolder familyViewHolder = (FamilyViewHolder) holder;
            familyViewHolder.onBind((StaffMember) familyMemberList.get(position), mPicasso);
        } else if (holder instanceof AllContactsViewHolder) {
            AllContactsViewHolder allContactsViewHolder = (AllContactsViewHolder) holder;
            allContactsViewHolder.onBindView();
        }
    }

    @Override
    public int getItemCount() {
        return familyMemberList != null ? familyMemberList.size() : 0;

    }

    @Override
    public int getItemViewType(int position) {
        if (familyMemberList.get(position) instanceof String) {
            return ALL_CONTACTS_VIEW;
        } else {
            return ITEM_VIEW;
        }
    }

    private List sortFamilyMember(int size, List<StaffMember> list) {
        List familyList = new ArrayList();
        for (int i = 0; i < size; i++) {
            familyList.add(list.get(i));
        }
        familyList.add(mListener.getString(R.string.all_contacts_holder));
        return familyList;
    }


    class FamilyViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public ItemContactFamilyBinding binding;

        FamilyViewHolder(ItemContactFamilyBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
            initializeViews(binding);
        }

        void onBind(StaffMember member, Picasso picasso) {
            ((View) binding.itemContactFamilyImage.getParent()).setBackgroundResource(0);
            //String[] names = member.getName().split(" ");
            binding.itemContactFamilyName.setText(member.getName());
            if (member.getImageUrl() != null && !member.getImageUrl().equalsIgnoreCase("")) {
                picasso
                        .load(member.getImageUrl())
                        .placeholder(R.drawable.ic_contact_default_48dp)
                        .fit()
                        .transform(new CircleImageTransform())
                        .into( binding.itemContactFamilyImage);
            } else {
                picasso
                        .load(member.getDrawableSource())
                        .fit()
                        .transform(new CircleImageTransform())
                        .into( binding.itemContactFamilyImage);
            }
        }

        private void initializeViews(ItemContactFamilyBinding binding) {
            binding.getRoot().setOnClickListener(FamilyViewHolder.this);
        }

        @Override
        public void onClick(View view) {
            mListener.onFamilyMemberClicked((StaffMember) familyMemberList.get(getAdapterPosition()));
        }
    }

    class AllContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ItemContactFamilyBinding binding;

        AllContactsViewHolder(ItemContactFamilyBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.binding = itemRowBinding;
            initViews(binding);
        }

        private void initViews(ItemContactFamilyBinding itemRowBinding) {
            binding.itemContactFamilyImage.setOnClickListener(this);
        }

        private void onBindView() {
            ((View) binding.itemContactFamilyImage.getParent()).setBackgroundResource(0);
            binding.itemContactFamilyImage.setBackground((binding.itemContactFamilyImage.getContext().getResources().getDrawable(R.drawable.bg_circle_transparent)));
            binding.itemContactFamilyImage.setImageResource(R.drawable.ic_all_contacts);
            binding.itemContactFamilyImage.setPadding(R.dimen.dp_20, R.dimen.dp_20, R.dimen.dp_20, R.dimen.dp_20);
            binding.itemContactFamilyName.setText(mListener.getString(R.string.All));
        }

        @Override
        public void onClick(View view) {
            mListener.onAllContactsClicked();
        }
    }
}
