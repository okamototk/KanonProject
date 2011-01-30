package org.ultimania.kanon.exchange.trac;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

/**
 *
 * XMLRPCでTracへ接続するクライアント<br />
 * Basic認証とDigest認証をサポート
 */
public class TracXmlRpcClient {
	XmlRpcClient client;
	XmlRpcClientConfigImpl config;

	/**
	 * url : TracURL
	 *
	 * @param url TracのURL(ex.  http://<ホスト名>/SampleProject/)
	 * @param user ユーザ名
	 * @param password パスワード
	 * @throws MalformedURLException
	 */
	public TracXmlRpcClient(String url, String user, String password)
			throws MalformedURLException {
		client = new XmlRpcClient();
		client.setTransportFactory(new XmlRpcCommonsTransportFactory(client));
		config = new XmlRpcClientConfigImpl();

		if(user!=null){
			config.setBasicUserName(user);
			if (password!=null){
				config.setBasicPassword(password);
			}
		}

		String sep = "";
		if (url.charAt(url.length() - 1) != '/') {
			sep = "/";
		}
		System.out.println(sep);
		config.setServerURL(new URL(url + sep + "login/xmlrpc"));

	}

	public String checkConnection(){
		try {
			query("id=0");
			return null;
		} catch (XmlRpcException e){
			return e.getMessage();
		}
	}
	// //////////////////////////////////////////////////////////////////
	// チケット操作メソッド
	// ////////////////////////////////////////////////////////////////
	/**
	 * チケットidを指定してチケットを取得
	 */
	public Ticket get(int id) throws XmlRpcException {
		Object[] res = (Object[]) client.execute(config, "ticket.get",
				new Object[] { id });
		Ticket ticket = new Ticket((Integer) res[0], (Date) res[1],
				(Date) res[2], (HashMap<String, String>) res[3]);
		return ticket;
	}

	/**
	 * 更新したフィールドとチケットIDを指定してチケットをアップデート
	 *
	 * @deprecated
	 */
	public void update(int id, HashMap<String, String> modifiedFields,
			String message) throws XmlRpcException {
		Object[] res = (Object[]) client.execute(config, "ticket.update",
				new Object[] { id, message, modifiedFields, false });
	}

	/**
	 * チケットをアップデート (更新したフィールドのみをアップデート)
	 */
	public void update(Ticket ticket, String message) throws XmlRpcException {
		Object[] res = (Object[]) client.execute(config, "ticket.update",
				new Object[] { ticket.id, message, ticket.getModifiedFields(),
						false });
		ticket.changetime = (Date) res[2];
		ticket.commit();
	}

	/**
	 * クエリを指定してマッチするチケットIDのリストを取得 クエリは、TIcketQueryと同じ
	 */
	public int[] query(String query) throws XmlRpcException {
		Object[] res = (Object[]) client.execute(config, "ticket.query",
				new Object[] { query });

		int[] ids = new int[res.length];
		for (int i = 0; i < res.length; i++) {
			ids[i] = (Integer) res[i];
		}
		return ids;
	}

	/**
	 * チケット作成
	 */
	public void create(Ticket ticket) throws XmlRpcException {
		Integer res = (Integer) client.execute(config, "ticket.create",
				new Object[] { ticket.$("summary"), ticket.$("description"),
						ticket.getFields(), false });
	}

	// ////////////////////////////////////////////////////////////////
	// マイルストーント操作メソッド
	// ////////////////////////////////////////////////////////////////

	/**
	 * 全てのマイルストーンの名前のリストを取得
	 */
	public String[] getAllMilestone() throws XmlRpcException {
		Object[] res = (Object[]) client.execute(config,
				"ticket.milestone.getAll", new Object[] {});
		String[] milestones = new String[res.length];
		for (int i = 0; i < res.length; i++) {
			milestones[i] = (String) res[i];
		}
		return milestones;
	}

	/**
	 * 指定した名前のマイルストーンを取得
	 */
	public HashMap<String, Object> getMilestone(String name)
			throws XmlRpcException {
		HashMap<String, Object> res = (HashMap<String, Object>) client.execute(
				config, "ticket.milestone.get", new Object[] { name });
		if (res.get("due") instanceof Integer) {
			res.remove("due");
		}
		if (res.get("completed") instanceof Integer) {
			res.remove("completed");
		}
		System.out.println(res);
		return res;

	}

	/**
	 * マイルストーン作成
	 *
	 * @param name
	 *            マイルストーン名
	 * @param due
	 *            期限(省略可能)
	 * @param description
	 *            説明
	 * @return マイルストーン情報が入ったハッシュ
	 * @throws XmlRpcException
	 */
	public HashMap<String, Object> createMilestone(String name, Date due,
			String description) throws XmlRpcException {
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("name", name);
		if (due != null)
			m.put("due", due);
		if (description != null)
			m.put("description", description);

		Integer res = (Integer) client.execute(config,
				"ticket.milestone.create", new Object[] { name, m });
		return m;
	}

	/**
	 * マイルストーンアップデート
	 * @param name マイルストーン名
	 * @param milestone マイルストーンのプロパティ(due,descriptionを設定)
	 * @throws XmlRpcException
	 */
	public void updateMilestone(String name, HashMap<String, Object> milestone)
			throws XmlRpcException {
		Integer res = (Integer) client.execute(config,
				"ticket.milestone.update", new Object[] { name, milestone });

	}

	/**
	 * まい
	 *
	 * @param args
	 * @throws MalformedURLException
	 * @throws XmlRpcException
	 */
	public static void main(String[] args) throws MalformedURLException,
			XmlRpcException {

		// Map<String, String> attributes = new HashMap<String,String>();
		// attributes.put("status", "assigned");
		TracXmlRpcClient rpc = new TracXmlRpcClient(
				"http://localhost/trac/SampleProject/", "admin", "admin");
		System.out.println(rpc.query("id=0"));
		Ticket ticket = rpc.get(0);
		ticket.$("status", "accepted");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("status", "accepted");
		// / rpc.update(1,map,"test");
		rpc.update(ticket, "test");
		System.out.println(ticket);
		rpc.getMilestone("1.0αリリース");

		Ticket n = new Ticket("概要", "詳細");
		rpc.create(n);

		// マイルストーン
		HashMap<String, Object> p = rpc.getMilestone("マイルストーン");
		/* rpc.createMilestone("マイルストーン", new Date(), "詳細の記述"); */

		p.put("name", "テスト");
		rpc.updateMilestone("マイルストーン", p);
		System.out.println(p);
	}

}
