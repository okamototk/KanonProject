package org.ultimania.kanon.exchange;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.swing.text.AsyncBoxView.ChildState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.ultimania.kanon.exchange.trac.Ticket;
import org.ultimania.kanon.exchange.trac.TracXmlRpcClient;

import com.projity.grouping.core.Node;
import com.projity.grouping.core.NodeFactory;
import com.projity.pm.assignment.Assignment;
import com.projity.pm.task.NormalTask;
import com.projity.pm.task.Project;
import com.projity.pm.task.ProjectFactory;
import com.projity.pm.task.Task;

import antlr.collections.List;

public class TracImporter {
	Log log = LogFactory.getLog(TracImporter.class);
	private TracXmlRpcClient client;
	// private static ResourceBundle bundle=
	// ResourceBundle.getBundle("fieldconfig");
	private String dateFormat = "yyyy/MM/dd";
	private SimpleDateFormat format = new SimpleDateFormat(dateFormat);
	private HashMap<Integer, NormalTask> map = new HashMap<Integer, NormalTask>();
	private ArrayList<Ticket> ticketList;
	private Project project;
	private String startField = "due_assign";
	private String endField = "due_close";
	private String parentField = "parents";
	private String completeField = "complete";
	private HashMap<Integer,ArrayList<Integer>> childMap = new HashMap<Integer,ArrayList<Integer>>();
	private ArrayList<Integer> rootNode = new ArrayList<Integer>();

	public TracImporter(String tracurl, String username, String password) {
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

	public Project getProject() {
		return project;
	}

	public String checkConnection() {
		return client.checkConnection();
	}

	public void importByQuery(String query) throws XmlRpcException {
		int[] list = client.query(query);
		ticketList = new ArrayList<Ticket>();
		for (int id : list) {
			ticketList.add(client.get(id));
		}
		importTicket();
		importHierarchy();
	}

	private long convertDate(Ticket t, String fieldName) {
		String tracField = t.$(fieldName);
		if (tracField != null && tracField.trim().length() != 0) {
			try {
				Date date = format.parse(tracField);
				return date.getTime()+TimeZone.getDefault().getRawOffset();
			} catch (ParseException e) {
				log.warn("#" + t.id + ": " + fieldName + "=" + tracField
						+ " is invalid format " + dateFormat, e);
			}
		}
		return 0;
	}

	private void importTicket(){
		ProjectFactory factory = ProjectFactory.getInstance();
		project = factory.createProject();
		project.setWasImported(true);
		//project.setStartConstraint(0);
		project.setStartDate(0);
		project.setBoundsAfterReadProject();
		//project.setForward(false);
//		project.baselineWork(0,System.currentTimeMillis());
//		project.setTemporaryLocal(true);

		for(Ticket t:ticketList){
			if(log.isInfoEnabled()){
				log.info("#"+t.id+":"+t.$("summary")+":"+t.$(startField)+"-"+t.$(endField));
			}
			//NormalTask task = new NormalTask(project);
			NormalTask task = project.newNormalTaskInstance();
			task.setOwningProject(project);
			task.setProjectId(project.getId());
			task.setId(t.id);
			task.setUniqueId(t.id);
			map.put(t.id ,task);
			task.setName(t.$("summary"));
			task.setWbs(t.$("summary"));
			task.isLocal();
//			task.set
			long start = convertDate(t,startField);
			long end = convertDate(t,endField);
//			task.setDuration(end-start);
			String tracComplete = t.$(completeField);
			float complete = 0;
			if(tracComplete!=null&&tracComplete.trim().length()>0){
				try {
					complete = Float.parseFloat(tracComplete)/100;
					task.setPercentComplete(complete);
				} catch(NumberFormatException e){
					task.setPercentComplete(0);
					log.error("#"+t.id + ": " + completeField + "='"+tracComplete+"' is invalid.");
				}

			} else {
				task.setPercentComplete(0);
			}
			String parent = t.$(parentField);
			if (parent != null && parent.trim().length() > 0) {
				int pid = Integer.parseInt(parent);
				ArrayList<Integer> children = childMap.get(pid);
				if(children==null){
					children = new ArrayList<Integer>();
					childMap.put(pid, children);
				}
				children.add(t.id);
			} else {
				rootNode.add(t.id);
			}

			if(start!=0){
				task.setStart(start);
				task.getCurrentSchedule().setStart(start);
			}

			if(end!=0){
				task.setEnd(end);
				task.getCurrentSchedule().setEnd((long)(start+(end-start)*complete));
//				task.getCurrentSchedule().setEnd((long)(start+(end-start)*complete));
				task.setActualDuration((long)((end-start)*complete/2));
			}

			// TODO: 以下の設定
			// 担当者
			// コンポーネント
			// マイルストーン
			// デバッグ用コード
			// 使うときはimportHierarchy() をコメントアウトする。
			//project.addToDefaultOutline(null ,NodeFactory.getInstance().createNode(task));
		}
	}

	private void importHierarchy() {
		for(Object rootId:rootNode){
			int id = (Integer)rootId;
			Task rootTask = map.get(id);
			Node pnode = NodeFactory.getInstance().createNode(rootTask);
			project.addToDefaultOutline(null, pnode);
			importHierarchy(id, pnode);
		}
	}
	private void importHierarchy(int parent, Node pnode){
		ArrayList<Integer> children = (ArrayList<Integer>) childMap.get(parent);
		if(children!=null){
			for(int i:children){
				Node cnode = NodeFactory.getInstance().createNode(map.get(i));
				project.addToDefaultOutline(pnode,cnode);
				importHierarchy(i,cnode);
			}
		}

	}

}
