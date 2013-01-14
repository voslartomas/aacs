package cz.ssakhk.androidaudiocarsystem.helper;

/**
 * 
 * @author 
 *
 */
public class TrackPointItem {
	
	/**
	 * Souradnice bodu.
	 */
	private double longtitude, latitude;
	
	/**
	 * Cas ulozeni souradnice.
	 */
	private long time;
	
	/**
	 * Konstruktor tridy
	 * @param longtitude
	 * @param latitude
	 * @param time
	 */
	public TrackPointItem(double longtitude, double latitude, long time) {
		super();
		this.longtitude = longtitude;
		this.latitude = latitude;
		this.time = time;
	}

	/**
	 * @return the longtitude
	 */
	public double getLongtitude() {
		return longtitude;
	}

	/**
	 * @param longtitude the longtitude to set
	 */
	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
}