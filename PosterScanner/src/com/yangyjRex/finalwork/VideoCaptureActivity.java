package com.yangyjRex.finalwork;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

public class VideoCaptureActivity extends Activity implements SurfaceHolder.Callback{

	private static  Context mContext ;
	private Button button_record ;
	private boolean isRecord =false;
	private MediaRecorder mRecorder;
	private Camera mCamera;
	private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private File mMediaFile;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_capture);
		mPreview = (SurfaceView) findViewById(R.id.preview);
		mHolder = mPreview.getHolder();
		mHolder.addCallback(this);
		button_record = (Button) findViewById(R.id.button_record);
		mContext =VideoCaptureActivity.this;
		
	}

	
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
//	
	/**
	 * 绑定camera与activity的生命周期
	 */
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		if(mCamera==null){
			mCamera=getCamera();
			if(mHolder!=null){
				setStartPreview(mCamera, mHolder);
			}
			
			
		}
	}

	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();
		releaseCamera();
	}
	
	/**
	 * 开始预览
	 * @param mCamera2
	 * @param mHolder2
	 */
	private void setStartPreview(Camera camera, SurfaceHolder holder) {
		// TODO 自动生成的方法存根
		try {
			camera.setPreviewDisplay(holder);
			camera.startPreview();
			camera.setDisplayOrientation(90);          //预览角度修改
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO 自动生成的方法存根
		setStartPreview(mCamera, mHolder);
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO 自动生成的方法存根
		setStartPreview(mCamera, mHolder);
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO 自动生成的方法存根
		releaseCamera();
	}

	
	
	/**
	 * 录像
	 */
	
	public void record(View v){
		Log.d("record", "!!!!!!!!!!!!!!!!!!!!!");
		Log.d("record", "!!!!!!!!!!!!!!!!!!!!!");
		Log.d("record", "!!!!!!!!!!!!!!!!!!!!!");
		if (isRecord == false){
			
			mRecorder = new MediaRecorder();
			Log.d("record", "!!!!!!!!!!!!!!!!!!!!!111111111");
			mCamera.unlock();
			Log.d("record", "!!!!!!!!!!!!!!!!!!!!!22222222222");
			setMediaRecorder(mRecorder);
			Log.d("record", "!!!!!!!!!!!!!!!!!!!!!3333333333333");
			try {
				mRecorder.prepare();
				
			} catch (IllegalStateException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自动生成的 catch 块
				e.printStackTrace();
			}
			
			mRecorder.start();
			
			Log.d("record", "!!!!!!!!!!!!!!!!!!!!!44444444444444");
			isRecord =!isRecord;
			button_record.setText("拍摄中..");
			
		  }
		else{
			mRecorder.stop();
			mRecorder.release();
			mCamera.lock();
			isRecord =!isRecord;
			button_record.setText("摄像");
			
			saveImageToGallery(mContext, mMediaFile);///////保存到系统相册
			
			//pass the path of video to next activity
			Intent intent = new Intent(this, ProcessActivity.class);
			intent.putExtra("videoDir", mMediaFile.getPath());
			startActivity(intent);
			
			new  AlertDialog.Builder(this).setTitle("Tips").setMessage("正在处理中....").show();
			
		 }
			
		}
	
	
	/**
	 * 设置MediaRecorder
	 * @param recorder
	 */
	private void setMediaRecorder(MediaRecorder recorder) {
		// TODO 自动生成的方法存根
		recorder.setCamera(mCamera);
		recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		recorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));  //设置录像质量
		
//		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        
//
//        recorder.setVideoSize( 720	, 480 );
        recorder.setVideoFrameRate(5);
		
		
		mMediaFile=getOutputMediaFile(MEDIA_TYPE_VIDEO);
		recorder.setOutputFile(mMediaFile.toString());
		recorder.setPreviewDisplay(mHolder.getSurface());//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
	}
	
	/**
	 * 获取照相机camera对象
	 * @return
	 */
	private Camera getCamera(){
		 Camera camera;
	try {
		 camera = Camera.open();
	} catch (Exception e) {
		camera=null;
		e.printStackTrace();
		// TODO: handle exception
	   }	
		 return camera;
	}
	
	/**
	 * release camera
	 */
	private void releaseCamera(){
		if(mCamera!=null){
			//mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			
		}
		
	}
	
	
	

		/** Create a file Uri for saving an image or video */
		private static Uri getOutputMediaFileUri(int type){
		      return Uri.fromFile(getOutputMediaFile(type));
		}

		/** Create a File for saving an image or video 
		 * @param context */
		private static File getOutputMediaFile(int type){
		    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
		    		Environment.DIRECTORY_PICTURES), "MyCameraApp");
		    
		    if (! mediaStorageDir.exists()){
		        if (! mediaStorageDir.mkdirs()){
		            Log.d("MyCameraApp", "failed to create directory");
		            return null;
		        }
		    }

		    // Create a media file name
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		    File mediaFile;
		    if (type == MEDIA_TYPE_IMAGE){
		        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		        "IMG_"+ timeStamp + ".jpg");
		    } else if(type == MEDIA_TYPE_VIDEO) {
		        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
		        "VID_"+ timeStamp + ".mp4");
		      
		       
		        
		    } else {
		        return null;
		    }

		    return mediaFile;
		  }
/*
 * 保存到系统相册
 */
		public static void saveImageToGallery(Context context,File mediaFile) {
//			Log.d("record", "!!!!!!!!!!!!!!!!!!!!!");
//			try {
//				MediaStore.Images.Media.insertImage(context.getContentResolver(), mediaFile.getAbsolutePath(), mediaFile.getName(), null);
//			} catch (FileNotFoundException e) {
//				// TODO 自动生成的 catch 块
//				e.printStackTrace();
//				
//			}
			
//	       context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mediaFile.getPath()))); 
	       context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile))); 
	      
			
			
			
		}

		
		
}