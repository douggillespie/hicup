package eteccontrol;

import PamguardMVC.PamDataUnit;

public class EtecDataUnit extends PamDataUnit {

	private EtecParameters etecParameters;

	public EtecDataUnit(long timeMilliseconds, EtecParameters etecParameters) {
		super(timeMilliseconds);
		this.etecParameters = etecParameters.clone();
	}

	/**
	 * @return the etecParameters
	 */
	public EtecParameters getEtecParameters() {
		return etecParameters;
	}

}
