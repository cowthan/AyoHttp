
package org.ayo.http.volly;

import com.android.volley.Request;
import com.android.volley.error.AuthFailureError;
import com.android.volley.request.MultiPartXRequest;
import com.android.volley.toolbox.HttpStack;
import com.dongqiudi.news.BaseApplication;
import com.dongqiudi.news.utils.StethoHelper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by hujinghui on 16/10/22.
 */

/**
 * OkHttp backed {@link com.android.volley.toolbox.HttpStack HttpStack} that does not
 * use okhttp-urlconnection
 */
public class OkHttp3Stack implements HttpStack {

	public OkHttp3Stack() {
	}

	@Override
	public HttpResponse performRequest(com.android.volley.Request<?> request,
                                       Map<String, String> additionalHeaders)
			throws IOException, AuthFailureError {
		return performRequest(request, additionalHeaders, true);
	}

	/**
	 *
	 * @param request
	 * @param additionalHeaders
	 * @param retryWhenSignInvalid 是否需要重试加密串接口
	 * @return
	 * @throws IOException
	 * @throws AuthFailureError
	 */
	public HttpResponse performRequest(com.android.volley.Request<?> request,
                                       Map<String, String> additionalHeaders,
                                       boolean retryWhenSignInvalid)
			throws IOException, AuthFailureError {
		if (retryWhenSignInvalid && HttpSignUtil.isLocalSignInvalid()) {
			retryWhenSignInvalid = false;
			try {
				HttpSignUtil.requestSync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		if (AppUtils.isDnsOn(BaseApplication.getInstance())) {
			clientBuilder.addInterceptor(HttpDnsUtils.getDnsInterceptor());
		}
		int timeoutMs = request.getTimeoutMs();
		if (BaseApplication.DEBUG)
			StethoHelper.addNetworkInterceptor(clientBuilder);
		clientBuilder.connectTimeout(timeoutMs, TimeUnit.MILLISECONDS);
		clientBuilder.readTimeout(timeoutMs, TimeUnit.MILLISECONDS);
		clientBuilder.writeTimeout(timeoutMs, TimeUnit.MILLISECONDS);

		okhttp3.Request.Builder okHttpRequestBuilder = new okhttp3.Request.Builder();
		String url = request.getUrl();
		// 添加签名
		HttpUrl httpUrl = HttpUrl.parse(url);
		Map<String, String> signHeaders = HttpSignUtil.signHeader(httpUrl);
		okHttpRequestBuilder.url(httpUrl);
		Map<String, String> headers = request.getHeaders();
		if (headers != null)
			for (final String name : headers.keySet()) {
				if (!TextUtils.isEmpty(headers.get(name)))
					okHttpRequestBuilder.addHeader(name, headers.get(name));
			}
		if (signHeaders != null) {
			for (final String name : signHeaders.keySet()) {
				if (!TextUtils.isEmpty(signHeaders.get(name)))
					okHttpRequestBuilder.addHeader(name, signHeaders.get(name));
			}
		}
		for (final String name : additionalHeaders.keySet()) {
			if (!TextUtils.isEmpty(additionalHeaders.get(name)))
				okHttpRequestBuilder.addHeader(name, additionalHeaders.get(name));
		}

		setConnectionParametersForRequest(okHttpRequestBuilder, request);

		OkHttpClient client = clientBuilder.build();
		okhttp3.Request okHttpRequest = okHttpRequestBuilder.build();
		Call okHttpCall = client.newCall(okHttpRequest);
		Response okHttpResponse = okHttpCall.execute();
		if (retryWhenSignInvalid && okHttpResponse.code() == HttpSignUtil.SIGN_INVALID_ERROR_CODE) {
			try {
				if (HttpSignUtil.requestSync()) {
					return performRequest(request, additionalHeaders, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		StatusLine responseStatus = new BasicStatusLine(parseProtocol(okHttpResponse.protocol()),
				okHttpResponse.code(), okHttpResponse.message());
		BasicHttpResponse response = new BasicHttpResponse(responseStatus);
		response.setEntity(entityFromOkHttpResponse(okHttpResponse));
		Headers responseHeaders = okHttpResponse.headers();
		for (int i = 0, len = responseHeaders.size(); i < len; i++) {
			final String name = responseHeaders.name(i), value = responseHeaders.value(i);
			if (name != null) {
				response.addHeader(new BasicHeader(name, value));
			}
		}
		return response;
	}

	private static HttpEntity entityFromOkHttpResponse(Response r) throws IOException {
		BasicHttpEntity entity = new BasicHttpEntity();
		ResponseBody body = r.body();

		entity.setContent(body.byteStream());
		entity.setContentLength(body.contentLength());
		entity.setContentEncoding(r.header("Content-Encoding"));
		if (body.contentType() != null) {
			entity.setContentType(body.contentType().type());
		}
		return entity;
	}

	@SuppressWarnings("deprecation")
	private static void setConnectionParametersForRequest(okhttp3.Request.Builder builder, com.android.volley.Request<?> request)
			throws IOException, AuthFailureError {
		switch (request.getMethod()) {
			case Request.Method.DEPRECATED_GET_OR_POST:
				// Ensure backwards compatibility.  Volley assumes a request with a null body is a GET.
				byte[] postBody = request.getPostBody();
				if (postBody != null) {
					builder.post(RequestBody.create(MediaType.parse(request.getPostBodyContentType()), postBody));
				}
				break;
			case Request.Method.GET:
				builder.get();
				break;
			case Request.Method.DELETE:
				builder.delete();
				break;
			case Request.Method.POST:
				if (request instanceof MultiPartXRequest) {
					builder.post(createMultipartRequestBody((MultiPartXRequest<?>) request));
				} else {
					builder.post(createRequestBody(request));
				}
				break;
			case Request.Method.PUT:
				builder.put(createRequestBody(request));
				break;
			case Request.Method.HEAD:
				builder.head();
				break;
			case Request.Method.OPTIONS:
				builder.method("OPTIONS", null);
				break;
			case Request.Method.TRACE:
				builder.method("TRACE", null);
				break;
			case Request.Method.PATCH:
				builder.patch(createRequestBody(request));
				break;
			default:
				throw new IllegalStateException("Unknown method type.");
		}
	}

	private static ProtocolVersion parseProtocol(final Protocol p) {
		switch (p) {
			case HTTP_1_0:
				return new ProtocolVersion("HTTP", 1, 0);
			case HTTP_1_1:
				return new ProtocolVersion("HTTP", 1, 1);
			case SPDY_3:
				return new ProtocolVersion("SPDY", 3, 1);
			case HTTP_2:
				return new ProtocolVersion("HTTP", 2, 0);
		}

		throw new IllegalAccessError("Unkwown protocol");
	}

	private static RequestBody createRequestBody(Request r) throws AuthFailureError {
		final byte[] body = r.getBody();
		if (body == null) {
			return null;
		}
		return RequestBody.create(MediaType.parse(r.getBodyContentType()), body);
	}

	private static RequestBody createMultipartRequestBody(MultiPartXRequest<?> request)
			throws AuthFailureError {
		List<MultiPartXRequest.MultiFileParam> list = request.getMultiFileParams();
		MultipartBody.Builder builder = new MultipartBody.Builder()
				.setType(MultipartBody.FORM);
		for (MultiPartXRequest.MultiFileParam param : list) {
			builder.addFormDataPart(param.name, param.filename,
					RequestBody.create(MediaType.parse(param.mimeType),
							new File(param.path)));
		}
		Map<String, MultiPartXRequest.MultiPartParam> paramMap = request.getMultipartParams();
		if (paramMap != null && !paramMap.isEmpty()) {
			Iterator<String> it = paramMap.keySet().iterator();
			String key;
			MultiPartXRequest.MultiPartParam value;
			while (it.hasNext()) {
				key = it.next();
				if (TextUtils.isEmpty(key))
					continue;
				value = paramMap.get(key);
				if (value == null || TextUtils.isEmpty(value.value))
					continue;
				builder.addFormDataPart(key, value.value);
			}
		}
		final byte[] body = request.getBody();
		if (body != null)
			builder.addPart(RequestBody.create(MediaType.parse(request.getBodyContentType()), body));
		return builder.build();
	}

}