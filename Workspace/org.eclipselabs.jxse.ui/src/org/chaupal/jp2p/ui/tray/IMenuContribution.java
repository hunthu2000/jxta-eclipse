package org.chaupal.jp2p.ui.tray;

public interface IMenuContribution {

	public String getMenuLabel();
	
	public boolean isEnabled();
	
	public void performmMenuAction();
}
