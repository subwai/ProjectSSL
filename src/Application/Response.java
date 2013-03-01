package Application;

import java.util.List;
import java.util.logging.Logger;

public class Response {
	public String message;

	// NOTE!!!! DO NOT add any attributes here, then they will be sent to the
	// client!

	// The purpuse of this function is to construct a message (and execute
	// associated logic) which is returned to the client.
	public void build(Request req, Person user, Database db, Logger logger) {

		// TODO: insert function calls in each if case:
		// I would recommend extracting the logic from Server into a db class or
		// something

		String action = req.action;
		List<String> args = req.args;
		StringBuilder sb = new StringBuilder();
		if (action.equals("list")) {
			String patient = req.args.get(0);
			List<Record> records;
			if (patient.equals("all")) {
				sb.append("List of all your available records:%n");
				records = db.listRecords(user);
			} else {
				sb.append("List of all your available records for " + patient + ":%n");
				records = db.searchRecords(user, patient);
			}

			sb.append("------------------------------------------------------------------- ");
			for (Record r : records) {
				sb.append("%n" + r.toString());
			}
					
		} else if (action.equals("create")) {
			String patient = args.get(0);
			String doctor = args.get(1);
			String nurse = args.get(2);
			String division = args.get(3);
			String data = args.get(4);
			int ID = db.createRecord(patient, doctor, nurse, division, data);
			if (ID > 0) {
				sb.append("Record created successfully with id: " + ID + "%n");
				sb.append("And data %n");
				sb.append("------------------------------------------------------------------- %n");
				sb.append(data);
			} else {
				sb.append("Error: Could not create record, invalid input.");
			}
		} else if (action.equals("delete")) {
			String record = req.args.get(0);
			try {
				int rid = Integer.parseInt(record);
				db.deleteRecord(rid);
				sb.append("Record with ID: " + rid + " successfully deleted.");
			} catch(NumberFormatException e) {
				sb.append("Error: Could not delete record, invalid input.");
			}

		} else if (action.equals("heartbeat")) {
			sb.append("");
		}
		message = sb.toString();
	}
}
