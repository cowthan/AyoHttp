package org.ayo.http.callback;

public enum HttpProblem {
	
	/** ok */
	OK,

	/** not on the internet */
	OFFLINE, 

	/** http code is not 200-300，or request is time out */
	SERVER_ERROR, 

	/** http code is 200, but local code process failed */
	DATA_ERROR,

	/** http code is 200, but the business logic is fail */
	LOGIC_FAIL,

	/** don't know what happend*/
	UNKNOWN
}
