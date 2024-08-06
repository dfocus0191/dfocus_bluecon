package com.example.remix.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GetFileRead {
	
	private File path;
	
	public String getFileRead(String filename){
		
		//임시추후 변경.
		
		File SDCardRoot = Environment.getExternalStorageDirectory();
		
		//해당폴더생성
		path = new File("/"+SDCardRoot+"/BLUECON");

		//path = new File(String.valueOf(login_form.this.getFilesDir()));

	     if(! path.isDirectory()) {
	             path.mkdirs();
	    }		
	    File f = new File(path + "/SavedData", filename);
	    
	     //File f = new File(MnuDiv_MenuList.PATH + "/SavedData", filename);
		
		FileReader fr = null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		BufferedReader br = new BufferedReader(fr);

		try {
			return br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	//추후 변경.
	public	void setFileWrite(String filename, String content, boolean add){
			
			File SDCardRoot = Environment.getExternalStorageDirectory();
			
			path = new File("/"+SDCardRoot+"/BLUECON");
		     if(! path.isDirectory()) {
		             path.mkdirs();
		    }	
		     
			File f = new File(path + "/SavedData");
			if(!f.isDirectory() && !f.exists()){
				f.mkdir();
			}
			
			f = new File(path + "/SavedData", filename);
			if(!f.exists()){
				try {
					f.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			FileWriter fw = null;
			try {
				fw = new FileWriter(f, add);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BufferedWriter bw = new BufferedWriter(fw);

			try {
				bw.write(content);
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					bw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
	
		public String getFileReadNFC(String filename){
		
		//임시추후 변경.
		
		File SDCardRoot = Environment.getExternalStorageDirectory();
		
		//해당폴더생성
		path = new File("/"+SDCardRoot+"/BLUECON");
	     if(! path.isDirectory()) {
	             path.mkdirs();
	    }		
	    File f = new File(path + "/SavedData", filename);

		FileReader fr = null;
		try {
			fr = new FileReader(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		BufferedReader br = new BufferedReader(fr);

		try {
			return br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
		public	void NTextWrite(String filename, String content) 
		
		{
			File SDCardRoot = Environment.getExternalStorageDirectory();
			
			File path2 = new File("/"+SDCardRoot+"/BLUECON");
			
			File savefile = new File(path2+"/SavedData/"+filename+"");
			
			try{
				
				FileOutputStream fos = new FileOutputStream(savefile);
				
				fos.write(content.getBytes());
				
				fos.close();		

				
				} catch(IOException e){}
			
		}
				

}
