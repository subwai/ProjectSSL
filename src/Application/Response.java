package Application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Response {
	public String message;

	// NOTE!!!! DO NOT add any attributes here, then they will be sent to the
	// client!

	// The purpuse of this function is to construct a message (and execute
	// associated logic) which is returned to the client.
	public void build(Request req, Person user, Database db, Logger logger) {

		

		String action = req.action;
		List<String> args = req.args;
		StringBuilder sb = new StringBuilder();
		if (action.equals("list")) {
			String patient = req.args.get(0);
			HashMap<Integer, Record> records;
			if (patient.equals("all")) {
				sb.append("List of all your available records:%n");
				records = db.listRecords(user);
				logger.info(user.getName() + " listed all availible records");
			} else {
				sb.append("List of all your available records for " + patient + ":%n");
				records = db.searchRecords(user, patient);
				logger.info(user.getName() + " listed all availible records for patient: " + patient);
			}

			sb.append("------------------------------------------------------------------- ");
			for (Record r : records.values()) {
				sb.append("%n" + r.toString());
			}
					
		} else if (action.equals("create")) {
			String patient = args.get(0);
			String doctor = args.get(1);
			String nurse = args.get(2);
			String division = args.get(3);
			String data = args.get(4);
			int ID = db.createRecord(user,patient, doctor, nurse, division, data);
			if (ID > 0) {
				sb.append("Record created successfully with id: " + ID + "%n");
				sb.append("And data %n");
				sb.append("------------------------------------------------------------------- %n");
				sb.append(data);
				logger.info(user.getName() + " created record with id: " + ID);
			} else if(ID==-1) {
				sb.append("Error: Could not create record, invalid input.");
				logger.info(user.getName() + " failed to create record. Reason: invalid input.");
			}else{
				sb.append("Error: Could not create record, no access.");
				logger.info(user.getName() + " failed to create record. Reason: insufficient access.");
			}
		} else if (action.equals("delete")) {
			String record = req.args.get(0);
			try {
				int rid = Integer.parseInt(record);
				db.deleteRecord(rid);
				sb.append("Record with ID: " + rid + " successfully deleted.");
				logger.info(user.getName() + " deleted record with id: " + rid);
			} catch(NumberFormatException e) {
				sb.append("Error: Could not delete record, invalid input.");
				logger.info(user.getName() + " failed to delete record. Reason: invalid input.");
			}

		} else if (action.equals("heartbeat")) {
			sb.append("");
		}else if (action.equals("edit")){
			int ID = Integer.parseInt(args.get(0));
			String edit = args.get(1);
			int success = db.editData(user, ID, edit);
			if(success==1)
				sb.append("Record: " + ID + " edited successfully.");
			if(success==0)
				sb.append("Failed to edit data. Reason: insufficient access.");
			if(success==-1)
				sb.append("Failed to edit data. Reason: no record with id: " + ID);
		}
		message = sb.toString();
	}
}
