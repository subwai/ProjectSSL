package Application;

import java.util.ArrayList;
import java.util.List;

public class RecordHandler {
	private ArrayList<Division> divisions;
	ArrayList<Record> records;
	ArrayList<Person> users;
	
	public RecordHandler(ArrayList<Record> records, ArrayList<Person> users, ArrayList<Division> divisions){
		this.divisions = divisions;
		this.records = records;
		this.divisions = divisions;
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
