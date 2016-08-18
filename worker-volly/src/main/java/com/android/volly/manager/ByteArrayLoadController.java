package com.android.volly.manager;


import com.android.volly.NetworkResponse;
import com.android.volly.Response;
import com.android.volly.VolleyError;

/**
 * ByteArrayLoadController implements Volley Listener & ErrorListener
 * 
 * @author panxw
 * 
 */
public class ByteArrayLoadController extends AbsLoadController implements
		Response.Listener<NetworkResponse>, Response.ErrorListener {

	private LoadListener mOnLoadListener;

	private int mAction = 0;

	public ByteArrayLoadController(LoadListener requestListener, int actionId) {
		this.mOnLoadListener = requestListener;
		this.mAction = actionId;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		String errorMsg = null;
		if (error.getMessage() != null) {
			errorMsg = error.getMessage();
		} else {
			try {
				errorMsg = "Server Response Error ("
						+ error.networkResponse.statusCode + ")";
			} catch (Exception e) {
				errorMsg = "Server Response Error";
			}
		}
		this.mOnLoadListener.onError(errorMsg, getOriginUrl(), this.mAction);
	}

	@Override
	public void onResponse(NetworkResponse response) {
		this.mOnLoadListener.onSuccess(response.data, response.headers,
				getOriginUrl(), this.mAction);
	}
}