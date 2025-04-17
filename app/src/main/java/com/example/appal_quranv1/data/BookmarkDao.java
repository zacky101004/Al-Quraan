package com.example.appal_quranv1.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.appal_quranv1.model.Bookmark;
import java.util.List;

@Dao
public interface BookmarkDao {
    @Insert
    void insert(Bookmark bookmark);

    @Delete
    void delete(Bookmark bookmark);

    @Query("SELECT * FROM bookmarks")
    List<Bookmark> getAllBookmarks();

    @Query("SELECT * FROM bookmarks WHERE isLastRead = 1 LIMIT 1")
    Bookmark getLastRead();

    @Query("UPDATE bookmarks SET isLastRead = 0 WHERE isLastRead = 1")
    void clearLastRead();

    @Query("SELECT * FROM bookmarks WHERE folder = :folder")
    List<Bookmark> getBookmarksByFolder(String folder);

    @Query("SELECT DISTINCT folder FROM bookmarks")
    List<String> getAllFolders();
}