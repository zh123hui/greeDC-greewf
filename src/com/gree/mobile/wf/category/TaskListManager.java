package com.gree.mobile.wf.category;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.gree.mobile.common.web.PathUtils;
import com.gree.mobile.wf.category.TaskCategoryLoader.Category;
import com.gree.mobile.wf.category.TaskCategoryLoader.Category.ProcessDef;
import com.gree.mobile.wf.config.GreeWfCofingCenter;
import com.gree.mobile.wf.vo.TaskCategoryVO;

public class TaskListManager {

	private static final Logger logger =Logger.getLogger(TaskListManager.class);
	
	private static TaskListManager manager = new TaskListManager();

	private FileChangedReloadingStrategy strategy;
	private TaskCategoryLoader cattegoryLoader;
	private Map<String, CategoryKey> categoryMap = new ConcurrentHashMap<String, CategoryKey>();

	private TaskListManager() {
		
		File file = new File(PathUtils.getConfigRoot()+"/taskCategory.xml");
		if(!file.exists()){
			
		}
		strategy = new FileChangedReloadingStrategy(file);
		cattegoryLoader = new TaskCategoryLoader();
	}

	public static TaskListManager getManager() {
		return manager;
	}

	public boolean excludeProccess(String proccessId) {
		return false;
	}

	private void reload(){
		if(strategy.reloadingRequired()){
			load();
		}
	}
	
	private void load(){
		synchronized (cattegoryLoader) {
			if(!strategy.reloadingRequired()){
				return ;
			}
			List<Category> taskCategories;
			try{
				taskCategories = cattegoryLoader.loadCategories(strategy.getFile());
				strategy.reloadingPerformed();
			}catch (Exception e) {
				logger.error("", e);
				return ;
			}
			for(Category vo : taskCategories){
				String name = vo.getName();
				int seq = vo.getSeq();
				List<ProcessDef> processDefs = vo.getProcessDefs();
				for(ProcessDef p : processDefs){
					String processID = p.getProcessID();
					if(StringUtils.isEmpty(processID)){
						continue;
					}
					categoryMap.put(processID, CategoryKey.newInstance(name, seq));
				}
			}
		}
	}
	
	public CategoryKey findCategoryKey(String proccessId) {
	    if(!GreeWfCofingCenter.getInstance().getBoolean("gree.tasklist.category.enable", false)){
	        return null;
	    }
		reload();
		return categoryMap.get(proccessId);
	}

	public List<TaskCategoryVO> getSortList(Map<CategoryKey, TaskCategoryVO> map) {
		List<CategoryKey> keys = new ArrayList<CategoryKey>(map.keySet());
		Collections.sort(keys);
		List<TaskCategoryVO> list = new ArrayList<TaskCategoryVO>();
		for(CategoryKey key : keys){
			list.add(map.get(key));
		}
		return list;
	}

	
}
