package Application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Database implements Serializable{

	private static final long serialVersionUID = 299569853749729381L;
	
	
	private ArrayList<Record> records;
	private ArrayList<Person> users;
	private ArrayList<Division> divisions;
	

	public Database() {
		
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
		users.add(new Doctor("Adam", surgery));
		users.add(new Admin("socialstyrelsen"));
	}

	public Person searchUser(String username) {
		ArrayList<Person> pArr = filter(users, new Predicate<Person>(
				new String[] { username }) {
			@Override
			public boolean apply(Person p) {
				return ((String) this.args[0]).equalsIgnoreCase(p.getName());
			}
		});
		return pArr.size() > 0 ? pArr.get(0) : null;
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

	public int createRecord(Person user, String patient, String doctor, String nurse,
			String division, String data) {
		Patient p = null;
		Doctor d = null;
		Nurse n = null;
		Division div = null;
		for (Person per : users) {
			if (per.getName().equals(patient) && per instanceof Patient) {
				p = (Patient) per;
			} else if (per.getName().equals(doctor) && per instanceof Doctor) {
				d = (Doctor) per;
			} else if (per.getName().equals(nurse) && per instanceof Nurse) {
				n = (Nurse) per;
			}
		}
		
		if (p == null) {
			p = new Patient(patient);
			users.add(p);
		}
		
		for (Division divi : divisions) {
			if (divi.getName().equals(division)) {
				div = divi;
			}
		}
		
		// -1 = wrong input. -2 = No access to create. //TODO: why not use OO? user.hasWriteAccess(record)
		if (d == null)
			return -1;
		if (n == null)
			return -1;
		if (div == null)
			return -1;
		if(d != user && !(user instanceof Admin))
			return -2; 
		int id = records.size() + 1;
		Record record = new Record(id, p, d, n, div, data);
		records.add(record);
		return id;
	}
	
	public String getData(int ID){
		String data = null;
		for(Record r: records){
			if(r.getID() == ID){
				data = r.getData();
			}
		}
		return data;
	}

	public boolean deleteRecord(int ID) {
		for (Record r : records) {
			if (r.getID() == ID) {
				records.remove(ID);
				return true;
			}
		}
		return false;
	}

	protected <T> ArrayList<T> filter(ArrayList<T> target,
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
