package org.encog.bot.spider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

import org.encog.bot.spider.Spider;
import org.encog.bot.spider.SpiderParseHTML;
import org.encog.bot.spider.SpiderReportable;
import org.encog.util.HSQLUtil;
import org.encog.util.orm.SessionManager;

public class TestSpiderMemory extends TestCase implements SpiderReportable {

	private String base = "www.httprecipes.com";

	private int urlsProcessed;

	public void testSpider() throws Exception {
		SessionManager manager = HSQLUtil.getSessionManager();

		
		Spider spider = new Spider(manager, this);
		spider.process(new URL("http://www.httprecipes.com"));
		TestCase.assertTrue(this.urlsProcessed > 100);

	}


	public void init(Spider spider) {
		// TODO Auto-generated method stub

	}

	public boolean spiderFoundURL(URL url, URL source, URLType type) {
		if (type != URLType.HYPERLINK) {
			return true;
		} else if ((this.base != null)
				&& (!this.base.equalsIgnoreCase(url.getHost()))) {
			return false;
		}

		return true;
	}

	public void spiderProcessURL(URL url, InputStream stream)
			throws IOException {
	}

	public void spiderProcessURL(URL url, SpiderParseHTML parse)
			throws IOException {
		
			parse.readAll();
		

		this.urlsProcessed++;

	}

	public void spiderURLError(URL url) {
		// TODO Auto-generated method stub

	}



}
