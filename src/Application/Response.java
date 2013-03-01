package Application;

import java.util.List;
import java.util.logging.Logger;

public class Response {
	public String message;
	//NOTE!!!! DO NOT add any attributes here, then they will be sent to the client!
	
	
	//The purpuse of this function is to construct a message (and execute associated logic) which is returned to the client.
	public void build(Request req, RecordHandler RH, Logger logger){
		
		//TODO: insert function calls in each if case:
		//I would recommend extracting the logic from Server into a db class or something
		
		String action = req.action;
		List<String> args = req.args;
		if(action.equals("list")){
			String patient = req.args.get(0);
			if(patient.equals("all")){
				message = "should list all (for the user) available records";
			}else{
				message = "should list records for " + patient;	
			}
			
		}else if(action.equals("create")){
			String patient = args.get(0);
			String doctor = args.get(1);
			String nurse = args.get(2);
			String division = args.get(3);
			String data = args.get(4);
			int ID = RH.createRecord(patient,doctor,nurse,data,division);
			if(ID>0){
				message = String.format("Record created succesfully with id: " + ID + "\n" +
						"And data \n" + "------------------------------------------------------------------- \n"
						 + data);
			}else{
				message = "Error: Could not create record, invalid input.";
			}
		}else if(action.equals("delete")){
			String record = req.args.get(0);
			message = "should delete record " + record;
			
		}else if(action.equals("heartbeat")){
			message = "";
		}
		
	}
}
