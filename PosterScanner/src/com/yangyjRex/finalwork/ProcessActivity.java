package com.yangyjRex.finalwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;




import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import com.yangyjRex.finalwork.stitcher.SIFTExtractor;

import android.R.integer;
import android.animation.Keyframe;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaCodec;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;


public class ProcessActivity extends Activity {

	
	
	
	private String videoFilePath;
	private VideoView video1;  
    MediaController  mediaco;   
    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
	private Mat image_gray;
	java.util.List<Mat> histList = new LinkedList<Mat>();
	ArrayList<Integer> keyFrame = new ArrayList<Integer>();
	java.util.List<Bitmap> mat4sift = new LinkedList<Bitmap>();
	
	
    private static  Context context ;

    
    //OpenCV����ز���ʼ���ɹ���Ļص����� /////////////////////////////////////////////////////////// 
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {  
  
        @Override  
        public void onManagerConnected(int status) {  
            // TODO Auto-generated method stub  
            switch (status){  
            case BaseLoaderCallback.SUCCESS:  
                Log.i("TAG", "�ɹ�����Opencv");  
                break;  
            default:  
                super.onManagerConnected(status);  
                Log.i("TAG", "����ʧ��Opencv");  
                break;  
            }  
              
        }  
    }; 
 //////////////////////////////////////////////////////////////////////////////////////////   
    
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		
		Log.i("TAG", "onCreate");
	    super.onCreate(savedInstanceState);

	    
		
		final Intent intent = getIntent();
		videoFilePath = intent.getStringExtra("videoDir");
		context =ProcessActivity.this;
		video1=(VideoView)findViewById(R.id.videoView);  
        mediaco=new MediaController(this);  
        File file=new File(videoFilePath);  
        if(file.exists()){  
            //VideoView��MediaController���й���  
            video1.setVideoPath(file.getAbsolutePath());  
            video1.setMediaController(mediaco);  
            mediaco.setMediaPlayer(video1);  
            //��VideiView��ȡ����  
            video1.requestFocus();  
        
        	
            Log.d("Tag", "!!!!!!!videoFilePath:"+videoFilePath);
//        	extractFrames2(videoFilePath);
        	
         
            
            
        }  
	}

	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.process, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	
	public void startProcess(View v){

		
		extractFrames(videoFilePath);
		
		
	}
	
	
	
	public void optFlow(View v){
		extractFramesByOptFlow(videoFilePath);
		
//		MatOfDMatch siftMatcher = SIFTExtractor.matchSIFT(mat4sift.get(0), mat4sift.get(1));
//		List<DMatch> dmatch = siftMatcher.toList();
//		for(int i=0; i<dmatch.size();i++){
//			Log.d("Tag", "!!!!!!!--------"+i+"  :  "+dmatch.get(i).toString());
//		}
	
	}
	
	private void extractFramesByOptFlow(String filePath) {
		// TODO �Զ����ɵķ������
		ArrayList<Integer> optFlowValue = new ArrayList<Integer>();
		retriever.setDataSource(filePath);
		String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		Bitmap prebitmap =null;
		Bitmap nextbitmap =null;
//		Canvas canvas = new Canvas( nextbitmap );
		int  fps = 5;
		int step ;
		int duration2 = Integer.valueOf(duration)*1000; 
		step = 1000000/fps;
		
		prebitmap = retriever.getFrameAtTime(0,MediaMetadataRetriever.OPTION_CLOSEST);
//		saveImage(prebitmap, (int) 0);
		for(long time=step;time<duration2;time=time+step){
			
			nextbitmap = retriever.getFrameAtTime(time,MediaMetadataRetriever.OPTION_CLOSEST);
			optFlowValue.add( CalOpticalFlowPyrLK(prebitmap, nextbitmap, 100) );
			
			prebitmap.recycle();
			prebitmap = null;
			System.gc();
			
			prebitmap = nextbitmap.copy(nextbitmap.getConfig(), true);
//			saveImage(nextbitmap, (int) time);
			nextbitmap.recycle();
			nextbitmap = null;
			System.gc();
			
		}
		
		int value;
		ArrayList<Integer> peak = new ArrayList<Integer>();
		
		for(int key=0;key<optFlowValue.size();key++){
			value = optFlowValue.get(key);
			Log.d("Tag", "!!!!!!!!!!!!!!!!vvvvvvvvvalue   "+key+"  :  "+ value);
			if(key==0 && value==0){
				peak.add(key);
			}
			if(value>0){
				peak.add(key);
			}
			
		}
		if(peak.get(peak.size()-1) < (optFlowValue.size()-1)){
			peak.add( optFlowValue.size()-1  );
		}
		
		
		
		
	    if(peak.size()<optFlowValue.size()){
	    	
	    	Log.d("Tag", "!!!!!!!!!!!!!!!!pppppeak   "+0+"  :  "+ peak.get(0));
	    	for(int i=1;i<peak.size();i++){
//	    		for(int j=peak.get(i);j>peak.get(i-1);j--){
//	    			if(optFlowValue.get(j)==0){
//	    				keyFrame.add(j);
//	    				break;
//	    			}
//	    		}
	    		if(peak.get(i)-peak.get(i-1) >1){
	    			keyFrame.add( (peak.get(i)+peak.get(i-1))/2 );
	    		}
				
				Log.d("Tag", "!!!!!!!!!!!!!!!!pppppeak   "+i+"  :  "+ peak.get(i));
			}
	    }else{
	    	new  AlertDialog.Builder(this).setTitle("Error").setMessage("�����²ɼ�����").show();
	    }
		
	    for(int i=0; i<keyFrame.size();i++){
	    	Log.d("Tag", "!!!!!!!!!!!!!!!!kkkkkkFrame  "+i+"  :  "+ keyFrame.get(i));
	    }
	    Bitmap frame = null;
	    int keyFrameNum = 0;
	    for(long time=0;time<duration2;time=time+step){
	    	
	    	if(keyFrame.contains(keyFrameNum)){
	    		frame = retriever.getFrameAtTime(time,MediaMetadataRetriever.OPTION_CLOSEST);
	    		saveImage(frame,(int)time);
	    		mat4sift.add(frame);
	    		frame.recycle();
	    		frame = null;
	    		System.gc();
	    	}
	    	keyFrameNum++;
	    }
	    new  AlertDialog.Builder(this).setTitle("Tips").setMessage("����ɣ�����").show();
	}

	/*
     * LKϡ������ļ���
     * return result of the opticalFlow value
     */
    public int CalOpticalFlowPyrLK(Bitmap preFrame, Bitmap nextFrame, int maxcorners){
    	 
    	int maxCorners = maxcorners;
    	ArrayList<Integer> OptFlowValue = new ArrayList<Integer>();
    	int result = 0;
    	
    	
    	int height = preFrame.getHeight();
		int width = preFrame.getWidth();
    	Mat mGray1 = new Mat(height, width, CvType.CV_8UC1);
    	Mat mGray2 = new Mat(height, width, CvType.CV_8UC1);
    	Utils.bitmapToMat(preFrame, mGray1);
    	Utils.bitmapToMat(nextFrame, mGray2);
    	Imgproc.cvtColor(mGray1,mGray1,Imgproc.COLOR_BGR2GRAY,1);
    	Imgproc.cvtColor(mGray2,mGray2,Imgproc.COLOR_BGR2GRAY,1);
//    	Mat mView = new Mat(height, width, CvType.CV_8UC4);
    	MatOfPoint initial = new MatOfPoint();
    	MatOfByte status = new MatOfByte();
    	MatOfFloat err = new MatOfFloat();
//    	Mat mask = new Mat(mRgba1.size(), CvType.CV_8UC1);
    	MatOfPoint2f prevPts = new MatOfPoint2f();
    	MatOfPoint2f nextPts = new MatOfPoint2f();
    	Size winSize = new Size(10 , 10);
    	
    	
  
    	Log.i("TAG", "ͨ������"+mGray1.channels());
    	Log.i("TAG", "ͨ������"+mGray2.channels());
    	
  
    	Imgproc.goodFeaturesToTrack(mGray1, initial, maxCorners, 0.01, 0.01);
    	initial.convertTo(prevPts, CvType.CV_32FC2);
    	
		
    	
    	Video.calcOpticalFlowPyrLK(mGray1, mGray2, prevPts, nextPts, status, err, winSize, 5);
    	
    	Point[] pointp = prevPts.toArray();
    	Point[] pointn = nextPts.toArray();
    	for(int i=0;i<pointp.length;i++){
    		
    		
//        	Log.i(TAG, "ǰ֡��"+i+"���ǵ㣺"+"("+(int)pointp[i].x+","+(int)pointp[i].y+")" );
//        	Log.i(TAG, "��֡��"+i+"���ǵ㣺"+"("+(int)pointn[i].x+","+(int)pointn[i].y+")"  );
        	
        	OptFlowValue.add((int) (Math.sqrt( (pointn[i].x-pointp[i].x)*(pointn[i].x-pointp[i].x) + (pointn[i].y-pointp[i].y)*(pointn[i].y-pointp[i].y) ) )) ;
//        	Log.i("TAG", "��"+i+"���ǵ�Ĺ���ֵ��"+(int)OptFlowValue[i] );
        	
    	}
    	for(int k=0;k<OptFlowValue.size();k++){
    		result = result + OptFlowValue.get(k);
    		result = result / OptFlowValue.size();
    	}
    	
		return result;
    	
    	
    }









	public void extractFrames(String filePath) {
		// TODO �Զ����ɵķ������

		Bitmap bitmap =null;
		int  fps = 5;
		int step ;
		
			
			retriever.setDataSource(filePath);
			String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			int duration2 = Integer.valueOf(duration)*1000; 
			step = 1000000/fps;
//			Log.d("Tag", "!!!!!!!1111111111111111111111"+duration);
//			Log.d("Tag", "!!!!!!!1111111111111111111111"+duration2);
//			Log.d("Tag", "!!!!!!!1111111111111111111111"+step);
			
			for(long time=0;time<duration2;time=time+step){
				Mat histogram = new Mat();
				bitmap = retriever.getFrameAtTime(time,MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
				Mat image_gray = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
				Utils.bitmapToMat(bitmap, image_gray);
				Imgproc.cvtColor(image_gray,image_gray,Imgproc.COLOR_BGR2RGB,3);
				java.util.List<Mat> matList = new LinkedList<Mat>();
	            matList.add(image_gray);	            
	            MatOfFloat ranges=new MatOfFloat(0f,256f);
	            boolean accumulate = false;
	            Log.d("Tag", "!!!!!!!!!!before  callllllll!!!!!!!!");
	            Imgproc.calcHist(
	                    matList, 
	                    new MatOfInt(0), 
	                    new Mat(), 
	                    histogram , 
	                    new MatOfInt(256), 
	                    ranges ,
	                    accumulate);
				histList.add(histogram);
				
				
	     
//		        
	     
		       
			}
			
			Log.d("Tag", "!!!!!!!!!!after  loop!!!!!!!!");
	        
	    
		
		int kf = 0;
		boolean flag = true;
		for(int i = 1;i < histList.size(); i ++){
			 
			 double compareResult=Imgproc.compareHist(histList.get(i-1), histList.get(i), Imgproc.HISTCMP_INTERSECT);
			 Log.d("Tag", "!!!!!!!compareHist:::::"+(i-1)+"--"+i+" : "+ compareResult);
			 if ((int)compareResult == 345600 && flag==true){
				 
				 keyFrame.add(i);
				 flag = false;
				 
			 }else if ((int)compareResult != 345600) {
				
				 flag = true;
				 
			}
			 
	        }
		for(int k=0;k<keyFrame.size();k++){
			Log.d("Tag", "!!!!!!!kkkkkkeyFrame------->:::::"+ keyFrame.get(k));
		}
		
		int keyFrameNum = 0;
		for(long time=0;time<duration2;time=time+step){
			
			if(keyFrame.contains(keyFrameNum)){
				bitmap = retriever.getFrameAtTime(time,MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
				saveImage(bitmap,(int)time);
			}
			
			keyFrameNum++;
		}
		
		
		new  AlertDialog.Builder(this).setTitle("Tips").setMessage("����ɣ�����").show();
	}

	



	private void saveImage(Bitmap bitmap,int time) {
		// TODO �Զ����ɵķ������
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	    		Environment.DIRECTORY_PICTURES), "Frames");
	    
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("MyCameraApp", "failed to create directory");
	           
	        }
	    }
	     File mediaFile;
		 mediaFile = new File(mediaStorageDir.getPath() + File.separator +
			        "IMG_"+ (int)time + ".jpg");
	 
		//д��mediaFile  
		try {
			FileOutputStream out = new FileOutputStream(mediaFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		     
	    
	    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mediaFile)));
	
   }
	
	
	protected void onResume() {  
        // TODO Auto-generated method stub  
        super.onResume();  
        //load OpenCV engine and init OpenCV library  ����OpenCV������
        Log.i("TAG", "Trying to load OpenCV library");
	    if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback))
	    {
	      Log.e("TAG", "Cannot connect to OpenCV Manager");
	    }
	    else{
	    	Log.e("TAG", "Succeed to connect to OpenCV Manager");
	    }
//      new Handler().postDelayed(new Runnable(){  
//  
//          @Override  
//          public void run() {  
//              // TODO Auto-generated method stub  
//              procSrc2Gray();  
//          }  
//            
//      }, 1000);  
          
    }
}
