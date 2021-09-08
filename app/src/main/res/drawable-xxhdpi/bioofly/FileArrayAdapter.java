package com.itcrusaders.msaleh.bioofly;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itcrusaders.msaleh.R;

import java.util.List;

/**
 * FileArrayAdapter 
 * Store file information array such as file name, created date, file image name
 * 
 * @author KUS, RKS
 * @date 2019/01/26
 */ 

public class FileArrayAdapter extends ArrayAdapter<FileItem> {

	private Context c;
	private int id;
	private List<FileItem> items;
	
	public FileArrayAdapter(Context context, int textViewResourceId,
                            List<FileItem> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}
	public FileItem getItem(int i)
	 {
		 return items.get(i);
	 }
	
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
           View v = convertView;
           if (v == null) {
               LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
               v = vi.inflate(id, null);
           }
           
           /* create a new view of my layout and inflate it in the row */
   		   //convertView = ( RelativeLayout ) inflater.inflate( resource, null );
   		
           final FileItem o = items.get(position);
           if (o != null) {
                   TextView t1 = (TextView) v.findViewById(R.id.TextView01);
                   TextView t2 = (TextView) v.findViewById(R.id.TextView02);
                   TextView t3 = (TextView) v.findViewById(R.id.TextViewDate);
                   
                   /* Take the ImageView from layout and set the city's image */
               		ImageView imageCity = (ImageView) v.findViewById(R.id.fd_Icon1);
               		String uri = "drawable/" + o.getImage();
               	    int imageResource = c.getResources().getIdentifier(uri, null, c.getPackageName());
               	    Drawable image = c.getResources().getDrawable(imageResource);
               	    imageCity.setImageDrawable(image);
                   
                   if(t1!=null)
                   	t1.setText(o.getName());
                   if(t2!=null)
                      	t2.setText(o.getData());
                   if(t3!=null)
                      	t3.setText(o.getDate());
                   
           }
           return v;
   }

}
