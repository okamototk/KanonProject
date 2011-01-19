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

Attribution Information: Attribution Copyright Notice: Copyright © 2006, 2007
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
package com.projity.pm.costing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;

import com.projity.algorithm.CollectionIntervalGenerator;
import com.projity.algorithm.Query;
import com.projity.algorithm.SelectFrom;
import com.projity.algorithm.WeightedSum;
import com.projity.datatype.Money;
import com.projity.grouping.core.model.NodeModel;
import com.projity.interval.ValueObjectForInterval;
import com.projity.interval.ValueObjectForIntervalTable;
import com.projity.pm.assignment.functor.AssignmentFieldFunctor;
import com.projity.pm.calendar.WorkCalendar;
import com.projity.pm.calendar.WorkingCalendar;
import com.projity.undo.DataFactoryUndoController;
import com.projity.util.DateTime;

public class CurrencyRateTable extends ValueObjectForIntervalTable{
	private static final long serialVersionUID = 9110087379489649891L;
	String baseCurrency = "USD";
	private static CurrencyRateTable instance = null;
	private boolean active = true;
	private long effectiveDate = DateTime.midnightToday(); // default use today;
	public static CurrencyRateTable getInstance() {
		if(null == instance){
			instance = new CurrencyRateTable();
		}
		return instance;
	}
	public static void setInstance(CurrencyRateTable t) {
		instance = t;
	}
	public CurrencyRateTable(String baseCurrency, String displayCurrency) {
		super(displayCurrency);
		add(0,1.0); // put in initial non  zero value;
		this.baseCurrency = baseCurrency;
		Money.setCurrency(displayCurrency);

//		initUndo();
	}
	private CurrencyRateTable() {
		super();
		add(0,1.0); // put in initial non  zero value;
//		initUndo();
	}

	/* (non-Javadoc)
	 * @see com.projity.configuration.NamedItem#getCategory()
	 */
	public String getCategory() {
		return "CurrencyRate";
	}
	/* (non-Javadoc)
	 * @see com.projity.pm.costing.ValueObjectForIntervalTable#createValueObject(long)
	 */
	protected ValueObjectForInterval createValueObject(long date) {
		return new CurrencyRate(this,date);
	}



	public static CurrencyRateTable deserialize(ObjectInputStream s) throws IOException, ClassNotFoundException  {
		return (CurrencyRateTable)deserialize(s,new CurrencyRateTable());
	}

	protected boolean isGroupDirty=false;
	public final boolean isGroupDirty() {
		return isGroupDirty;
	}
	public final void setGroupDirty(boolean isGroupDirty) {
		this.isGroupDirty = isGroupDirty;
	}

	//Undo
	protected transient DataFactoryUndoController undoController;
	protected void initUndo(){
		undoController=new DataFactoryUndoController(this);
	}
	public DataFactoryUndoController getUndoController() {
		return undoController;
	}

	public void initOutline(NodeModel nodeModel){}

	public boolean containsAssignments(){return false;}


	protected ValueObjectForInterval createValueObject() {
		return new CurrencyRate();
	}
	public String getBaseCurrency() {
		return baseCurrency;
	}
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}
	public String getDisplayCurrency() {
		return getName();
	}
	public void setDisplayCurrency(String displayCurrency) {
		Money.setCurrency(displayCurrency);
		this.name = displayCurrency;
	}
	public static boolean isActive() {
		if (instance == null)
			return false;
		return instance.active;
	}
	public static void setActive(boolean active) {
		if (instance == null)
			return;
		instance.active = active;
	}
	
	private double getMultiplier() {
		if (!isActive()) {
			System.out.println("not active - returning 1.0");
			return 1.0D;
		}
		return ((CurrencyRate)findActive(effectiveDate)).getRate();

	}
	public static Number convertToDisplay(Number value) {
		if (value == null)
			return null;
		if (instance == null){
			getInstance();
		}
		double newValue = value.doubleValue() * instance.getMultiplier();
		
//System.out.println("convert to display " + value + " " + newValue);		
		if (value instanceof Money){
			//System.out.println("returning Money value of: "+ Money.getInstance(newValue) +" from CurrencyRateTable.convertToDisplay");
			return Money.getInstance(newValue);
		}
		else
		{
			//System.out.println("returning Double value of: "+ Double.valueOf(newValue) +" from CurrencyRateTable.convertToDisplay");
			return Double.valueOf(newValue);
		}	
	}
	public static Number convertFromDisplay(Number value) {
		if (value == null)
			return null;
		if (instance == null)
			return 1.0;
		double newValue =  value.doubleValue() / instance.getMultiplier();
//		System.out.println("convert from display " + value + " " + newValue);		
		if (value instanceof Money)
		{
			//System.out.println("returning Money value of: "+ Money.getInstance(newValue) +" from CurrencyRateTable.convertFromDisplay");
			return Money.getInstance(newValue);
		}
		else
		{
			//System.out.println("returning Double value of: "+ Double.valueOf(newValue) +" from CurrencyRateTable.convertFromDisplay");
			return Double.valueOf(newValue);
		}
	}
	public long getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(long effectiveDate) {
		System.out.println("xxxx setting effective date to " + new java.util.Date(effectiveDate));
		this.effectiveDate = effectiveDate;
	}

	public double calculateWeightedRateOverPeriod(long start, long end, WorkCalendar cal) {
		long duration = cal.compare(end,start,false);
		if (duration == 0)
			return 0;
		SelectFrom clause = SelectFrom.getInstance();
		CollectionIntervalGenerator currencyRate = CollectionIntervalGenerator.getInstance(getList()); // go through project which will delegate to global value. Should realy be project related, not global
		WeightedSum weightedSum = WeightedSum.getInstance(cal,currencyRate);
		
		clause.select(weightedSum)
			.from(currencyRate)
			.whereInRange(start,end);
		
		Query.getInstance().selectFrom(clause).execute();
		
		return weightedSum.getValue() / duration;
	}
}

