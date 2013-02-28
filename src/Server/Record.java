package Server;

public class Record {
	private Patient patient;
	private Doctor doctor;
	private Nurse nurse;
	private String data;
	private Division division;

	public Record(Patient patient, Doctor doctor, Nurse nurse, String data,
			Division division) {
		this.patient = patient;
		this.data = data;
		this.doctor = doctor;
		this.nurse = nurse;
		this.division = division;
	}

	public String getData() {
		return data;
	}

	public Patient getPatient() {
		return patient;
	}

	public Nurse getNurse() {
		return nurse;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public Division getDivision() {
		return division;
	}
	
	public boolean hasReadAccess(Person person){
		if(person == patient){
			return true;

		}
		if(person == nurse){
			return true;

		}
		if(person == doctor){
			return true;

		}
		if(person instanceof Admin){
			return true;
		}
	}
	
	public boolean hasWriteAccess)Person person)
	
	
	
}
