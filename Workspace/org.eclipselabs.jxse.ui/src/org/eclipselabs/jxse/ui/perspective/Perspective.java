package org.eclipselabs.jxse.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipselabs.jxse.ui.console.JxseConsole;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);

		MessageConsole console = new JxseConsole();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);

		
		layout.setFixed(false);
		
	}

}
