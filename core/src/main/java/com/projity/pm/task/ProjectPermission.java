package com.projity.pm.task;

public class ProjectPermission {
	boolean read = true;
	boolean write = true;
	boolean saveBaseline = true;
	boolean breakLock = true;
	boolean deleteActuals = true;
	public boolean isRead() {
		return read;
	}
	public void setRead(boolean read) {
		this.read = read;
	}
	public boolean isWrite() {
		return write;
	}
	public void setWrite(boolean write) {
		this.write = write;
	}
	public boolean isSaveBaseline() {
		return saveBaseline;
	}
	public void setSaveBaseline(boolean saveBaseline) {
		this.saveBaseline = saveBaseline;
	}
	public boolean isBreakLock() {
		return breakLock;
	}
	public void setBreakLock(boolean breakLock) {
		this.breakLock = breakLock;
	}
	public boolean isDeleteActuals() {
		return deleteActuals;
	}
	public void setDeleteActuals(boolean deleteActuals) {
		this.deleteActuals = deleteActuals;
	}
	public void denyAll() {
		read = false;
		write = false;
		saveBaseline = false;
		breakLock = false;
		deleteActuals = false;
	}
	
}
