package com.example.appal_quranv1.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.model.Surah;
import java.util.ArrayList; // Import ditambahkan
import java.util.List;

public class SurahAdapter extends RecyclerView.Adapter<SurahAdapter.SurahViewHolder> {

    private final List<Surah> surahList;

    public SurahAdapter(List<Surah> surahList) {
        this.surahList = surahList != null ? surahList : new ArrayList<>();
    }

    @NonNull
    @Override
    public SurahViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SurahViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_surah, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SurahViewHolder holder, int position) {
        Surah surah = surahList.get(position);
        if (surah == null) return;

        holder.tvSurahNumber.setText(String.valueOf(surah.getNumber()));
        holder.tvSurahName.setText(surah.getEnglishName() != null ? surah.getEnglishName() : "Unknown");
        holder.tvSurahArabicName.setText(surah.getName() != null ? surah.getName() : "");
        holder.tvAyahCount.setText(surah.getNumberOfAyahs() + " Ayahs");
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), SurahDetailActivity.class);
            intent.putExtra("surah_id", surah.getNumber());
            intent.putExtra("surah_name", surah.getEnglishName());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return surahList.size();
    }

    static class SurahViewHolder extends RecyclerView.ViewHolder {
        TextView tvSurahNumber, tvSurahName, tvSurahArabicName, tvAyahCount;

        SurahViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSurahNumber = itemView.findViewById(R.id.tvSurahNumber);
            tvSurahName = itemView.findViewById(R.id.tvSurahName);
            tvSurahArabicName = itemView.findViewById(R.id.tvSurahArabicName);
            tvAyahCount = itemView.findViewById(R.id.tvAyahCount);
        }
    }
}