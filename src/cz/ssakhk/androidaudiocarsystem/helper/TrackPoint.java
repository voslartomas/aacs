package cz.ssakhk.androidaudiocarsystem.helper;

import java.util.ArrayList;

/**
 * 
 * @author 
 *
 */
public class TrackPoint {
	
	/**
	 * Konstanta pro identifikaci prevodu - kilometru v hodine.
	 */
	public static final int KMH = 0;
	
	/**
	 * Konstanta pro identifikaci prevodu - mil v hodine.
	 */
	public static final int MPH = 1;
	
	/**
	 * Konstanta pro identifikaci prevodu - metrech za sekundu.
	 */
	public static final int MS = 2;
	
	/**
	 * Konstanta pro identifikaci nadpisu km/h.
	 */
	public static final String KMHTITLE = "km/h";
	
	/**
	 * Konstanta pro identifikaci nadpisu mp/h.
	 */
	public static final String MPHTITLE = "mph";
	
	/**
	 * Konstanta pro identifikaci nadpisu pro m/s.
	 */
	public static final String MSTITLE = "m/s";
	
	/**
	 * Pole pro ulozeni cesty, k vypoctu rychlosti.
	 */
	private ArrayList<TrackPointItem> route = null;
	
	/**
	 * Konstruktor tridy.
	 */
	public TrackPoint() {
		super();
		this.route = new ArrayList<TrackPointItem>();
	}

	/**
	 * Ulozi bod do cesty.
	 * @param tpi
	 */
	public void addPoint(TrackPointItem tpi){
		if(route.size() > 0) route.add(1, route.get(0));
		route.add(0, tpi);
	}
	
	/**
	 * Vraci aktualni rychlost vypocitanou z poslednich dvou ulozenych bodu.
	 * @return float
	 */
	public float getSpeed(){
		
		if(route.size() > 1){
		
			double ltActual = route.get(0).getLatitude();
			double lngActual = route.get(0).getLongtitude();
			
			double ltLast = route.get(1).getLatitude();
			double lngLast = route.get(1).getLongtitude();
			
			Long delta = route.get(0).getTime() - route.get(1).getTime();
	
			// vypocet rychlosti		
			return (float) Math.sqrt(Math.exp((ltActual - ltLast)) + Math.exp((lngActual - lngLast))) / delta;
		}else{
			return 0;
		}
	}
	
	/**
	 * Vraci nadpis jednotek.
	 * @param index
	 * @return String
	 */
	protected String getUnits(int index){
		
		String units;
		
		switch (index) {
		case KMH:
			units = KMHTITLE;
			break;
		
		case MPH:
			units = MPHTITLE;
			break;
			
		default:
			units = MSTITLE;
			break;
		}
		
		return units;
	}
	
	/**
	 * Prepocita rychlost do danych jednotek.
	 * @param speed
	 * @param units
	 * @return String
	 */
	public String calculateInto(float speed, int units) {
		
		float newValue;
		
		switch (units) {
			case KMH:
				newValue = (float) (speed * 3.6);
			break;
			case MPH:
				newValue = (float) (speed / 0.44704);
			break;
			default:
				// defaultni m/s
				newValue = speed;
			break;
		}
		
		return Float.toString(newValue) + getUnits(units);
	}
	
}
