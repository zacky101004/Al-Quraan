package com.example.appal_quranv1.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.data.AppDatabase;
import com.example.appal_quranv1.model.Bookmark;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    private List<Bookmark> bookmarkList;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public BookmarkAdapter(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookmarkViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bookmark, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);
        holder.tvFolder.setText(bookmark.getFolder());
        holder.tvSurah.setText("Surah " + bookmark.getSurahName());
        holder.tvAyat.setText("Ayat " + bookmark.getAyatNumber());
        holder.tvAyatText.setText(bookmark.getAyatText());
        holder.tvTranslation.setText(bookmark.getTranslation());

        // Navigasi ke SurahDetailActivity saat item diklik
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), SurahDetailActivity.class);
            intent.putExtra("surah_id", bookmark.getSurahId());
            intent.putExtra("surah_name", bookmark.getSurahName());
            holder.itemView.getContext().startActivity(intent);
        });

        // Tombol hapus bookmark
        holder.btnDelete.setOnClickListener(v -> {
            executorService.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(holder.itemView.getContext());
                db.bookmarkDao().delete(bookmark); // Perlu menambahkan method delete di BookmarkDao
                holder.itemView.post(() -> {
                    int currentPosition = holder.getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        bookmarkList.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                    }
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView tvFolder, tvSurah, tvAyat, tvAyatText, tvTranslation;
        ImageButton btnDelete;

        BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFolder = itemView.findViewById(R.id.tvFolder);
            tvSurah = itemView.findViewById(R.id.tvSurah);
            tvAyat = itemView.findViewById(R.id.tvAyat);
            tvAyatText = itemView.findViewById(R.id.tvAyatText);
            tvTranslation = itemView.findViewById(R.id.tvTranslation);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        executorService.shutdown();
    }
}