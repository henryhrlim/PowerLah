package com.se2006.teamkaydon.powerfull.Boundary;

import android.database.Cursor;

/**
 * Data Access Object that provides a list of functions to read the ChargingStationLocations
 * database.
 *
 * @author Team Kaydon
 * @version 1.0
 * @since 2018-04-17
 */
public interface ChargingStationDAO {
    Cursor retrieveData();
}
