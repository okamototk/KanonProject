package com.projity.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.MapIterator;

@SuppressWarnings("unchecked")
public class HashMapWithDirtyFlags extends HashMap {
	private static final long serialVersionUID = -3225582409855164110L;
	private Set dirtyKeys = new HashSet();
	public void markAsClean() {
		dirtyKeys.clear();
	}
	@Override
	public Object put(Object key, Object value) {
		dirtyKeys.add(key);
		return super.put(key, value);
	}
	
	public Set dirtyKeySet() {
		return dirtyKeys;
	}
	public boolean isDirty() {
		return !dirtyKeys.isEmpty();
	}
	private void dirtyAll() {
		dirtyKeys.addAll(keySet());
	}
	@Override
	public Object clone() {
		HashMapWithDirtyFlags result = (HashMapWithDirtyFlags) super.clone();
		result.dirtyAll(); // the copied object has all dirty keys since they need to be saved
		return result;
	}
	
}
