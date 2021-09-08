package com.itcrusaders.msaleh.bioofly;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.itcrusaders.msaleh.R;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

;

/**
 * FileChooser 
 * File choose class
 * @author KUS, RKS 
 * @date 2019/01/29
 */
public class FileChooser extends ListActivity {
	
	//file directory constant
	//public static final String DIRECTORY_PATH = "/storage/sdcard0/";
	public static final String DIRECTORY_PATH = "/storage/emulated/0/telpo";
	public static final String DIRECTORY_ROOT = "/"; //root directory
	private String pathRoot = "/";
	private String sFileExtension = "";
	private File currentDir; //directory
    private FileArrayAdapter adapter; //file array adapter
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 

        Intent intent = getIntent();
        sFileExtension = intent.getExtras().getString("fileExtension");
        
        currentDir = new File(DIRECTORY_PATH);
        
        fill(currentDir);  
    }
    
    /**
     * Get file list from directory
     * @param f   file name
     */
    private void fill(File f)
    {    	 
    	 File[]dirs = f.listFiles();
		 this.setTitle("Current Dir: "+f.getName());
		 List<FileItem> dir = new ArrayList<FileItem>();
		 List<FileItem> fls = new ArrayList<FileItem>();
		 try{
			 for(File ff: dirs)
			 { 
				Date lastModDate = new Date(ff.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				
				if(ff.isDirectory()){				
					
					File[] fbuf = ff.listFiles();
					int buf = 0;
					if(fbuf != null){ 
						buf = fbuf.length;
					} 
					else buf = 0; 
					String num_item = String.valueOf(buf);
					if(buf == 0) num_item = num_item + " item";
					else num_item = num_item + " items"; 
		 
					dir.add(new FileItem(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon")); 
				}
				else
				{
					if (ff.getName().toLowerCase().endsWith(sFileExtension))	//	display file filter
				    {
						fls.add(new FileItem(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
				    }
				}
			 }
		 }catch(Exception e)
		 {    
			 
		 }
		 
		 Collections.sort(dir);
		 Collections.sort(fls);
		 dir.addAll(fls);
		 
		 if(!f.getName().equalsIgnoreCase("sdcard"))
			 dir.add(0,new FileItem("..","Parent Directory","",f.getParent(),"directory_up"));
		 adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,dir);
		 this.setListAdapter(adapter); 
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
 
		super.onListItemClick(l, v, position, id);
		String path = ""; //file path
		
		FileItem o = adapter.getItem(position); //get file item
		
		//if file is directory or directory up, then get new directory information
		if(o.getImage().equalsIgnoreCase("directory_icon")
				||o.getImage().equalsIgnoreCase("directory_up"))
		{			 
			path = o.getPath();	
			//Log.i("LOG", "directory path = " + path );
			if(path.equals(DIRECTORY_ROOT)) return;			
			//Log.i("LOG", "directory path = " + o.getPath() );
			currentDir = new File(o.getPath());
 
			fill(currentDir);
		}
		else //item is file
		{
			onFileClick(o);
		}
	}
    
    /**
     * Set intent with file name and path and finish activity
     * @param o  selected file
     */
    private void onFileClick(FileItem o)
    {    	 
    	Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
