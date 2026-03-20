package eteccontrol;

import PamModel.PamDependency;
import PamModel.PamPluginInterface;
import PamView.PamViewInterface;

public class EtecPlugin implements PamPluginInterface {

	private String jarFile;

	@Override
	public String getDefaultName() {
		return "Etec Preamplifier Control";
	}

	@Override
	public String getHelpSetName() {
		return null;
	}

	@Override
	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	@Override
	public String getJarFile() {
		return jarFile;
	}

	@Override
	public String getDeveloperName() {
		return "Douglas Gillespie";
	}

	@Override
	public String getContactEmail() {
		return "support@pamguard.org";
	}

	@Override
	public String getVersion() {
		return "1.1";
	}

	@Override
	public String getPamVerDevelopedOn() {
		return "1.15 Beta";
	}

	@Override
	public String getPamVerTestedOn() {
		return "1.15 Beta";
	}

	@Override
	public String getAboutText() {
		return "Control of digitally controlled Etec amplifiers using an Arduino controller board";
	}

	@Override
	public String getClassName() {
		return EtecControl.class.getName();
	}

	@Override
	public String getDescription() {
		return "Etec Preamp Control";
	}

	@Override
	public String getMenuGroup() {
		return "Utilities";
	}

	@Override
	public String getToolTip() {
		return "Control of digitally controlled Etec amplifiers using an Arduino controller board";
	}

	@Override
	public PamDependency getDependency() {
		return (new PamDependency(null, "arduino.ArduinoControl"));
	}

	@Override
	public int getMinNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxNumber() {
		return 1;
	}

	@Override
	public int getNInstances() {
		return 0;
	}

	@Override
	public boolean isItHidden() {
		return false;
	}

	@Override
	public int allowedModes() {
		return PamPluginInterface.ALLMODES;
	}

}
