import java.io.Serializable;

/**
 * This class represents the "Cattle" processed by Soylent
 * Productions.
 * @author Max Fowler
 *
 */
public class Cattle implements Serializable {
	
	private String cattleName;
	private String randomID;
	private int age;

	public Cattle(String cattleName, String randomID, int age) {
		this.cattleName = cattleName;
		this.randomID = randomID;
		this.age = age;
	}
	
	public Cattle() {
		randomID = CattleStats.generateID();
		cattleName = CattleStats.generateName();
		age = CattleStats.generateAge();
	}

	public String toString(){
		return randomID;
	}
	
	public String cattleOutputData(){
		return String.format("%s: This cattle was named %s "
				+ "and was %d years old", randomID,cattleName, age);
	}

}
