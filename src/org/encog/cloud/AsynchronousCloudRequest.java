package org.encog.cloud;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.encog.bot.BotUtil;
import org.encog.util.http.FormUtility;

public class AsynchronousCloudRequest implements Runnable {

	private final URL url;
	private final Map<String, String> params;

	public AsynchronousCloudRequest(URL url) {
		this.url = url;
		this.params = new HashMap<String, String>();
	}
	
	public AsynchronousCloudRequest(URL url, Map<String, String> params) {
		this.url = url;
		this.params = params;
	}

	public URL getUrl() {
		return url;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void run() {

		try {
			if (this.params.size() > 0) {
				URLConnection u = url.openConnection();
				u.setDoOutput(true);
				OutputStream os = u.getOutputStream();

				FormUtility form = new FormUtility(os, null);

				for (String key : params.keySet()) {
					form.add(key, params.get(key));
				}
				form.complete();

				BotUtil.loadPage(u.getInputStream());
			} else {
				URLConnection u = this.url.openConnection();
				BotUtil.loadPage(u.getInputStream());

			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
