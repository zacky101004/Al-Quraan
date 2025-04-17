package com.example.appal_quranv1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.data.AppDatabase;
import com.example.appal_quranv1.model.Bookmark;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarkFragment extends Fragment {

    private RecyclerView rvBookmarks;
    private BookmarkAdapter adapter;
    private List<Bookmark> bookmarkList = new ArrayList<>();
    private TextView tvLastRead;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        tvLastRead = view.findViewById(R.id.tvLastRead);
        rvBookmarks = view.findViewById(R.id.rvBookmarks);
        rvBookmarks.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BookmarkAdapter(bookmarkList);
        rvBookmarks.setAdapter(adapter);

        loadBookmarks();
        return view;
    }

    private void loadBookmarks() {
        if (getContext() == null) {
            Log.e("BookmarkFragment", "Context is null, cannot load bookmarks.");
            return;
        }

        executorService.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(getContext());
                Bookmark lastRead = db.bookmarkDao().getLastRead();
                List<Bookmark> bookmarks = db.bookmarkDao().getAllBookmarks();

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (lastRead != null) {
                            tvLastRead.setText("Terakhir Dibaca: Surah " + lastRead.getSurahName() + " Ayat " + lastRead.getAyatNumber());
                            tvLastRead.setOnClickListener(v -> {
                                Intent intent = new Intent(getContext(), SurahDetailActivity.class);
                                intent.putExtra("surah_id", lastRead.getSurahId());
                                intent.putExtra("surah_name", lastRead.getSurahName());
                                startActivity(intent);
                            });
                        } else {
                            tvLastRead.setText("Belum ada ayat terakhir yang dibaca");
                            tvLastRead.setOnClickListener(null);
                        }

                        bookmarkList.clear();
                        bookmarkList.addAll(bookmarks != null ? bookmarks : new ArrayList<>());
                        adapter.notifyDataSetChanged();
                    });
                }
            } catch (Exception e) {
                Log.e("BookmarkFragment", "Error loading bookmarks: ", e);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadBookmarks();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}