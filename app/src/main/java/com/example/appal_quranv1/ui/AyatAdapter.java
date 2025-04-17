package com.example.appal_quranv1.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appal_quranv1.R;
import com.example.appal_quranv1.data.AppDatabase;
import com.example.appal_quranv1.model.Ayat;
import com.example.appal_quranv1.model.Bookmark;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AyatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_AYAT = 1;

    private final List<Ayat> ayatList;
    private final String surahName;
    private final int surahId;
    private MediaPlayer mediaPlayer;
    private AyatListener ayatListener;
    private int currentlyPlayingPosition = -1;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public AyatAdapter(List<Ayat> ayatList, String surahName, int surahId) {
        this.ayatList = ayatList != null ? ayatList : new ArrayList<>();
        this.surahName = surahName != null ? surahName : "Unknown Surah";
        this.surahId = surahId;
        this.mediaPlayer = new MediaPlayer();
    }

    public void setAyatListener(AyatListener listener) {
        this.ayatListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_AYAT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_ayat, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ayat, parent, false);
            return new AyatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.tvHeader.setText(surahName);
        } else {
            AyatViewHolder ayatHolder = (AyatViewHolder) holder;
            int ayatPosition = position - 1; // Kurangi 1 karena posisi 0 adalah header
            Ayat ayat = ayatList.get(ayatPosition);
            if (ayat == null) return;

            ayatHolder.tvAyatNumber.setText(String.valueOf(ayat.getNumberInSurah()));
            ayatHolder.tvArabic.setText(ayat.getText() != null ? ayat.getText() : "Teks tidak tersedia");
            ayatHolder.tvTranslation.setText(ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia");

            // Update visibility of play/pause buttons
            int currentPosition = ayatHolder.getAdapterPosition();
            if (currentPosition == currentlyPlayingPosition && mediaPlayer.isPlaying()) {
                ayatHolder.btnPlay.setVisibility(View.GONE);
                ayatHolder.btnPause.setVisibility(View.VISIBLE);
            } else {
                ayatHolder.btnPlay.setVisibility(View.VISIBLE);
                ayatHolder.btnPause.setVisibility(View.GONE);
            }

            // Play button click listener
            ayatHolder.btnPlay.setOnClickListener(v -> {
                int adapterPosition = ayatHolder.getAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) return;

                // Reset previous playback if any
                if (currentlyPlayingPosition != -1 && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    notifyItemChanged(currentlyPlayingPosition);
                }

                if (ayat.getAudio() == null) {
                    ayatHolder.showError("Audio tidak tersedia untuk ayat ini.");
                    return;
                }

                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(ayat.getAudio());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    currentlyPlayingPosition = adapterPosition;
                    ayatHolder.btnPlay.setVisibility(View.GONE);
                    ayatHolder.btnPause.setVisibility(View.VISIBLE);

                    if (ayatListener != null) {
                        ayatListener.saveLastRead(ayat);
                    }

                    mediaPlayer.setOnCompletionListener(mp -> {
                        int currentPos = ayatHolder.getAdapterPosition();
                        if (currentPos == RecyclerView.NO_POSITION) return;

                        mediaPlayer.reset();
                        currentlyPlayingPosition = -1;
                        ayatHolder.btnPlay.setVisibility(View.VISIBLE);
                        ayatHolder.btnPause.setVisibility(View.GONE);
                    });
                } catch (IOException e) {
                    ayatHolder.showError("Gagal memutar audio: " + e.getMessage());
                }
            });

            // Pause button click listener
            ayatHolder.btnPause.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    ayatHolder.btnPlay.setVisibility(View.VISIBLE);
                    ayatHolder.btnPause.setVisibility(View.GONE);
                }
            });

            // Bookmark button click listener
            ayatHolder.btnBookmark.setOnClickListener(v -> {
                Context context = ayatHolder.itemView.getContext();
                if (context == null) {
                    ayatHolder.showError("Konteks tidak tersedia.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Simpan ke Bookmark");

                View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_bookmark, null);
                Spinner folderSpinner = dialogView.findViewById(R.id.spinnerFolder);
                EditText newFolderEditText = dialogView.findViewById(R.id.etNewFolder);

                List<String> folders = new ArrayList<>();
                folders.add("Default");

                // Mengambil folder dari database di thread latar
                executorService.execute(() -> {
                    try {
                        AppDatabase db = AppDatabase.getInstance(context);
                        List<String> existingFolders = db.bookmarkDao().getAllFolders();
                        if (existingFolders != null) {
                            for (String folder : existingFolders) {
                                if (folder != null && !folders.contains(folder)) {
                                    folders.add(folder);
                                }
                            }
                        }
                        // Update UI di thread utama
                        ayatHolder.itemView.post(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                    android.R.layout.simple_spinner_item, folders);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            folderSpinner.setAdapter(adapter);
                        });
                    } catch (Exception e) {
                        ayatHolder.itemView.post(() -> ayatHolder.showError("Gagal memuat folder: " + e.getMessage()));
                    }
                });

                builder.setView(dialogView);
                builder.setPositiveButton("Simpan", (dialog, which) -> {
                    String selectedFolder = folderSpinner.getSelectedItem() != null ?
                            folderSpinner.getSelectedItem().toString() : "Default";
                    String newFolder = newFolderEditText.getText().toString().trim();

                    // Buat salinan final dari selectedFolder
                    final String finalSelectedFolder;
                    if (!newFolder.isEmpty()) {
                        finalSelectedFolder = newFolder;
                    } else {
                        finalSelectedFolder = selectedFolder;
                    }

                    Bookmark bookmark = new Bookmark(
                            surahId,
                            surahName,
                            ayat.getNumberInSurah(),
                            ayat.getText() != null ? ayat.getText() : "",
                            ayat.getTranslation() != null ? ayat.getTranslation() : "Terjemahan tidak tersedia"
                    );
                    bookmark.setFolder(finalSelectedFolder);
                    bookmark.setLastRead(false);

                    executorService.execute(() -> {
                        try {
                            AppDatabase.getInstance(context).bookmarkDao().insert(bookmark);
                            // Beri umpan balik ke pengguna di thread utama
                            ayatHolder.itemView.post(() -> {
                                Toast.makeText(context, "Bookmark berhasil disimpan ke folder: " + finalSelectedFolder,
                                        Toast.LENGTH_SHORT).show();
                            });
                        } catch (Exception e) {
                            ayatHolder.itemView.post(() -> {
                                ayatHolder.showError("Gagal menyimpan bookmark: " + e.getMessage());
                                Toast.makeText(context, "Gagal menyimpan bookmark: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                });
                builder.setNegativeButton("Batal", null);
                try {
                    builder.show();
                } catch (Exception e) {
                    ayatHolder.showError("Gagal menampilkan dialog: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ayatList.size() + 1; // Tambah 1 untuk header
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
        executorService.shutdown();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvHeader);
        }
    }

    static class AyatViewHolder extends RecyclerView.ViewHolder {
        TextView tvAyatNumber, tvArabic, tvTranslation;
        ImageButton btnPlay, btnPause, btnBookmark;

        AyatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAyatNumber = itemView.findViewById(R.id.tvAyatNumber);
            tvArabic = itemView.findViewById(R.id.tvArabic);
            tvTranslation = itemView.findViewById(R.id.tvTranslation);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnPause = itemView.findViewById(R.id.btnPause);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
        }

        void showError(String message) {
            tvTranslation.setText(message);
            tvTranslation.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    public interface AyatListener {
        void saveLastRead(Ayat ayat);
    }
}