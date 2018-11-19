package de.kempalab.msdps.constants;

/**
 * A {@link FragmentKey} identifies a fragment derived from a certain metabolite by its characteristic mass.
 * @author sfuerst
 *
 */
public enum FragmentKey {
	//TODO: complete the list of keys
	LAC_117(MetaboliteKey.LAC, 117),
	LAC_191(MetaboliteKey.LAC, 191),
	LAC_219(MetaboliteKey.LAC, 219),
	ASP_130(MetaboliteKey.ASP,	130),
	ASP_160(MetaboliteKey.ASP,	160),
	ASP_218(MetaboliteKey.ASP,	218),
	ASP_232(MetaboliteKey.ASP,	232),
	ASP_306(MetaboliteKey.ASP,	306),
	MAL_233(MetaboliteKey.MAL, 233),
	MAL_245(MetaboliteKey.MAL, 245),
	MAL_335(MetaboliteKey.MAL, 335),
	GLC_160(MetaboliteKey.GLC, 160),
	GLC_205(MetaboliteKey.GLC, 205),
	GLC_217(MetaboliteKey.GLC, 217),
	ALA_116(MetaboliteKey.ALA,	116),
	ALA_190(MetaboliteKey.ALA,	190),
	ALA_188(MetaboliteKey.ALA,	188),
	ALA_262(MetaboliteKey.ALA,	262),
	SER_116(MetaboliteKey.SER,	116),
	SER_132(MetaboliteKey.SER,	132),
	SER_188(MetaboliteKey.SER,	188),
	SER_204(MetaboliteKey.SER,	204),
	SER_218(MetaboliteKey.SER,	218),
	GLY_176(MetaboliteKey.GLY,	176),
	GLY_204(MetaboliteKey.GLY,	204),
	GLY_276(MetaboliteKey.GLY,	276),
	GLY_248(MetaboliteKey.GLY,	248),
	GLN_156(MetaboliteKey.GLN, 156),
	GLN_245(MetaboliteKey.GLN, 245),
	UNKNOWN(MetaboliteKey.UNKNOWN, 0),
	;
	private MetaboliteKey metaboliteKey;
	private int baseMass;
	
	FragmentKey(MetaboliteKey metaboliteKey, int baseMass) {
		this.metaboliteKey = metaboliteKey;
		this.baseMass = baseMass;
	}

	public int getBaseMass() {
		return baseMass;
	}
	
	public static FragmentKey byKeyName(String keyName) {
		for (FragmentKey key : FragmentKey.values()) {
			if (key.name().equals(keyName)) {
				return key;
			}
		}
		return UNKNOWN;
	}
	
	public static FragmentKey byMassAndAbbreviation(int mass, String abbreviation) {
		for (FragmentKey key : FragmentKey.values()) {
			if (key.getMetaboliteKey().getAbbreviation().equals(abbreviation) && key.getBaseMass() == mass) {
				return key;
			}
		}
		return UNKNOWN;
	}

	/**
	 * @return the metaboliteKey
	 */
	public MetaboliteKey getMetaboliteKey() {
		return metaboliteKey;
	}

}
