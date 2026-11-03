package com.sb.woohoo.ui.main.fragments.dialer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sb.woohoo.R;
import com.sb.woohoo.custom.CircleImageTransform;
import com.sb.woohoo.databinding.ItemSipQuickActionBinding;
import com.sb.woohoo.ui.main.fragments.contacts.pojo.StaffMember;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

class SipQuickActionAdapter extends RecyclerView.Adapter<SipQuickActionAdapter.SipQuickActionViewHolder> {

    interface Listener {
        void onQuickContactClicked(StaffMember member);

        void onInternalCallClicked();
    }

    private static final int VIEW_TYPE_CONTACT = 1;
    private static final int VIEW_TYPE_INTERNAL_CALL = 2;

    private final Picasso picasso;
    private final Listener listener;
    private final List<StaffMember> staffMembers = new ArrayList<>();

    SipQuickActionAdapter(Picasso picasso, Listener listener) {
        this.picasso = picasso;
        this.listener = listener;
    }

    void submitContacts(List<StaffMember> contacts) {
        staffMembers.clear();
        if (contacts != null) {
            staffMembers.addAll(contacts);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position < staffMembers.size() ? VIEW_TYPE_CONTACT : VIEW_TYPE_INTERNAL_CALL;
    }

    @NonNull
    @Override
    public SipQuickActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSipQuickActionBinding binding = ItemSipQuickActionBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new SipQuickActionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SipQuickActionViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_CONTACT) {
            holder.bindContact(staffMembers.get(position), position);
        } else {
            holder.bindInternalCall();
        }
    }

    @Override
    public int getItemCount() {
        return staffMembers.size() + 1;
    }

    final class SipQuickActionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ItemSipQuickActionBinding binding;

        SipQuickActionViewHolder(ItemSipQuickActionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        void bindContact(StaffMember member, int position) {
            binding.getRoot().setCardBackgroundColor(getCardBackgroundColor(position));
            binding.itemSipQuickActionTitle.setText(member.getName());
            binding.itemSipQuickActionSubtitle.setText(formatPhoneNumber(member.getPhoneNumber()));
            if (member.getImageUrl() != null && !member.getImageUrl().trim().isEmpty()) {
                binding.itemSipQuickActionIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
                binding.itemSipQuickActionIcon.setPadding(0, 0, 0, 0);
                picasso.load(member.getImageUrl())
                        .placeholder(R.drawable.ic_contact_default_48dp)
                        .fit()
                        .transform(new CircleImageTransform())
                        .into(binding.itemSipQuickActionIcon);
            } else {
                int iconPadding = binding.getRoot().getResources().getDimensionPixelSize(R.dimen.dp_12);
                binding.itemSipQuickActionIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
                binding.itemSipQuickActionIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                picasso.load(R.drawable.ic_contact_default_48dp)
                        .fit()
                        .transform(new CircleImageTransform())
                        .into(binding.itemSipQuickActionIcon);
            }
        }

        void bindInternalCall() {
            binding.getRoot().setCardBackgroundColor(
                    ContextCompat.getColor(binding.getRoot().getContext(), R.color.sip_card_surface_internal)
            );
            binding.itemSipQuickActionTitle.setText(R.string.sip_internal_call_title);
            binding.itemSipQuickActionSubtitle.setText(R.string.sip_internal_call_subtitle);
            int iconPadding = binding.getRoot().getResources().getDimensionPixelSize(R.dimen.dp_12);
            binding.itemSipQuickActionIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
            binding.itemSipQuickActionIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            binding.itemSipQuickActionIcon.setImageResource(R.drawable.ic_dialpad);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }
            if (SipQuickActionAdapter.this.getItemViewType(adapterPosition) == VIEW_TYPE_CONTACT) {
                listener.onQuickContactClicked(staffMembers.get(adapterPosition));
            } else {
                listener.onInternalCallClicked();
            }
        }

        private String formatPhoneNumber(String phoneNumber) {
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return binding.getRoot().getContext().getString(R.string.sip_quick_actions_contact_subtitle);
            }
            return phoneNumber.trim();
        }

        private int getCardBackgroundColor(int position) {
            int[] colors = new int[] {
                    R.color.sip_card_surface_primary,
                    R.color.sip_card_surface_secondary,
                    R.color.sip_card_surface_tertiary,
                    R.color.sip_card_surface_quaternary
            };
            return ContextCompat.getColor(
                    binding.getRoot().getContext(),
                    colors[position % colors.length]
            );
        }
    }
}
