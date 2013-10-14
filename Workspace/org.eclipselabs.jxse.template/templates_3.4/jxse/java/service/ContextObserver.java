package $packageName$.service;

import net.osgi.jxse.builder.ICompositeBuilderListener;
import net.osgi.jxse.factory.ComponentFactoryEvent;

public class ContextObserver implements ICompositeBuilderListener {

	public ContextObserver() {
		System.out.println( this.getClass().getName() + ": " + "Starting to Observe.");
	}

	@Override
	public void notifyCreated(ComponentFactoryEvent event) {
		System.out.println( this.getClass().getName() + ": " + event.getFactoryEvent().toString() + "=>" + event.getFactory().getComponentName() );
	}
}
