package de.kempalab.msdps.log;

import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

public class MyLogger extends Logger {
	
	boolean debugEnabled = false;

	protected MyLogger(String name) {
		super(LoggerContext.getContext(), name, null);
	}
	
	@SuppressWarnings("rawtypes")
	public static MyLogger getLogger(Class inputClass) {
		return new MyLogger(inputClass.getName());
	}
	
	@Override
	public boolean isDebugEnabled() {
		return debugEnabled;
	}
	
	public void enableDebug() {
		this.debugEnabled = true;
	}
	
	public void disableDebug() {
		this.debugEnabled = false;
	}
	
	public void infoValue(String info, Object object) {
		super.info(info + ":\t" + object);
	}
	
	public void infoConcat(String info1, String info2) {
		super.info(info1 + " " + info2);
	}
	
	public void warnValue(String warning, Object object) {
		super.warn(warning + ":\t" + object);
	}
	
	public void debugValue(String debug, Object object) {
		if (isDebugEnabled()) {
			super.debug(debug + ":\t" + object);
		}
	}
	
	public void debug(Object object) {
		if (isDebugEnabled()) {
			super.debug(object);
		}
	}
	
	public void horizontalLine() {
		System.out.println("\n_________________________________________________________________________________________________________\n");
	}
	
	public void debugHorizontalLine() {
		if (isDebugEnabled()) {
			System.out.println("\n_________________________________________________________________________________________________________\n");
		}
	}

}
