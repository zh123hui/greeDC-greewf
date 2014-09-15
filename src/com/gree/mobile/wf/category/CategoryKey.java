package com.gree.mobile.wf.category;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CategoryKey implements Comparable<CategoryKey>{
	private String name;
	private int seq;
	private int hashCode;

	private CategoryKey(String name, int seq) {
		super();
		this.name = name;
		this.seq = seq;
		this.hashCode = name.hashCode();
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CategoryKey)) {
			return false;
		}
		if (o == this) {
			return true;
		}
		CategoryKey other = (CategoryKey) o;
		return name.equals(other.name);
	}

	@Override
	public String toString() {
		return "[name='" + name + "']";
	}

	public static CategoryKey newInstance(String name, int defaultSeq) {
		CategoryKey categoryKey = cache.get(name);
		if(categoryKey!=null){
			return categoryKey;
		}
		synchronized (cache) {
			if(cache.containsKey(name)){
				return cache.get(name);
			}
			categoryKey = new CategoryKey(name, defaultSeq);
			cache.put(name, categoryKey);
			return categoryKey;
		}
	}
	private static Map<String, CategoryKey> cache = new ConcurrentHashMap<String, CategoryKey>();

	public String getName() {
		return name;
	}

	public int getSeq() {
		return seq;
	}

	public int compareTo(CategoryKey o) {
		return seq-o.seq;
	}

}