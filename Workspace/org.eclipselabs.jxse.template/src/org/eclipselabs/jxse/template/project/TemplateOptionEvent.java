package org.eclipselabs.jxse.template.project;

import java.util.EventObject;

import org.eclipselabs.jxse.template.project.ContextWizardOption.TemplateOptions;

public class TemplateOptionEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private TemplateOptions option;
	public TemplateOptionEvent(Object arg0, TemplateOptions option) {
		super(arg0);
		this.option = option;
	}
	
	TemplateOptions getOption() {
		return option;
	}
}
