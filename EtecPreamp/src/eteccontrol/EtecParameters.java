package eteccontrol;

import java.io.Serializable;

public class EtecParameters implements Serializable, Cloneable {

	public static final long serialVersionUID = 1L;
	
	private static final double[] gains = {0., 10., 20., 30., 40.};
	private static final int[] gainBits = {0, 1, 3, 5, 7};
	private static final double[] filters = {10., 100., 2000., 10000., 20000.};
	private static final int[] filterBits = {7, 5, 3, 1, 0};
	
	public int filterIndex = 3;
	
	public int gainIndex = 1;
	
	public boolean differential = true;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public EtecParameters clone() {
		try {
			return (EtecParameters) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @return the filterIndex
	 */
	public int getFilterIndex() {
		return filterIndex;
	}

	/**
	 * @param filterIndex the filterIndex to set
	 */
	public void setFilterIndex(int filterIndex) {
		this.filterIndex = filterIndex;
	}

	/**
	 * @return the gainIndex
	 */
	public int getGainIndex() {
		return gainIndex;
	}

	/**
	 * @param gainIndex the gainIndex to set
	 */
	public void setGainIndex(int gainIndex) {
		this.gainIndex = gainIndex;
	}

	/**
	 * @return the differential
	 */
	public boolean isDifferential() {
		return differential;
	}

	/**
	 * @param differential the differential to set
	 */
	public void setDifferential(boolean differential) {
		this.differential = differential;
	}
	
	/**
	 * 
	 * @return The current gain in dB
	 */
	public double getGain() {
		return getGain(gainIndex);
	}
	
	/**
	 * 
	 * @param gainIndex gain index
	 * @return the gain for the given gain index
	 */
	public double getGain(int gainIndex) {
		double g = gains[gainIndex];
		if (differential) g+= 6;
		return g;
	}
	
	/**
	 * 
	 * @return the current filter frequency
	 */
	public double getFilter() {
		return getFilter(filterIndex);
	}

	/**
	 * 
	 * @param filterIndex filter index
	 * @return the filter frequency for the given filter index
	 */
	public double getFilter(int filterIndex) {
		return filters[filterIndex];
	}

	/**
	 * 
	 * @return the number of available gain settings
	 */
	public int getNumGains() {
		return gains.length;
	}
	
	/**
	 * @return the number of available filter settings
	 */
	public int getNumFilters() {
		return filters.length;
	}
	
	/**
	 * 
	 * @return bits to set in the Arduino for the current filter .
	 */
	public int getFilterBite() {
		return filterBits[filterIndex];
	}
	
	/**
	 * 
	 * @return bits to set in the Arduino for the current gain
	 */
	public int getGainBits() {
		return gainBits[gainIndex];
	}
}
