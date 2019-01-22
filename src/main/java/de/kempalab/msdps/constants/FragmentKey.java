package de.kempalab.msdps.constants;

/**
 * A {@link FragmentKey} identifies a fragment derived from a certain metabolite by its characteristic mass.
 * @author sfuerst
 *
 */
public enum FragmentKey {
	ALA_116(MetaboliteKey.ALA, 116), //
	ALA_190(MetaboliteKey.ALA, 190), //
	ALA_188(MetaboliteKey.ALA, 188), //
	ALA_262(MetaboliteKey.ALA, 262), //
	ASP_130(MetaboliteKey.ASP, 130), //
	ASP_160(MetaboliteKey.ASP, 160), //
	ASP_218(MetaboliteKey.ASP, 218), //
	ASP_232(MetaboliteKey.ASP, 232), //
	ASP_306(MetaboliteKey.ASP, 306), //
	CIT_273(MetaboliteKey.CIT, 273), //
	CIT_347(MetaboliteKey.CIT, 347), //
	CIT_375(MetaboliteKey.CIT, 375), //
	CIT_465(MetaboliteKey.CIT, 465), //
	DHAP_315(MetaboliteKey.DHAP, 315), //
	DHAP_400(MetaboliteKey.DHAP, 400), //
	FRC_205(MetaboliteKey.FRC, 205), //
	FRC_217(MetaboliteKey.FRC, 217), //
	FRC_307(MetaboliteKey.FRC, 307), //
	FBP_217(MetaboliteKey.FBP, 217), //
	FBP_315(MetaboliteKey.FBP, 315), //
	FBP_387(MetaboliteKey.FBP, 387), //
	F6P_217(MetaboliteKey.F6P, 217), //
	F6P_315(MetaboliteKey.F6P, 315), //
	F6P_387(MetaboliteKey.F6P, 387), //
	FUM_217(MetaboliteKey.FUM, 217), //
	FUM_245(MetaboliteKey.FUM, 245), //
	PGA6_217(MetaboliteKey.PGA6, 217), //
	PGA6_315(MetaboliteKey.PGA6, 315), //
	PGA6_387(MetaboliteKey.PGA6, 387), //
	GLC_160(MetaboliteKey.GLC, 160), //
	GLC_205(MetaboliteKey.GLC, 205), //
	GLC_217(MetaboliteKey.GLC, 217), //
	GLC_315(MetaboliteKey.GLC, 315), //
	G16P_217(MetaboliteKey.G16P, 217), //
	G16P_357(MetaboliteKey.G16P, 357), //
	G6P_217(MetaboliteKey.G6P, 217), //
	G6P_357(MetaboliteKey.G6P, 357), //
	GLU_230(MetaboliteKey.GLU, 230), //
	GLU_246(MetaboliteKey.GLU, 246), //
	GLU_348(MetaboliteKey.GLU, 348), //
	GLU_186(MetaboliteKey.GLU, 186), //
	GLU_276(MetaboliteKey.GLU, 276), //
	GLN_83(MetaboliteKey.GLN, 83),//
	GLN_156(MetaboliteKey.GLN, 156), //
	GLN_245(MetaboliteKey.GLN, 245), //
	HG2_231(MetaboliteKey.HG2, 231), //
	HG2_247(MetaboliteKey.HG2, 247), //
	HG2_349(MetaboliteKey.HG2, 349), //
	AKG_156(MetaboliteKey.AKG, 156), //
	AKG_198(MetaboliteKey.AKG, 198), //
	AKG_288(MetaboliteKey.AKG, 288), //
	GLT_233(MetaboliteKey.GLT, 233), //
	GLT_261(MetaboliteKey.GLT, 261), //
	PGA3_315(MetaboliteKey.PGA3, 315), //
	PGA3_357(MetaboliteKey.PGA3, 357), //
	PGA3_387(MetaboliteKey.PGA3, 387), //
	GLYC_218(MetaboliteKey.GLYC, 218), //
	GLYC_293(MetaboliteKey.GLYC, 293), //
	GLYC3P_315(MetaboliteKey.GLYC3P, 315), //
	GLYC3P_357(MetaboliteKey.GLYC3P, 357), //
	GLYC3P_387(MetaboliteKey.GLYC3P, 387), //
	GLY_176(MetaboliteKey.GLY, 176), //
	GLY_204(MetaboliteKey.GLY, 204), //
	GLY_276(MetaboliteKey.GLY, 276), //
	GLY_248(MetaboliteKey.GLY, 248), //
	LAC_117(MetaboliteKey.LAC, 117), //
	LAC_191(MetaboliteKey.LAC, 191), //
	LAC_219(MetaboliteKey.LAC, 219), //
	MAL_233(MetaboliteKey.MAL, 233), //
	MAL_245(MetaboliteKey.MAL, 245), //
	MAL_335(MetaboliteKey.MAL, 335), //
	PEP_211(MetaboliteKey.PEP, 211), //
	PEP_369(MetaboliteKey.PEP, 369), //
	PYR_158(MetaboliteKey.PYR, 158), //
	PYR_174(MetaboliteKey.PYR, 174), //
	PYR_189(MetaboliteKey.PYR, 189), //
	R5P_217(MetaboliteKey.R5P, 217), //
	R5P_315(MetaboliteKey.R5P, 315), //
	R5P_357(MetaboliteKey.R5P, 357), //
	SER_116(MetaboliteKey.SER, 116), //
	SER_132(MetaboliteKey.SER, 132), //
	SER_188(MetaboliteKey.SER, 188), //
	SER_204(MetaboliteKey.SER, 204), //
	SER_218(MetaboliteKey.SER, 218), //
	SUC_172(MetaboliteKey.SUC, 172), //
	SUC_247(MetaboliteKey.SUC, 247), //
	UNKNOWN(MetaboliteKey.UNKNOWN, 0), // 
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
