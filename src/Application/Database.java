package Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Database implements Serializable{

	private static final long serialVersionUID = 299569853749729381L;
	
	
	private Map<Integer, Record> records;
	private Map<String, Person> users;
	private Map<String, Division> divisions;
	

	public Database() {
		
		records = new HashMap<Integer, Record>();
		users = new HashMap<String, Person>();
		divisions = new HashMap<String, Division>();

		divisions.put("surgery", new Division("surgery"));
		divisions.put("quarantine", new Division("quarantine"));
		divisions.put("xray", new Division("xray"));

		users.put("johan", new Patient("johan"));
		users.put("sven", new Nurse("sven", divisions.get("surgery")));
		users.put("mergim", new Doctor("mergim", divisions.get("surgery")));
		users.put("adam", new Doctor("adam", divisions.get("surgery")));
		users.put("socialstyrelsen", new Admin("socialstyrelsen"));
	}

	public Person searchUsers(String person) {
		return users.get(person);
	}
	
	public HashMap<Integer, Record> listRecords(Person user) {
		return filter(records, new Predicate<Record>() {
			@Override
			public boolean apply(Record r, Person... args) {
				return args[0].hasReadAccess(r);
			}
		}, user);
	}

	public HashMap<Integer, Record> searchRecords(Person user, String patient) {
		return filter(records, new Predicate<Record>() {
			@Override
			public boolean apply(Record r, Person... args) {
				return args[0].hasReadAccess(r)
						&& (args[1] != null && args[1].equals(r.getPatient()));
			}
		}, user, users.get(patient));
	}

	public int createRecord(Person user, String patient, String doctor, String nurse,
			String division, String data) {
		Patient p = null;
		Doctor d = null;
		Nurse n = null;
		Division div = null;
		users.containsKey(patient);
		try {
			if (users.containsKey(patient)) {
				p = (Patient) users.get(patient);
			} else {
				users.put(patient, p = new Patient(patient));
			}
			
			d = (Doctor) users.get(doctor);
			n = (Nurse) users.get(nurse);
			div = divisions.get(division);
		} catch (Exception e) {
			return -1;
		}
		
		// -1 = wrong input. -2 = No access to create.
		if(d != user)
			return -2; 
		
		records.put(records.size() + 1, new Record(records.size() + 1, p, d, n, div, data));
		return records.size();
	}
	
	public int editData(Person user, int ID, String data){
		Record rec = null;
		for(Record r: records.values()){
			if(r.getID() == ID){
				rec = r;
			}
		}
		if(rec!=null){
			if(user.hasWriteAccess(rec)){
				rec.editData(data);
				return 1;
			}else{
				return 0; //no access.
			}
		}
		
		return -1; //no record with ID.
	}

	public boolean deleteRecord(int ID) {
		for (Record r : records.values()) {
			if (r.getID() == ID) {
				records.remove(ID);
				return true;
			}
		}
		return false;
	}

	protected <K, V> HashMap<K, V> filter(Map<K, V> map, Predicate<V> p, Person... args) {
		HashMap<K, V> filtered = new HashMap<K, V>();
	    for (Entry<K, V> e : map.entrySet()) {
	        if (p.apply(e.getValue(), args)) {
	        	filtered.put(e.getKey(), e.getValue());
	        }
		}
		return filtered;
	}
}
