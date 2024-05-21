package com.gcu.anniversary;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.gcu.anniversary.firebase.FirebaseDatabaseManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import DTO.AnniversaryData;

public class CalendarFragment extends Fragment implements AddAnniversaryFragment.OnAnniversaryUpdatedListener {
    private FirebaseDatabaseManager databaseManager;
    private ListView anniversaryListView;
    private AnniversaryAdapter anniversaryAdapter;
    private List<AnniversaryData> anniversaryDataList;
    private ValueEventListener anniversaryEventListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        databaseManager = new FirebaseDatabaseManager();
        anniversaryListView = view.findViewById(R.id.calendarListView);

        anniversaryDataList = new ArrayList<>();
        anniversaryAdapter = new AnniversaryAdapter(anniversaryDataList);
        anniversaryListView.setAdapter(anniversaryAdapter);

        fetchAnniversaries();

        // Set toolbar with menu
        Toolbar toolbar = view.findViewById(R.id.toolbar_anniversary);
        toolbar.inflateMenu(R.menu.anniversary_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add_anniversary_button) {
                    showAddAnniversaryDialog();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    private void fetchAnniversaries() {
        String userId = getCurrentUserId();
        if (userId == null) {
            Log.e("CalendarFragment", "User ID is null");
            return;
        }

        if (anniversaryEventListener != null) {
            databaseManager.getAnniversariesReference().removeEventListener(anniversaryEventListener);
        }

        anniversaryEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                anniversaryDataList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    AnniversaryData anniversary = snapshot.getValue(AnniversaryData.class);
                    if (anniversary != null) {
                        anniversaryDataList.add(anniversary);
                    }
                }
                sortAnniversariesByDate();
                anniversaryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("CalendarFragment", "Failed to fetch anniversaries: " + databaseError.getMessage());
            }
        };

        databaseManager.getAnniversariesReference().orderByChild("userId").equalTo(userId)
                .addValueEventListener(anniversaryEventListener);
    }

    private void sortAnniversariesByDate() {
        Collections.sort(anniversaryDataList, new Comparator<AnniversaryData>() {
            @Override
            public int compare(AnniversaryData o1, AnniversaryData o2) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date date1 = sdf.parse(o1.getDate());
                    Date date2 = sdf.parse(o2.getDate());
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    private void showEditAnniversaryDialog(final AnniversaryData anniversary) {
        AddAnniversaryFragment addAnniversaryFragment = AddAnniversaryFragment.newInstance(anniversary, this);
        addAnniversaryFragment.show(getParentFragmentManager(), "AddAnniversaryFragment");
    }

    private void showAddAnniversaryDialog() {
        AddAnniversaryFragment addAnniversaryFragment = AddAnniversaryFragment.newInstance(null, this);
        addAnniversaryFragment.show(getParentFragmentManager(), "AddAnniversaryFragment");
    }

    private String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onAnniversaryUpdated() {
        fetchAnniversaries();
    }

    private class AnniversaryAdapter extends BaseAdapter {
        private List<AnniversaryData> anniversaryList;

        public AnniversaryAdapter(List<AnniversaryData> anniversaryList) {
            this.anniversaryList = anniversaryList;
        }

        @Override
        public int getCount() {
            return anniversaryList.size();
        }

        @Override
        public Object getItem(int position) {
            return anniversaryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.anniversary_list_item, parent, false);
            }

            TextView dateTextView = convertView.findViewById(R.id.dateTextView);
            TextView commentTextView = convertView.findViewById(R.id.commentTextView);
            ImageButton editButton = convertView.findViewById(R.id.editButton);
            ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

            AnniversaryData anniversary = anniversaryList.get(position);
            dateTextView.setText(anniversary.getDate());
            commentTextView.setText(anniversary.getComment());

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditAnniversaryDialog(anniversary);
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteAnniversary(anniversary);
                }
            });

            return convertView;
        }

        private void deleteAnniversary(AnniversaryData anniversary) {
            databaseManager.deleteAnniversary(anniversary.getId(), aVoid -> {
                anniversaryList.remove(anniversary);
                notifyDataSetChanged();
                Toast.makeText(getContext(), "Anniversary deleted successfully", Toast.LENGTH_SHORT).show();
            }, e -> Toast.makeText(getContext(), "Failed to delete anniversary", Toast.LENGTH_SHORT).show());
        }
    }
}
