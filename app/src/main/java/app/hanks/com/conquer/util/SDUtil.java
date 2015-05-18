package app.hanks.com.conquer.util;

import android.os.Environment;

import java.io.File;

public class SDUtil {

	public static boolean sdIsAvail(){
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}
	
	/**
	 * 返回自定义工程路径
	 * @return
	 */
	public static String getProjectDir(){
		String dir = null;
		if(sdIsAvail()){
			dir = Environment.getExternalStorageDirectory().getPath()+File.separatorChar+"pnszx";
			File f = new File(dir);
			if(!f.exists()){
				f.mkdirs();
			}
			return dir;
		}else{
			return null;
		}
	}
	
	
}
