package org.ultimania.kanon.exchange;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

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
//	private static ResourceBundle bundle= ResourceBundle.getBundle("fieldconfig");
	private String dateFormat = "yyyy/MM/dd";
	private SimpleDateFormat format = new SimpleDateFormat(dateFormat);
	private HashMap<Integer,NormalTask> map = new HashMap<Integer,NormalTask>();
	private ArrayList<Ticket> ticketList;
	private Project project;
	private String startField = "due_assign";
	private String endField = "due_close";
	private String parentField = "parents";
	private String completeField = "complete";

	public TracImporter(String tracurl, String username, String password){
		log.debug("URL: "+tracurl);
		log.debug("Username: "+username);
		log.debug("Password: "+password);
		log.debug("Start field in trac: "+startField);
		log.debug("End field in trac: "+endField);
		log.debug("Parent field in trac: "+parentField);
		try {
			client = new TracXmlRpcClient(tracurl, username, password);
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public Project getProject(){
		return project;
	}

	public String checkConnection(){
		return client.checkConnection();
	}

	public void importByQuery(String query) throws XmlRpcException  {
		int[] list = client.query(query);
		ticketList = new ArrayList<Ticket>();
		for(int id:list){
			ticketList.add(client.get(id));
		}
		importTicket();
//		importHierarchy();
	}

	private long convertDate(Ticket t, String fieldName){
		String tracField = t.$(fieldName);
		if(tracField!=null&&tracField.trim().length()!=0){
			try {
				Date date = format.parse(tracField);
				System.out.println(date);
				return date.getTime();
			} catch (ParseException e) {
				log.warn("#"+t.id+": "+fieldName+"="+tracField+" is invalid format "+dateFormat,e);
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


		//project.setStart(0);
		for(Ticket t:ticketList){
			if(log.isInfoEnabled()){
				log.info("#"+t.id+":"+t.$("summary")+":"+t.$(startField)+"-"+t.$(endField));
			}
			//NormalTask task = new NormalTask(project);
			NormalTask task = project.newNormalTaskInstance();
			task.setProject(project);
			task.setProjectId(project.getId());
			task.setId(t.id);
			task.setUniqueId(t.id);
			map.put(t.id ,task);
			task.setName(t.$("summary"));
			task.setWbs(t.$("summary"));

			long start = convertDate(t,startField);
			if(start!=0){
				task.setStart(start);
			}
			long end = convertDate(t,endField);
			if(end!=0){
				task.setEnd(end);
			}

			String tracComplete = t.$(completeField);
			if(tracComplete!=null&&tracComplete.trim().length()>0){
				try {
					float complete = Float.parseFloat(tracComplete);
					task.setPercentComplete(complete/100);
				} catch(NumberFormatException e){
					log.error("#"+t.id + ": " + completeField + "='"+tracComplete+"' is invalid.");
				}

			}
			// TODO: 以下の設定
			// 担当者
			// コンポーネント
			// マイルストーン

			project.addToDefaultOutline(null ,NodeFactory.getInstance().createNode(task));
		}
	}
	private void importHierarchy(){
		ArrayList<Task> hasParent = new ArrayList<Task>();
		for(Ticket t:ticketList){
			String parent = t.$(parentField);
			if(parent!=null&&parent.trim().length()>0){
				Task task = map.get(t.id);
				try {
					Task parentTask = map.get(Integer.parseInt(parent));
					if (parentTask!=null) {
						Node taskNode = NodeFactory.getInstance().createNode(task);
						Node parentNode = NodeFactory.getInstance().createNode(parentTask);
						project.addToDefaultOutline(parentNode ,taskNode);
						hasParent.add(task);
					} else {
						log.error("#"+t.id + ": " + parentField + "='"+parent+"' is not exists");
					}
				} catch (NumberFormatException e){
					log.error("#"+t.id + ": " + parentField + "' is not Number format.",e);
				}
			}
		}
		for(Task t:hasParent){
			map.remove(t);
		}
		for(int id:map.keySet()){
			Node node = NodeFactory.getInstance().createNode(map.get(id));
			project.addToDefaultOutline(null,node);
		}

	}
}
