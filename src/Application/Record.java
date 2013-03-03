package Application;

import java.io.Serializable;

public class Record implements Serializable{
	private static final long serialVersionUID = -284571681094969658L;
	private int ID;
	private Patient patient;
	private Doctor doctor;
	private Nurse nurse;
	private String data;
	private Division division;

	public Record(int id, Patient patient, Doctor doctor, Nurse nurse,
			Division division, String data) {
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
	
	@Override
	public String toString() {
		return "Id: " + ID + ", Patient: " + patient.getName() + ", Doctor: "
				+ doctor.getName() + ", Nurse: " + nurse.getName()
				+ ", Division: " + division.getName() + ", Data: " + data;
	}
}
