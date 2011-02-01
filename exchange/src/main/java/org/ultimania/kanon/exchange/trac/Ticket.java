package org.ultimania.kanon.exchange.trac;
import java.util.Date;
import java.util.HashMap;

/**
 * チケットのモデル
 * <code>
 * Ticket ticket = ..
 *
 * int id = ticket.id;
 * Date time = ticket.time; // チケット作成日時
 * Date changetime = ticket.changetime; // チケット最終更新日時
 *
 * // フィールドのアクセス
 * String component = ticket.$("component");
 * String owner = ticket.$("ownerd");
 * ticket.$("component", "新しいコンポーネント");
 * ticket.$("milestone", "新しいマイルストーン");
 * // ※　変更されたフィールドは内部で記憶され、XMLRPCでアップデートされるときに利用される
 *
 *
 * // フィールドの変更記録を反映(XMLRPCでアップデートされたときに以前の変更は反映されなくなる。
 * ticket.commit();
 * </code>
 */
public class Ticket {
	public int id;
	public Date time;
	public Date changetime;
	private HashMap<String,String> fields;
	private HashMap<String,String> modified = new HashMap<String, String>();

	/**
	 * コンストラクタ
	 * @param summary 概要
	 * @param description 詳細
	 */
	Ticket(String summary, String description){
		fields = new HashMap<String,String>();
		fields.put("summary",summary);
		fields.put("description",description);
	}

	protected Ticket(int id, Date time, Date changetime, HashMap<String,String> fields){
		this.id = id;
		this.time = time;
		this.changetime = changetime;
		this.fields = fields;
	}


	/**
	 * フィールドの値の取得.カスタムフィールドも取得可能
	 * デフォルトのフィールドは、
	 * <ul>
	 *  <li>summary</li>
	 *  <li>description</li>
	 *  <li>component</li>
 	 *  <li>milestone</li>
 	 *  <li>owner</li>
 	 *  <li>reporter</li>
 	 *  <li>cc</li>
 	 *  <li>version</li>
	 *  </ul>
	 * @param name フィールド名
	 * @return フィールドの値
	 */
	public String $(String name){
		return fields.get(name);
	}

	/**
	 * フィールドの値の設定
	 * @param name フィールド名
	 * @param value フィールド値
	 */
	public void $(String name, String value){
		fields.put(name,value);
		if(modified.get(name)==null&&(!fields.get(name).equals(value))){
			modified.put(name,value);
		}
	}

	public HashMap<String,String> getModifiedFields() {
		return modified;
	}

	public void commit(){
		modified.clear();
	}

	public HashMap<String,String> getFields(){
		return fields;
	}

	public String toString(){
		return "#"+id+":"+fields.get("summary")+"("+changetime+")";
	}
}
