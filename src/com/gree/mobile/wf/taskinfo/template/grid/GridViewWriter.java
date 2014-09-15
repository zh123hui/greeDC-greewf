package com.gree.mobile.wf.taskinfo.template.grid;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

public class GridViewWriter {

	private Writer writer;
	
	public GridViewWriter(Writer writer) {
		super();
		this.writer = writer;
	}
	
	public void writeGridView(GridView gridView) throws IOException{
		GridView parent = gridView.getParent();
		boolean isMainView = parent==null;
		String style = isMainView ? "" : "style=\"display: none;\"";
		writer.write(String.format("<table ellspacing=\"0\" cellpadding=\"0\" class=\"view\" id=\"%s\" %s >", gridView.getViewId(), style));
		writer.write("<thead>");
		writer.write("<tr>");
		String thClass = isMainView ? "th_title" : "th_sub_title";
		String viewTitle = isMainView ? gridView.getTitle() : "<span onclick=\"__BU.returnTo()\" class=\"arrow_l\"><<&nbsp;返回</span>"+parent.getTitle()+"-"+gridView.getTitle();
		writer.write(String.format("<th colspan=\"4\" class=\"%s\">%s</th>", thClass, viewTitle));
		writer.write("</tr>");
		writer.write("</thead>");
		writer.write("<tbody>");
		
		List<CellUI> cells = gridView.getCells();
		for(CellUI cell : cells){
			if(cell instanceof PropCell){
				writeRow((PropCell) cell);
			}
			if(cell instanceof RowCell){
				writeRow((RowCell) cell);
			}
			if(cell instanceof IteratorCell){
				writeRows((IteratorCell) cell);
			}
		}
		List<GridView> subGridView = gridView.getSubGridView();
		boolean hasSub = !CollectionUtils.isEmpty(subGridView);
		if(hasSub){
			writeButtonRows(subGridView);
		}
		writer.write("</tbody>");
		writer.write("</table>");
		if(hasSub){
			for(GridView gv : subGridView){
				writeGridView(gv);
			}
		}
	}
	public void writeButtonRows(List<GridView> gridViews) throws IOException{
		int counter=0;
		writer.write("<tr>");
		for(GridView gv : gridViews){
			if(counter%4==0){
				if(counter!=0){
					writer.write("</tr>");
				}
				writer.write("<tr>");
			}
			writer.write(String.format("<td width=\"%s\" class=\"td_btn\"><a onclick=\"__BU.forwardTo('%s')\">%s</a></td>", "25%", gv.getViewId(), gv.getTitle()));
			counter++;
		}
		while(counter%4 != 0){
			writeValue("", 1);
			counter++;
		}
		writer.write("</tr>");
		
	}
	public void writeRow(PropCell propCell) throws IOException{
		if(propCell.isTwoLine()){
			writer.write("<tr>");
			writeTitle(propCell.getTitle(), 4, propCell.getTitleCss());
			writer.write("</tr>");
			writer.write("<tr>");
			writeValue(propCell.getValue(), 4);
			writer.write("</tr>");
			return;
		}
		writer.write("<tr>");
		writeTitle(propCell.getTitle());
		writeValue(propCell.getValue(), 3);
		writer.write("</tr>");
	}
	public void writeRow(RowCell rowCell) throws IOException{
		writeRow(rowCell.getPropCells());
	}
	public void writeRow(List<PropCell> propCells) throws IOException{
		if(propCells==null){
			return ;
		}
		int size = propCells.size();
		if(size!=1 && size!=2){
			return ;
		}
		
		if(size==1){
			writeRow(propCells.get(0));
			return;
		}
		writer.write("<tr>");
		for(PropCell pc : propCells){
			writeTitle(pc.getTitle());
			writeValue(pc.getValue(), 1);
		}
		writer.write("</tr>");
	}
	
	public void writeRows(IteratorCell iteratorCell) throws IOException{
		List<List<PropCell>> propCells = iteratorCell.getPropCellList();
		if(CollectionUtils.isEmpty(propCells)){
			return ;
		}
		List<PropCell> first = propCells.get(0);
		int count = first.size();
		switch(count){
		case 1 :
		case 2 :
			for(List<PropCell> pcs : propCells){
				writeRow(pcs);
			}
			break;
		case 3 :
		case 4 :
			writeRowTitle(first);
			for(List<PropCell> pcs : propCells){
				writeRowValue(pcs);
			}
			break;
		default :
		}
	}
	private void writeRowTitle(List<PropCell> propCells) throws IOException{
		if(propCells==null){
			return ;
		}
		int size = propCells.size();
		if(size!=3 && size!=4){
			return ;
		}
		writer.write("<tr>");
		for(PropCell pc : propCells){
			writeTitle(pc.getTitle());
		}
		if(size==3){
			writeTitle("&nbsp;");
		}
		writer.write("</tr>");
	}
	private void writeTitle(String title) throws IOException{
		writeTitle(title, 1, null);
	}
	private void writeTitle(String title, int colspan, String titleCss) throws IOException{
		if(colspan<1 || colspan>4){
			return;
		}
		String width = 25*colspan+"%";
		String attrStr = "";
		if(colspan>1){
			attrStr = "colspan=\""+colspan+"\"";
		}
		if(titleCss!=null){
		    attrStr += "style=\""+titleCss+"\"";
		}
		writer.write(String.format("<td width=\"%s\" class=\"td_title\" %s >%s</td>", width, attrStr, title));
	}
	private void writeValue(String value, int colspan) throws IOException{
		if(colspan<1 || colspan>4){
			return;
		}
		String width = 25*colspan+"%";
		String colsStr = "";
		if(colspan>1){
			colsStr = "colspan=\""+colspan+"\"";
		}
		writer.write(String.format("<td width=\"%s\" %s >%s</td>", width, colsStr, value));
	}
	private void writeRowValue(List<PropCell> propCells) throws IOException{
		if(propCells==null){
			return ;
		}
		int size = propCells.size();
		if(size!=3 && size!=4){
			return ;
		}
		writer.write("<tr>");
		for(PropCell pc : propCells){
			writeValue(pc.getValue(), 1);
		}
		if(size==3){
			writeValue("&nbsp;", 1);
		}
		writer.write("</tr>");
	}
	
}
