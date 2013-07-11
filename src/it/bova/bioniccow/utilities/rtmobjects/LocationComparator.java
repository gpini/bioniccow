package it.bova.bioniccow.utilities.rtmobjects;

import it.bova.rtmapi.Location;

import java.util.Comparator;

public class LocationComparator implements Comparator<Location> {
	@Override public int compare(Location loc1, Location loc2) {
      return loc1.getName().compareToIgnoreCase(loc2.getName());
   }
}
