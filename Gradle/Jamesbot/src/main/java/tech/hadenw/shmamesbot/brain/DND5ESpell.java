package tech.hadenw.shmamesbot.brain;

public class DND5ESpell {
	private String spellName;
	private String spellDesc;
	private String spellSchool;
	private String spellComponents;
	private String spellCastTime;
	private String spellRange;
	private String spellDuration;
}

/*
 // Hoping to serialize this class as spell data for a new James feature.
[
	{
		spellName: "Cure Wounds",
		spellDesc: "A creature you touch regains a number of hit points equal to 1d8 + your spellcasting ability modifier. This spell has no effect on undead or constructs.",
		spellSchool: "Evocation"
		spellComponents: "V, S",
		spellCastTime: "1 action",
		spellRange: "Touch",
		spellDuration: "Instantaneous"
	},
	{
		spellName: "",
		spellDesc: "",
		spellSchool: ""
		spellComponents: "",
		spellCastTime: "",
		spellRange: "",
		spellDuration: ""
	}
]
*/