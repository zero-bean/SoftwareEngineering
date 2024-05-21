package com.gcu.anniversary;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import DTO.GiftData;

public class GiftListFragment extends DialogFragment {
    private String friendListId;
    private FirebaseDatabaseManager databaseManager;
    private ListView giftListView;
    private List<GiftData> giftDataList;
    private GiftAdapter giftAdapter;
    private ImageButton addGiftButton;
    private ImageButton closeButton;

    public GiftListFragment(String friendListId) {
        this.friendListId = friendListId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_list, container, false);

        databaseManager = new FirebaseDatabaseManager();
        giftListView = view.findViewById(R.id.giftListView);
        addGiftButton = view.findViewById(R.id.addGiftButton);
        closeButton = view.findViewById(R.id.closeButton);

        giftDataList = new ArrayList<>();
        giftAdapter = new GiftAdapter(getContext(), giftDataList);
        giftListView.setAdapter(giftAdapter);

        addGiftButton.setOnClickListener(v -> showAddGiftDialog());
        closeButton.setOnClickListener(v -> dismiss());

        fetchGifts();

        return view;
    }

    private void fetchGifts() {
        databaseManager.getGiftsReference().orderByChild("friendListID").equalTo(friendListId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        giftDataList.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            GiftData gift = snapshot.getValue(GiftData.class);
                            if (gift != null) {
                                giftDataList.add(gift);
                            }
                        }
                        sortAndDisplayGifts();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("GiftListFragment", "Failed to fetch gifts: " + databaseError.getMessage());
                    }
                });
    }

    private void sortAndDisplayGifts() {
        Collections.sort(giftDataList, (gift1, gift2) -> gift2.getDate().compareTo(gift1.getDate()));
        giftAdapter.notifyDataSetChanged();
    }

    private void showAddGiftDialog() {
        AddGiftFragment addGiftFragment = new AddGiftFragment(friendListId, this::fetchGifts);
        addGiftFragment.show(getParentFragmentManager(), "AddGiftFragment");
    }

    private class GiftAdapter extends ArrayAdapter<GiftData> {
        private List<GiftData> gifts;

        public GiftAdapter(Context context, List<GiftData> gifts) {
            super(context, R.layout.gift_list_item, gifts);
            this.gifts = gifts;
        }

        @NonNull
        @Override
        public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.gift_list_item, parent, false);
            }

            GiftData gift = gifts.get(position);
            TextView giftInfo = convertView.findViewById(R.id.giftInfo);
            ImageButton editButton = convertView.findViewById(R.id.editButton);
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

            giftInfo.setText(gift.getDate() + " - " + gift.getGiftName());

            editButton.setOnClickListener(v -> showEditGiftDialog(gift));
            deleteButton.setOnClickListener(v -> deleteGift(gift.getGiftID()));

            return convertView;
        }

        private void showEditGiftDialog(GiftData gift) {
            AddGiftFragment addGiftFragment = new AddGiftFragment(friendListId, GiftListFragment.this::fetchGifts, gift);
            addGiftFragment.show(GiftListFragment.this.getParentFragmentManager(), "EditGiftFragment");
        }

        private void deleteGift(String giftID) {
            databaseManager.deleteGift(giftID, aVoid -> {
                Toast.makeText(getContext(), "Gift deleted", Toast.LENGTH_SHORT).show();
                fetchGifts();
            }, e -> Toast.makeText(getContext(), "Failed to delete gift", Toast.LENGTH_SHORT).show());
        }
    }
}
