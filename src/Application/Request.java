package Application;

import java.util.ArrayList;
import java.util.List;

public class Request {
	protected String action;
	protected List<String> args;

	public Request() {
		args = new ArrayList<String>();
	}
}
