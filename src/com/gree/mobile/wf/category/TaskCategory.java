package com.gree.mobile.wf.category;

import java.util.List;

public class TaskCategory {

	private String name;
	private List<String> processes;
	private List<String> bosTypes;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getProcesses() {
		return processes;
	}
	public void setProcesses(List<String> processes) {
		this.processes = processes;
	}
	public List<String> getBosTypes() {
		return bosTypes;
	}
	public void setBosTypes(List<String> bosTypes) {
		this.bosTypes = bosTypes;
	}
	
}
