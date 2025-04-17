package com.example.appal_quranv1.ui;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.model.Juz;
import java.util.List;

public class JuzAdapter extends RecyclerView.Adapter<JuzAdapter.JuzViewHolder> {

    private final List<Juz> juzList;

    public JuzAdapter(List<Juz> juzList) {
        this.juzList = juzList;
    }

    @NonNull
    @Override
    public JuzViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new JuzViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_juz, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JuzViewHolder holder, int position) {
        Juz juz = juzList.get(position);
        if (juz == null) return;

        holder.tvJuzNumber.setText(String.valueOf(juz.getNumber()));
        holder.tvJuzDetails.setText("Juz " + juz.getNumber());

        String range = getJuzRange(juz.getNumber());
        holder.tvAyahRange.setText(range);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), JuzDetailActivity.class);
            intent.putExtra("juz_number", juz.getNumber());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return juzList.size();
    }

    static class JuzViewHolder extends RecyclerView.ViewHolder {
        TextView tvJuzNumber, tvJuzDetails, tvAyahRange;

        JuzViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJuzNumber = itemView.findViewById(R.id.tvJuzNumber);
            tvJuzDetails = itemView.findViewById(R.id.tvJuzDetails);
            tvAyahRange = itemView.findViewById(R.id.tvAyahRange);
        }
    }

    private String getJuzRange(int juzNumber) {
        switch (juzNumber) {
            case 1: return "Surah 1, Ayah 1 - Surah 2, Ayah 141";
            case 2: return "Surah 2, Ayah 142 - Surah 2, Ayah 252";
            case 3: return "Surah 2, Ayah 253 - Surah 3, Ayah 92";
            case 4: return "Surah 3, Ayah 93 - Surah 4, Ayah 23";
            case 5: return "Surah 4, Ayah 24 - Surah 4, Ayah 147";
            case 6: return "Surah 4, Ayah 148 - Surah 5, Ayah 81";
            case 7: return "Surah 5, Ayah 82 - Surah 6, Ayah 110";
            case 8: return "Surah 6, Ayah 111 - Surah 7, Ayah 87";
            case 9: return "Surah 7, Ayah 88 - Surah 8, Ayah 40";
            case 10: return "Surah 8, Ayah 41 - Surah 9, Ayah 92";
            case 11: return "Surah 9, Ayah 93 - Surah 11, Ayah 5";
            case 12: return "Surah 11, Ayah 6 - Surah 12, Ayah 52";
            case 13: return "Surah 12, Ayah 53 - Surah 14, Ayah 52";
            case 14: return "Surah 15, Ayah 1 - Surah 16, Ayah 128";
            case 15: return "Surah 17, Ayah 1 - Surah 18, Ayah 74";
            case 16: return "Surah 18, Ayah 75 - Surah 20, Ayah 135";
            case 17: return "Surah 21, Ayah 1 - Surah 22, Ayah 78";
            case 18: return "Surah 23, Ayah 1 - Surah 25, Ayah 20";
            case 19: return "Surah 25, Ayah 21 - Surah 27, Ayah 55";
            case 20: return "Surah 27, Ayah 56 - Surah 29, Ayah 45";
            case 21: return "Surah 29, Ayah 46 - Surah 33, Ayah 30";
            case 22: return "Surah 33, Ayah 31 - Surah 36, Ayah 27";
            case 23: return "Surah 36, Ayah 28 - Surah 39, Ayah 31";
            case 24: return "Surah 39, Ayah 32 - Surah 41, Ayah 46";
            case 25: return "Surah 41, Ayah 47 - Surah 45, Ayah 37";
            case 26: return "Surah 46, Ayah 1 - Surah 50, Ayah 45";
            case 27: return "Surah 51, Ayah 1 - Surah 57, Ayah 29";
            case 28: return "Surah 58, Ayah 1 - Surah 66, Ayah 12";
            case 29: return "Surah 67, Ayah 1 - Surah 77, Ayah 50";
            case 30: return "Surah 78, Ayah 1 - Surah 114, Ayah 6";
            default: return "Juz " + juzNumber;
        }
    }
}
