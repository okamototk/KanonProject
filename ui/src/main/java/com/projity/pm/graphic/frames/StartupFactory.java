/*
The contents of this file are subject to the Common Public Attribution License
Version 1.0 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at
http://www.projity.com/license . The License is based on the Mozilla Public
License Version 1.1 but Sections 14 and 15 have been added to cover use of
software over a computer network and provide for limited attribution for the
Original Developer. In addition, Exhibit A has been modified to be consistent
with Exhibit B.

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for the
specific language governing rights and limitations under the License. The
Original Code is OpenProj. The Original Developer is the Initial Developer and
is Projity, Inc. All portions of the code written by Projity are Copyright (c)
2006, 2007. All Rights Reserved. Contributors Projity, Inc.

Alternatively, the contents of this file may be used under the terms of the
Projity End-User License Agreeement (the Projity License), in which case the
provisions of the Projity License are applicable instead of those above. If you
wish to allow use of your version of this file only under the terms of the
Projity License and not to allow others to use your version of this file under
the CPAL, indicate your decision by deleting the provisions above and replace
them with the notice and other provisions required by the Projity  License. If
you do not delete the provisions above, a recipient may use your version of this
file under either the CPAL or the Projity License.

[NOTE: The text of this license may differ slightly from the text of the notices
in Exhibits A and B of the license at http://www.projity.com/license. You should
use the latest text at http://www.projity.com/license for your modifications.
You may not remove this license text from the source files.]

Attribution Information: Attribution Copyright Notice: Copyright � 2006, 2007
Projity, Inc. Attribution Phrase (not exceeding 10 words): Powered by OpenProj,
an open source solution from Projity. Attribution URL: http://www.projity.com
Graphic Image as provided in the Covered Code as file:  openproj_logo.png with
alternatives listed on http://www.projity.com/logo

Display of Attribution Information is required in Larger Works which are defined
in the CPAL as a work which combines Covered Code or portions thereof with code
not governed by the terms of the CPAL. However, in addition to the other notice
obligations, all copies of the Covered Code in Executable and Source Code form
distributed must, as a form of attribution of the original author, include on
each user interface screen the "OpenProj" logo visible to all users.  The
OpenProj logo should be located horizontally aligned with the menu bar and left
justified on the top left of the screen adjacent to the File menu.  The logo
must be at least 100 x 25 pixels.  When users click on the "OpenProj" logo it
must direct them back to http://www.projity.com.
*/
package com.projity.pm.graphic.frames;

import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.HeadlessException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

import org.apache.commons.collections.Closure;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.jdesktop.swing.actions.ServerAction;
import org.openproj.util.UpdateChecker;
import org.ultimania.kanon.exchange.TracImporter;

import com.projity.company.DefaultUser;
import com.projity.configuration.Configuration;
import com.projity.configuration.ConfigurationReader;
import com.projity.configuration.Dictionary;
import com.projity.configuration.Settings;
import com.projity.contrib.ClassLoaderUtils;
import com.projity.dialog.LicenseDialog;
import com.projity.dialog.LoginDialog;
import com.projity.dialog.LoginForm;
import com.projity.dialog.TipOfTheDay;
import com.projity.dialog.TryPODDialog;
import com.projity.dialog.UserInfoDialog;
import com.projity.exchange.ImportedCalendarService;
import com.projity.field.FieldConverter;
import com.projity.functor.StringList;
import com.projity.graphic.configuration.ActionLists;
import com.projity.graphic.configuration.CellStyles;
import com.projity.grouping.core.transform.filtering.NotAssignmentFilter;
import com.projity.grouping.core.transform.filtering.NotVoidFilter;
import com.projity.help.HelpUtil;
import com.projity.job.Job;
import com.projity.options.AdvancedOption;
import com.projity.options.CalculationOption;
import com.projity.options.CalendarOption;
import com.projity.options.EditOption;
import com.projity.options.GanttOption;
import com.projity.options.GeneralOption;
import com.projity.options.ScheduleOption;
import com.projity.options.TimesheetOption;
import com.projity.pm.assignment.AssignmentService;
import com.projity.pm.assignment.functor.ZeroFunctor;
import com.projity.pm.calendar.CalendarCatalog;
import com.projity.pm.calendar.CalendarDefinition;
import com.projity.pm.calendar.CalendarService;
import com.projity.pm.calendar.WorkingCalendar;
import com.projity.pm.graphic.frames.workspace.DefaultFrameManager;
import com.projity.pm.graphic.laf.LafManagerImpl;
import com.projity.pm.graphic.spreadsheet.renderer.NameCellComponent;
import com.projity.pm.key.uniqueid.UniqueIdPool;
import com.projity.pm.resource.ResourcePoolFactory;
import com.projity.pm.scheduling.Schedule;
import com.projity.pm.scheduling.ScheduleService;
import com.projity.pm.task.Project;
import com.projity.pm.task.ProjectFactory;
import com.projity.print.PageSizes;
import com.projity.script.ContextStore;
import com.projity.server.access.PartnerInfo;
import com.projity.server.data.CustomFieldsMapper;
import com.projity.server.data.ProjectData;
import com.projity.session.Session;
import com.projity.session.SessionFactory;
import com.projity.strings.Messages;
import com.projity.util.Alert;
import com.projity.util.DateTime;
import com.projity.util.DebugUtils;
import com.projity.util.Environment;
import com.projity.util.Factory;
import com.projity.util.VersionUtils;

public abstract class StartupFactory {
	public static final String defaultServerUrl = Settings.SITE_HOME;
	private static final int NUM_INVALID_LOGINS = 3;
	private static Log log = LogFactory.getLog(StartupFactory.class);

	protected String serverUrl=null;
	protected String[] projectUrls=null;
	protected String login=null;
	protected String password=null;
	protected Map credentials=new HashMap();
	protected long projectId;
	protected HashMap opts=null;
	private boolean instanceFromNewSessionDone;
	private static StartupFactory instance = null;
	Map<String,String> args = null;
	protected StartupFactory() {
		instance = this;
//		System.out.println("---------- StartupFactory");
	}
	public static StartupFactory getInstance() {
		return instance;
	}

	/**
	 * Used to test restoring of workspace to simulate applet restart
	 * @param old
	 * @return
	 */
	public GraphicManager restart(GraphicManager old) {
		RootPaneContainer con = (RootPaneContainer) old.getContainer();
		old.encodeWorkspace();
		old.cleanUp();
		con.getContentPane().removeAll();
		GraphicManager g = instanceFromExistingSession((Container) con,null);
//		g.decodeWorkspace();

//		System.out.println("restarted");
		return g;
	}

	public GraphicManager instanceFromExistingSession(Container container,Map args) {
		this.args = args;

		System.gc(); // hope to avoid out of memory problems

		DebugUtils.isMemoryOk(true);


		long t=System.currentTimeMillis();
		System.out.println("---------- StartupFactory instanceFromExistingSession#1");
		final GraphicManager graphicManager = new GraphicManager(container);
		graphicManager.setStartupFactory(this);
		SessionFactory.getInstance().setJobQueue(graphicManager.getJobQueue());
		//if (Environment.isNewLook())
			graphicManager.initLookAndFeel();
//		System.out.println("---------- StartupFactory instanceFromExistingSession#1 done in "+(System.currentTimeMillis()-t)+" ms");
		final Map appletArgs = args;
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				long t=System.currentTimeMillis();
//				System.out.println("---------- StartupFactory instanceFromExistingSession#2");
				graphicManager.initView();
//				System.out.println("---------- StartupFactory instanceFromExistingSession#2 done in "+(System.currentTimeMillis()-t)+" ms");

				if (appletArgs != null) {
					String id = (String)appletArgs.get("projectId");
					if (id != null)
						projectId = Long.parseLong(id);
					if (id != null)
						doStartupAction(graphicManager,projectId,null,false,false,appletArgs);

				}
			}});
//		graphicManager.invalidate();
		return graphicManager;
	}


	public GraphicManager instanceFromNewSession(Container container,  boolean doWelcome) {

//		DebugUtils.dumpStack("instanceFromNewSession being called ");
		VersionUtils.versionCheck(true);
		if (!VersionUtils.isJnlpUpToDate()) System.out.println("Jnlp isn't up to date, current version is: "+VersionUtils.getJnlpVersion());
		long t=System.currentTimeMillis();
		log.info("New session");
		Environment.setClientSide(true);

		System.setSecurityManager(null);
		Thread loadConfigThread=new Thread("loadConfig"){
			public void run() {
				long t=System.currentTimeMillis();
				doLoadConfig();
			}
		};
		loadConfigThread.start();

		GraphicManager graphicManager = GraphicManager.getInstance(); // normally null, unless reinit
		boolean recycling = graphicManager != null;
		//String projectUrl[]=null;
		try {
			if (Environment.isNoPodServer() || graphicManager == null) {
				graphicManager=new GraphicManager(/*projectUrl,*/serverUrl,container);
				graphicManager.setStartupFactory(this);
			} else {
				reinitialize();
			}
		} catch (HeadlessException e) {
			e.printStackTrace();
		}
		graphicManager.setConnected(false);

		if (!doLogin(graphicManager))
			return null;
		//if (Environment.isNewLook())
		if (Environment.isNoPodServer() || !recycling)
			graphicManager.initLookAndFeel();

		SessionFactory.getInstance().setJobQueue(graphicManager.getJobQueue());

		PartnerInfo partnerInfo=null;
		if (!Environment.getStandAlone() && !Environment.isNoPodServer()) {
			Session session = SessionFactory.getInstance().getSession(false);
			try {
				partnerInfo=(PartnerInfo)SessionFactory.call(session,"retrievePartnerInfo",null,null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

//		System.out.println("---------- StartupFactory instanceFromNewSession#1 main done in "+(System.currentTimeMillis()-t)+" ms");
		try {
			loadConfigThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		t=System.currentTimeMillis();
//		System.out.println("---------- StartupFactory instanceFromNewSession#2");

		//graphicManager.showWaitCursor(true); //TODO use a progress bar - maybe a Job
		if (partnerInfo!=null){

			if (partnerInfo.getConfigurationXML() != null) {
				ConfigurationReader.readString(partnerInfo.getConfigurationXML(),Configuration.getInstance());
				Configuration.getInstance().setDonePopulating();
			}
			if (partnerInfo.getViewXML() != null) {
				ConfigurationReader.readString(partnerInfo.getViewXML(),Dictionary.getInstance());
			}
		}

		final GraphicManager gm = graphicManager;
		graphicManager.beginInitialization();
		try{


			graphicManager.initView();

			doStartupAction(graphicManager,projectId,(projectUrls==null&&gm.getLastFileName()!=null)?new String[]{gm.getLastFileName()}:projectUrls,doWelcome,false,args);

			doPostInitView(graphicManager.getContainer());
			//graphicManager.getContainer().applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}finally{
			graphicManager.finishInitialization();
		}
		instanceFromNewSessionDone = true;
        return graphicManager;
	}

	public void doLoadConfig() {
		com.projity.init.Init.initialize();
	}
	public void doPostInitView(Container container) {
	}

	public boolean doLogin(GraphicManager graphicManager) {

		if (Environment.isNoPodServer()){
			Environment.setNewLook(true);
			log.info("Login POD Server.");
		}
		if (Environment.getStandAlone() || Environment.isNoPodServer()){
			if(!Environment.isNoPodServer()){
				Environment.setUser(new DefaultUser());
				log.info("Locale user.");
			}
			return true;
		}
		credentials.put("serverUrl",serverUrl);
		getCredentials();
		Environment.setNewLook(true);

		int badLoginCount = 0;
		while (true) { // until a good login or exit because of too many bad
//			graphicManager.getFrame().setVisible(true);
			if (login==null||password==null || badLoginCount > 0){
				URL loginUrl=null;
				if (login==null||password==null){
					try {
						loginUrl=new URL(serverUrl+"/login");
						System.out.println("trying login at " +serverUrl+"/login");
					} catch (MalformedURLException e) {}
				}
				LoginForm form = LoginDialog.doLogin(graphicManager.getFrame(),loginUrl); // it's actually a singleton
				if (form.isCancelled())
					System.exit(-1);
				if (form.isUseMenus())
					Environment.setNewLook(false);

				login=form.getLogin();
				password=form.getPassword();
			}

			if ("_SA".equals(login)||Environment.getStandAlone()) {// for testing purposes!
				Environment.setStandAlone(true);
				Environment.setUser(new DefaultUser());
				break;
			} else {
				credentials.put("login",login);
				credentials.put("password",password);


				SessionFactory.getInstance().setCredentials(credentials);
				try {
					Session session = SessionFactory.getInstance().getSession(false);
				System.out.println("logging in");
					final GraphicManager gm = graphicManager;
					SessionFactory.callNoEx(session,"login",new Class[]{Closure.class},new Object[]{new Closure(){
						public void execute(Object arg0) {
							Map<String,String> env=(Map<String,String>)arg0;
							if (env!=null){
								String serverVersion=env.get("serverVersion");
								checkServerVersion(serverVersion);
							}
							if (gm!=null) gm.setConnected(true);

						}
					}});
					if (!((Boolean)SessionFactory.callNoEx(session,"isLicensedToRunClient",null,null)).booleanValue()) {
						Alert.error(Messages.getString("Error.roleCantRunClient"));
						abort();
						return false;
					}

//					System.out.println("Application started with args: credentials=" + credentials.get("login") + " name " + session.getUser().getName() + " Roles " + session.getUser().getServerRoles());
					break;
					//			TODO test if login is valid.  If not, reshow login dialog
				} catch (Exception e) {
					if (Session.EXPIRED.equals(e.getMessage())) {
						Alert.error(Messages.getString("Error.accountExpired"));
						abort();
						return false;

					}
					System.out.println("failure " + e);
					badLoginCount++;
					SessionFactory.getInstance().clearSessions();

					if (badLoginCount == NUM_INVALID_LOGINS) {
						Alert.error(Messages.getString("Login.tooManyBad"));
						abort();
						return false;
					} else {
						Alert.error(Messages.getString("Login.error"));
					}
				}
			}
		}
		return true;
	}

	protected void checkServerVersion(String serverVersion){
		String thisVersion=null;
		if (serverVersion!=null){
			thisVersion=VersionUtils.getVersion();
			if (thisVersion!=null) thisVersion=VersionUtils.toAppletVersion(thisVersion);
			if(thisVersion==null||serverVersion.equals(thisVersion)) return; //ok
		}
		String jnlpUrl="https://www.projity.com/web/jnlp/project-on-demand.jnlp";
//		String jnlpUrl="http://192.168.0.2/web/jnlp/project-on-demand.jnlp";
		if (Alert.okCancel(Messages.getString("Text.newPODVersion"))){
			try {
				Object basicService = ClassLoaderUtils.forName("javax.jnlp.ServiceManager").getMethod("lookup", new Class[]{String.class})
				.invoke(null, new Object[] {"javax.jnlp.BasicService"});
				ClassLoaderUtils.forName("javax.jnlp.BasicService").getMethod("showDocument", new Class[]{URL.class})
				.invoke(basicService, new Object[] {new URL(jnlpUrl)});
			} catch(Exception e) {
				//e.printStackTrace();
				// Not running in JavaWebStart or service is not supported.
				return;
				//Runtime.getRuntime().exec("javaws ");
			}
//			try {
//			BasicService basicService=(BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
//			basicService.showDocument(/*new URL(basicService.getCodeBase(),*/new URL(jnlpUrl));
//			}catch (UnavailableServiceException e) {
//			Runtime.getRuntime().exec("javaws ");
//			}
			System.exit(0);
		}
	}




/*
 * Returns null if shouldn't open, returns false if open read only, true if open writable
 *
 */	public  Boolean verifyOpenWritable(Long projectId) {
		if (projectId == null || projectId == 0)
			return null;
		if (ProjectFactory.getInstance().isResourcePoolOpenAndWritable()) {
			Alert.warn(Messages.getString("Warn.resourcePoolOpen"));
			return null;
		}

		String locker = getLockerName(projectId);
		boolean openAs = false;
		if (locker != null) {
			openAs = (JOptionPane.YES_OPTION == Alert.confirmYesNo(Messages.getStringWithParam("Warn.lockMessage",locker)));
			if (openAs == false)
				return null;
		}
		return !openAs;
	}
	public static String getLockerName(long projectId) {
		ProjectData projectData = (ProjectData)ProjectFactory.getProjectData(projectId);
		System.out.println("Locked is " + projectData.isLocked() + "  Lock info: User is " + Environment.getUser().getUniqueId() + "  locker id is " + projectData.getLockedById() + " locker is "+projectData.getLockedByName() );

		if (projectData != null && projectData.isLocked()) {

			if (Environment.getUser().getUniqueId() != projectData.getLockedById())
				return projectData.getLockedByName();
		}
		return null;
	}



	protected abstract void abort();
	protected void getCredentials() {
	}
	public void doStartupAction(final GraphicManager gm, final long projectId, final String[] projectUrls, final boolean welcome, boolean readOnly, final Map<String,String> args) {
		this.args = args;
		log.debug("Start up action: "+projectId);
		if (Environment.isClientSide() && !Environment.isTesting()) {
			log.debug("Start up action: A");
			if (projectId > 0) {
				log.debug("Start up action: B");

				Boolean writable = null;
				if (readOnly)
					writable = Boolean.FALSE;
				else
					writable = verifyOpenWritable(projectId);
				if (writable == null)
					return;
				AssignmentService.getInstance().setSubstituting(args.get("oldResourceId") != null);
				log.info("Load document: " + projectUrls);
				gm.loadDocument(projectId, true,!writable,new Closure(){
					public void execute(Object arg0) {
						log.debug("Start up action: D");
						Project project=(Project)arg0;
						DocumentFrame frame=gm.getCurrentFrame();
						if (frame!=null&&frame.getProject().getUniqueId() != projectId) {
							log.debug("Start up action: E");

							gm.switchToProject(projectId);
						}
						log.debug("Start up action: F");
						if (args.get("oldResourceId") != null) { // see if need to substitute
							//JGao 6/3/2009 Need to set initial ids to make sure before doing resource substitution
							if (project.getInitialTaskIds() == null)
								project.setInitialIds();
							final long oldResourceId = Long.parseLong(args.get("oldResourceId"));
							final long newResourceId = Long.parseLong(args.get("newResourceId"));
							final Date startingFrom = DateTime.parseZulu(args.get("startingFrom"));
							final int[] taskIds = StringList.parseAsIntArray(args.get("commaSeparatedTaskIds"));
							AssignmentService.getInstance().replaceResource(projectId, oldResourceId, newResourceId, startingFrom, taskIds);
							if (GraphicManager.getInstance() != null)
								GraphicManager.getInstance().setEnabledDocumentMenuActions(true);
							args.put("oldResourceId", null); //avoid doing again
							AssignmentService.getInstance().setSubstituting(false);
							log.debug("Start up action: G");
						}
					}
				});
			}
			else if (projectUrls!=null && projectUrls.length > 0) {
					log.info("Load document: " + projectUrls[0]);
					gm.loadLocalDocument(projectUrls[0],!Environment.getStandAlone(),true);
				}
			} if(serverUrl!=null) {
				if(opts.get("auth")!=null){
					String username = (String)((LinkedList)opts.get("auth")).get(1);
					String password = (String)((LinkedList)opts.get("auth")).get(2);
					TracImporter importer = new TracImporter(serverUrl, username,password);
					String msg = importer.checkConnection();
					if(msg!=null){

					}
					try {
						importer.importByQuery("status!=close");
					} catch (XmlRpcException e) {
						e.printStackTrace();
					}
					Project project = importer.getProject();
					String HTTP_PREFIX = "http://";

					project.setFileName(HTTP_PREFIX+username+":"+password+"@"+serverUrl.substring(HTTP_PREFIX.length()));


					project.initialize(false,false);
					project.setBoundsAfterReadProject();

					Environment.setImporting(false);
					project.setWasImported(true);

					final Session session=SessionFactory.getInstance().getSession(true);
					session.refreshMetadata(project);
					session.readCurrencyData(project);
					return ;

				} else	{
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if (Environment.isOpenProj()&&!Environment.isPlugin()) {
//							LicenseDialog.showDialog(gm.getFrame(),false);
							UserInfoDialog.showDialog(gm.getFrame(),false);
//							TryPODDialog.maybeShow(gm.getFrame(),false);
//							UpdateChecker.checkForUpdateInBackground();
						}
						if (welcome&&!Environment.isPlugin()) {
							if (Environment.isOpenProj()) {
								//LicenseDialog.showDialog(gm.getFrame(),false);
								//TipOfTheDay.showDialog(gm.getFrame(), false);
							} else {
								if (Environment.isNeedToRestart())
									return;
								if (!LafManagerImpl.isLafOk()) // for startup glitch - we don't want people to work until restarting.
									return;


//
//								String lastVersion = Preferences.userNodeForPackage(StartupFactory.class).get("lastVersion","0");
//								String thisVersion = VersionUtils.getVersion();
//								System.out.println("last version " + lastVersion + " this version " + thisVersion);
//								if (!lastVersion.equals(thisVersion)) {
//									Preferences.userNodeForPackage(StartupFactory.class).put("lastVersion",thisVersion);
//									String javaVersion = System.getProperty("java.version");
//									if (javaVersion.equals("1.6.0_04") || javaVersion.equals("1.6.0_05"))
//										Alert.warn("Project-ON-Demand has been updated.  Please close your browser completely and restart it to complete the upgrade process.");
//									return;
//								}

							}
							gm.doWelcomeDialog();
						}
						if (Environment.isPlugin()) gm.doNewProjectNoDialog(opts);
					}
				});

			}
		}

	}

	public void loadDocument(GraphicManager gm, long projectId, boolean openAs) {
		gm.loadDocument(projectId, true,openAs);

	}
	public void setSecurityToken(String token) {
	}

	protected void doExtraReinit() {

	}
	private boolean reinitializing = false;

	public static void clear(boolean graphic) {
		ProjectFactory.getInstance().setPromptDisabled(true); // prevent remove projects job from prompting to save
		Job job=ProjectFactory.getInstance().getPortfolio().getRemoveAllProjectsJob(null,false,null);
		SessionFactory.getInstance().getLocalSession().schedule(job);
		try {
			job.waitResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (graphic) {
			GraphicManager gm = GraphicManager.getInstance();
			if (gm != null)
				((DefaultFrameManager)gm.getFrameManager()).cleanUp(false);

		}
		ProjectFactory.getInstance().getPortfolio().getObjectEventManager().removeAll();
		ProjectFactory.getInstance().setPromptDisabled(false); // enable prompting again


		//Singletons, most of them can cause problems when it re-used.
		//Other singletons shouldn't be problem but have a cleanUp method just in case
		SessionFactory.cleanUp();
		ProjectFactory.cleanUp();
		ResourcePoolFactory.cleanUp();
		UniqueIdPool.cleanUp();
		MainFrame.cleanUp();
		GraphicManager.cleanUpStatic();
		StartupFactory.cleanUpStatic();

		CalendarCatalog.cleanUp();
		CalendarDefinition.cleanUp();
		CalendarService.cleanUp();
		WorkingCalendar.cleanUp();

		AdvancedOption.cleanUp();
		CalculationOption.cleanUp();
		CalendarOption.cleanUp();
		EditOption.cleanUp();
		GanttOption.cleanUp();
		GeneralOption.cleanUp();
		ScheduleOption.cleanUp();
		TimesheetOption.cleanUp();

		FieldConverter.cleanUp();

		ActionLists.cleanUp();
		CellStyles.cleanUp();
		Configuration.cleanUp();
		Dictionary.cleanUp();

		Factory.cleanUp();
		ImportedCalendarService.cleanUp(); //remove dep
		NotAssignmentFilter.cleanUp();
		NotVoidFilter.cleanUp();
		HelpUtil.cleanUp();
		ZeroFunctor.cleanUp();
		NameCellComponent.cleanUp();
		ScheduleService.cleanUp();
		PageSizes.cleanUp();
		ContextStore.cleanUp();
		CustomFieldsMapper.cleanUp();// remove dep


	}

	public static void cleanUpStatic() {
		instance = null;
	}

	public void reinitialize() {
		if (reinitializing)
			return;
		reinitializing = true;
		// remove all projects quietly
		System.out.println("----------------reinitialize---------------");
		clear(false);
		ProjectFactory.getInstance().setPromptDisabled(true); // prevent remove projects job from prompting to save
		GraphicManager gm = GraphicManager.getInstance();

		Container container = gm == null ? null : gm.getContainer();
		instanceFromNewSession(container, false); // reinit
		ProjectFactory.getInstance().setPromptDisabled(false); // enable prompting again
		((DefaultFrameManager)GraphicManager.getInstance().getFrameManager()).cleanUp(true);
		doExtraReinit();
		reinitializing = false;

	}
}