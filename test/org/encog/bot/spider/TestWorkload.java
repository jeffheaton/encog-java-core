package org.encog.bot.spider;

import java.net.URL;
import java.sql.Connection;

import org.encog.bot.spider.workload.SpiderWorkload;
import org.encog.bot.spider.workload.WorkloadItem;
import org.encog.bot.spider.workload.WorkloadStatus;
import org.encog.util.HSQLUtil;
import org.encog.util.orm.ORMSession;
import org.encog.util.orm.SessionManager;
import org.junit.Assert;

import junit.framework.TestCase;

public class TestWorkload extends TestCase {
	public void testWorkload() throws Throwable
	{
		URL url1 = new URL("http://www.httprecipes.com");
		URL url2 = new URL("http://www.httprecipes.com/one.html");
		URL url3 = new URL("http://www.httprecipes.com/two.html");
		SessionManager manager = HSQLUtil.getSessionManager();
		SpiderWorkload workload = new SpiderWorkload(manager);
		
		workload.addURL(url1, null);
		Assert.assertTrue(workload.countEligibleWorkloadItems()==1);
		WorkloadItem base = workload.obtainWork();
		Assert.assertNotNull(base);
		workload.updateStatus(base, WorkloadStatus.PROCESSED);
		base = workload.obtainWork();
		Assert.assertNull(base);
		
		
	}
}
