package com.itcrusaders.msaleh.bioofly;

/**
 * Item 
 * Store file information such as file name, path, image
 * 
 * @author KUS, RKS
 * @date 2019/01/26
 */ 

public class FileItem implements Comparable<FileItem> {
	private String name; //file name
	private String data; //file data
	private String date; //created date
	private String path; //file path
	private String image; //file image icon
	
	/** constructor **/
	public FileItem(String n, String d, String dt, String p, String img)
	{
		name = n;
		data = d;
		date = dt;
		path = p; 
		image = img;
		
	}	
	public String getName()
	{
		return name;
	}
	public String getData()
	{
		return data;
	}
	public String getDate()
	{
		return date;
	}
	public String getPath()
	{
		return path;
	}
	public String getImage() {
		return image;
	}
	
	public int compareTo(FileItem o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase()); 
		else 
			throw new IllegalArgumentException();
	}
}
