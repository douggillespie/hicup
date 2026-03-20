/**
 * 
 */
package eteccontrol;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import PamUtils.FrequencyFormat;
import PamView.dialog.PamDialog;
import PamView.dialog.PamGridBagContraints;

/**
 * @author Doug
 *
 */
public class EtecDialogPanel {
	
	private JPanel mainPanel;
	
	private JComboBox<String> gainList;
	
	private JComboBox<String> filterList;
	
	private EtecParameters etecParameters;
	
	public EtecDialogPanel() {
		mainPanel = new JPanel();
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(new TitledBorder("Preamplifier Control"));
		GridBagConstraints c = new PamGridBagContraints();
		gainList = new JComboBox();
		filterList = new JComboBox();
		
		mainPanel.add(new JLabel("Preamp gain ", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(gainList, c);
		c.gridy++;
		c.gridx = 0;
		mainPanel.add(new JLabel("High pass filter ", JLabel.RIGHT), c);
		c.gridx++;
		mainPanel.add(filterList, c);
	}

	public JComponent getDialogComponent() {
		return mainPanel;
	}

	public void setParams(EtecParameters etecParameters) {
		this.etecParameters = etecParameters.clone();
		gainList.removeAllItems();
		int nGain = etecParameters.getNumGains();
		for (int i = 0; i < nGain; i++) {
			gainList.addItem(String.format("%3.1f dB", etecParameters.getGain(i)));
		}
		gainList.setSelectedIndex(etecParameters.gainIndex);
		
		filterList.removeAllItems();
		int nFilt = etecParameters.getNumFilters();
		for (int i = 0; i < nFilt; i++) {
			filterList.addItem(FrequencyFormat.formatFrequency(etecParameters.getFilter(i), true));
		}
		filterList.setSelectedIndex(etecParameters.filterIndex);
	}

	public EtecParameters getParams() {
		etecParameters.gainIndex = gainList.getSelectedIndex();
		etecParameters.filterIndex = filterList.getSelectedIndex();
		return etecParameters;
	}



}
