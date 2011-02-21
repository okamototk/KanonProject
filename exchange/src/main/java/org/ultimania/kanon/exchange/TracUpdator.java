package org.ultimania.kanon.exchange;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.ultimania.kanon.exchange.trac.TracXmlRpcClient;

import com.projity.pm.task.Task;

public class TracUpdator {
	Log log = LogFactory.getLog(TracImporter.class);
	private TracXmlRpcClient client;
	private String dateFormat = "yyyy/MM/dd";
	private String startField = "due_assign";
	private String endField = "due_close";
	private String parentField = "parents";
	private SimpleDateFormat format = new SimpleDateFormat(dateFormat);

	public TracUpdator(String tracurl, String username, String password) {
		log.debug("URL: " + tracurl);
		log.debug("Username: " + username);
		log.debug("Password: " + password);
		log.debug("Start field in trac: " + startField);
		log.debug("End field in trac: " + endField);
		log.debug("Parent field in trac: " + parentField);
		try {
			client = new TracXmlRpcClient(tracurl, username, password);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void update(LinkedList<Task> tasks) {
		for (Task t : tasks) {
			if (t.isDirty()) {
				HashMap<String,String> fields = new HashMap<String,String>();
				boolean isModified = false;

				log.info("#" + t.getId());
				log.info("U#" + t.getUniqueId());
				long lastStart = t.getLastSavedStart();
				long start = t.getStart();
				if(lastStart!=start){
					isModified = true;
					Date startDate = new Date(start);
					String tracStart = format.format(startDate);
					log.info("Update start:" + tracStart);
					fields.put(startField ,tracStart);
				}


				long lastFinish = t.getLastSavedFinish();
				long end = t.getEnd();
				if(lastFinish!=end){
					isModified = true;
					Date endDate = new Date(end);
					String tracEnd = format.format(endDate);
					log.info("Update end:" + tracEnd);
					fields.put(endField ,tracEnd);
				}
				try {
					if(isModified)
					client.update((int)t.getId(),fields,"update from Kanon Project.");
					t.setDirty(false);
					t.setLastSavedStart(start);
					t.setLastSavedFinish(end);
				} catch (XmlRpcException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

			}
		}

	}

}
