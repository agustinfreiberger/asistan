package ar.edu.unicen.isistan.asistan.storage.database.wifi;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class WiFiDao {

    @Insert
    public abstract long insert(WiFiScan wifiScan);

    @Insert
    public abstract long[] insert(List<WiFiScan> wifiScans);

    @Query("DELETE FROM " + WiFiScan.TABLE_NAME + " WHERE time <= :until")
    public abstract int deleteUntil(long until);

    @Query("SELECT * FROM " + WiFiScan.TABLE_NAME + " WHERE time <= :until")
    public abstract List<WiFiScan> selectUntil(long until);

}
