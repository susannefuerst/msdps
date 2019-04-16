package de.kempalab.msdps.constants;
/**
 * An enumeration of error messages, used for the isotopeincorporation project
 * @author sfuerst
 *
 */
public enum ErrorMessage {
	INVALID_FORMULA("Cannot read formula. Please enter a valid format.", "A valid format does not contain any brackets."),
	INVALID_MASS_SHIFT_PATTERN("Cannot read MassShift from string.", "The input string does not match the ecpected pattern."),
	INTENSITY_TYPE_MISMATCH("Different intensity types", " "),
	SPECTRUM_TYPE_MISMATCH("Different spectrum types", " "),
	INVALID_ISOTOPE_NAME("No such isotope.", ""),
	NO_MID_DEFINITION("No MID definition for continuous spectra.", " "),
	NO_TRACER("There is no tracer defined for this element.", "");
	
 	private String message;
	private String detail;

	private ErrorMessage(String message, String detail) {
		this.message = message;
		this.detail = detail;
	}
	
	public String getMessage() {
		return message;
	}

	public String getDetail() {
		return detail;
	}
}
