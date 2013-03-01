package Application;

import java.util.ArrayList;
import java.util.List;

public class RecordHandler {
	private static ArrayList<Record> records;
	private static ArrayList<Person> users;
	private static ArrayList<Division> divisions;
	
	public RecordHandler(){
		records = new ArrayList<Record>();
	 	 
	    users = new ArrayList<Person>();
	 	 	
	    divisions = new ArrayList<Division>();
	    Division surgery = new Division("surgery");
	    Division xray = new Division("xray");
	    Division quarantine = new Division("quarantine");
	    divisions.add(surgery);
	    divisions.add(xray);
	    divisions.add(quarantine);
	    users.add(new Patient("Johan"));
	    users.add(new Nurse("Sven", surgery));
	    users.add(new Doctor("Mergim", surgery));	
	    users.add(new Admin("socialstyrelsen"));
	}
	public ArrayList<Person> getUsers(){
		return users;
	}
	
	public int createRecord(String patient, String doctor, String nurse,
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
		if(d==null)return -1;
		if(n==null)return -1;
		if(div==null)return -1;
		int size = records.size();
		Record record = new Record(size,p,d,n,data,div);
		records.add(record);
		return size;
	}
	
	public boolean deleteRecord(int ID) {
		for(Record r: records){
			if(r.getID() == ID){
				records.remove(ID);
				return true;
			}
		}
		return false;
	}
	public List<Record> listRecords(Person user) {
		return filter(records, new Predicate<Record>(new Object[] { user }) {
			@Override
			public boolean apply(Record r) {
				return ((Person) this.args[0]).hasReadAccess(r);
			}
		});
	}

	public List<Record> searchRecords(Person user, String patient) {
		return filter(records, new Predicate<Record>(new Object[] { user,
				patient }) {
			@Override
			public boolean apply(Record r) {
				return ((Person) this.args[0]).hasReadAccess(r)
						&& ((String) this.args[1]).equalsIgnoreCase(r
								.getPatient().getName());
			}
		});
	}
	protected static <T> ArrayList<T> filter(ArrayList<T> target,
			Predicate<T> predicate) {
		ArrayList<T> result = new ArrayList<T>();
		for (T element : target) {
			if (predicate.apply(element)) {
				result.add(element);
			}
		}
		return result;
	}
}
