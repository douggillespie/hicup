package eteccontrol;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamProcess;

public class EtecDataBlock extends PamDataBlock<EtecDataUnit> {

	public EtecDataBlock(PamProcess parentProcess, int channelMap) {
		super(EtecDataUnit.class, "Etec Preamp", parentProcess, channelMap);
	}

}
