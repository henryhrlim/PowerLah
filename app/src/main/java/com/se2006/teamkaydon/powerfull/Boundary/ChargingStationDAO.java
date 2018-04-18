package com.se2006.teamkaydon.powerfull.Boundary;

import android.database.Cursor;

/**
 * Data Access Object that provides a list of functions to read the ChargingStationLocations
 * database.
 */
public interface ChargingStationDAO {
    Cursor retrieveData();
}