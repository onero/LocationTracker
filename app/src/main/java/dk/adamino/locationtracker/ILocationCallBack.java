package dk.adamino.locationtracker;

import android.location.Location;

/**
 * Created by Adamino.
 */

public interface ILocationCallBack {

    /**
     * Set the velocity of the device
     * @param speed
     */
    void setVelocity(double speed);

    /**
     * Set the distance to our home location in meters
     * @param location
     */
    void setDistanceFromHome(Location location);
}
