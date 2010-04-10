package org.encog.cloud;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.encog.bot.BotUtil;
import org.encog.parse.tags.read.ReadXML;
import org.encog.util.http.FormUtility;

public class CloudRequest {

	private Map<String, String> headerProperties = new HashMap<String, String>();
	private Map<String, String> sessionProperties = new HashMap<String, String>();
	private Map<String, String> responseProperties = new HashMap<String, String>();

	private void handleResponse(String contents) {
		ByteArrayInputStream is = new ByteArrayInputStream(contents.getBytes());
		ReadXML xml = new ReadXML(is);
		int ch;

		while ((ch = xml.read()) != -1) {
			if (ch == 0) {
				if (xml.getTag().getName().equalsIgnoreCase("EncogCloud")) {
					processCloud(xml);
				}
			}
		}

		if (getStatus() == null || getStatus().equals("failed")) {
			throw new EncogCloudError(getMessage());
		}
	}

	private void processCloud(ReadXML xml) {
		int ch;

		while ((ch = xml.read()) != -1) {
			if (ch == 0) {
				if (xml.getTag().getName().equalsIgnoreCase("Header")) {
					this.headerProperties = xml.readPropertyBlock();
				} else if (xml.getTag().getName().equalsIgnoreCase("Session")) {
					this.sessionProperties = xml.readPropertyBlock();
				} else if (xml.getTag().getName().equalsIgnoreCase("Response")) {
					this.responseProperties = xml.readPropertyBlock();
				}
			}
		}
	}

	public String getStatus() {
		return this.headerProperties.get("status");
	}

	public String getMessage() {
		return this.headerProperties.get("message");
	}

	public String getService() {
		return this.headerProperties.get("service");
	}

	public String getSession() {
		return this.sessionProperties.get("url");
	}

	public String getResponseProperty(String key) {
		return this.responseProperties.get(key);
	}

	public void performURLGET(boolean async, String url) {
		try {
			if (async) {
				AsynchronousCloudRequest request = new AsynchronousCloudRequest(
						new URL(url));
				Thread t = new Thread(request);
				t.setDaemon(true);
				t.start();
			} else {
				URL url2 = new URL(url);
				URLConnection u = url2.openConnection();
				String contents = BotUtil.loadPage(u.getInputStream());
				handleResponse(contents);
			}
		} catch (IOException e) {
			throw new EncogCloudError(e);
		}
	}

	public void performURLPOST(boolean async, String service,
			Map<String, String> args) {
		try {
			if (async) {
				AsynchronousCloudRequest request = new AsynchronousCloudRequest(
						new URL(service), args);
				Thread t = new Thread(request);
				t.setDaemon(true);
				t.start();
			} else {
				URL url = new URL(service);
				URLConnection u = url.openConnection();
				u.setDoOutput(true);
				OutputStream os = u.getOutputStream();

				FormUtility form = new FormUtility(os, null);

				for (String key : args.keySet()) {
					form.add(key, args.get(key));
				}
				form.complete();

				String contents = BotUtil.loadPage(u.getInputStream());
				handleResponse(contents);
			}
		} catch (IOException e) {
			throw new EncogCloudError(e);
		}
	}

}
