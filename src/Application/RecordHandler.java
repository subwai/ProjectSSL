package Application;

import java.util.ArrayList;

public class RecordHandler {
	private ArrayList<Division> divisions;
	ArrayList<Record> records;
	ArrayList<Person> users;
	
	public RecordHandler(ArrayList<Record> records, ArrayList<Person> users, ArrayList<Division> divisions){
		this.divisions = divisions;
		this.records = records;
		this.divisions = divisions;
	}
	
	public boolean createRecord(String patient, String doctor, String nurse,
			String division, String data) {
		Patient p = null;
		Doctor d = null;
		Nurse n = null;
		Division div = null;
		for(Person per : users){
			if(per.getName().equals(patient) && per instanceof Patient){
				p=(Patient)per;
			}else if(per.getName().equals(doctor) && per instanceof Doctor){
				d=(Doctor)per;
			}else if(per.getName().equals(nurse) && per instanceof Doctor){
				n=(Nurse)per;
			}
		}
		if(p==null){
			p = new Patient(patient);
			users.add(p);
		}
		for(Division divi : divisions){
			if(divi.getName().equals(division)){
				div=divi;
			}
		}
		if(d==null)return false;
		if(n==null)return false;
		if(div==null)return false;
		Record record = new Record(p,d,n,data,div);
		records.add(record);
		return true;
	}

}
