package Application;

public class Record {
	private static int ID;
	private Patient patient;
	private Doctor doctor;
	private Nurse nurse;
	private String data;
	private Division division;

	public Record(int id, Patient patient, Doctor doctor, Nurse nurse, String data,
			Division division) {
		this.ID = id;
		this.patient = patient;
		this.data = data;
		this.doctor = doctor;
		this.nurse = nurse;
		this.division = division;
	}

	public String getData() {
		return data;
	}

	public Division getDivision() {
		return division;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public Nurse getNurse() {
		return nurse;
	}

	public Patient getPatient() {
		return patient;
	}
	public int getID(){
		return ID;
	}

}
