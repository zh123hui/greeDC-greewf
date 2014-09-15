package com.gree.mobile.wf.category;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.gree.mobile.wf.category.TaskCategoryLoader.Category;

public class TaskCategoryLoader {
	
	private Digester digester;
	
	public TaskCategoryLoader() {
		this.digester = createDigester();
	}
	
	public TaskCategoryLoader(Digester digester) {
		this.digester = digester;
	}

	public List<Category> loadCategories(File file) throws IOException, SAXException{
		if(digester==null){
			digester = createDigester();			
		}
		Categories categories = new Categories();
		digester.push(categories);
		try{
			digester.parse(file);
			return categories.taskCategories;
		}finally{
			digester.clear();
		}
//		List<Category> taskCategories = categories.taskCategories;
//		for(Category vo : taskCategories){
//			String name = vo.getName();
//			int seq = vo.getSeq();
//			List<ProcessDef> processDefs = vo.getProcessDefs();
//			for(ProcessDef p : processDefs){
//				String processID = p.getProcessID();
//				
//			}
//		}
	}

	private static class Categories{
		private List<Category> taskCategories = new ArrayList<Category>();
		public void addCategory(Category vo) {
			taskCategories.add(vo);
		}
	}
	public static class Category {
		private String name;
		private int seq;
		private List<ProcessDef> processDefs=new ArrayList<ProcessDef>();

		public void addProcessDef(ProcessDef def){
			processDefs.add(def);
		}
		public static class ProcessDef{
			private String processID;
			private String name;
			public ProcessDef() {
			}
			public ProcessDef(String processID, String name) {
				super();
				this.processID = processID;
				this.name = name;
			}
			public String getProcessID() {
				return processID;
			}
			public void setProcessID(String processID) {
				this.processID = processID;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<ProcessDef> getProcessDefs() {
			return processDefs;
		}
		public void setProcessDefs(List<ProcessDef> processDefs) {
			this.processDefs = processDefs;
		}
		public void setSeq(int seq) {
			this.seq = seq;
		}
		public int getSeq() {
			return seq;
		}
	}
	
	private Digester createDigester() {
		Digester digester = new Digester();
//		digester.setValidating(true);
		digester.setClassLoader(TaskCategoryLoader.class.getClassLoader());
		digester.addObjectCreate("categories/category", Category.class);
		digester.addSetProperties("categories/category");
		digester.addSetNext("categories/category", "addCategory");
		digester.addObjectCreate("categories/category/processDef", Category.ProcessDef.class);
		digester.addSetProperties("categories/category/processDef");
		digester.addSetNext("categories/category/processDef", "addProcessDef");
		return digester;
	}
	
	public static void main(String[] args) throws Exception {
	    TaskCategoryLoader cattegoryLoader = new TaskCategoryLoader();
	    List<Category> taskCategories = cattegoryLoader.loadCategories(new File("F:\\zh-private\\gree\\workspace\\greemobile\\webroot\\config\\taskCategory-null.xml"));
	    System.out.println(taskCategories.size());
    }

}
