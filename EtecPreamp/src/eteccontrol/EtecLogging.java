package eteccontrol;

import java.sql.Types;

import PamguardMVC.PamDataBlock;
import PamguardMVC.PamDataUnit;
import generalDatabase.PamTableDefinition;
import generalDatabase.PamTableItem;
import generalDatabase.SQLLogging;
import generalDatabase.SQLTypes;

public class EtecLogging extends SQLLogging {
	
	private PamTableItem gain, filter;

	protected EtecLogging(PamDataBlock pamDataBlock) {
		super(pamDataBlock);
		PamTableDefinition tableDef = new PamTableDefinition("Etec Preamplifier", UPDATE_POLICY_WRITENEW);
		tableDef.addTableItem(gain = new PamTableItem("Gain", Types.REAL));
		tableDef.addTableItem(filter = new PamTableItem("Filter", Types.REAL));
		setTableDefinition(tableDef);
	}

	@Override
	public void setTableData(SQLTypes sqlTypes, PamDataUnit pamDataUnit) {
		EtecDataUnit etecDataUnit = (EtecDataUnit) pamDataUnit;
		EtecParameters etecParams = etecDataUnit.getEtecParameters();
		gain.setValue((float) etecParams.getGain());
		filter.setValue((float) etecParams.getFilter());
	}

}
