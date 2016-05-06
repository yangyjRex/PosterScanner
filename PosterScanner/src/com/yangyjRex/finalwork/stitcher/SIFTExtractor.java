package com.yangyjRex.finalwork.stitcher;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import android.R.integer;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class SIFTExtractor {
	

	
	    public SIFTExtractor(){
	    	
	    }
		
		
		
		
		public static MatOfDMatch matchSIFT(Bitmap bitmap1,Bitmap bitmap2){
		
		int height = bitmap2.getHeight();
		int width = bitmap2.getWidth();
		Log.i("TAG", "1111111111111111111111");
		Mat img1 = new Mat(height, width, CvType.CV_16UC3);
		Mat img2 = new Mat(height, width, CvType.CV_16UC3);
		Log.i("TAG", "qqqqqqqqqqqqqq");
		Utils.bitmapToMat(bitmap1, img1);
		Utils.bitmapToMat(bitmap2, img2);
		Log.i("TAG", "2222222222222222222222");
		
		FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
		MatOfKeyPoint keypoints1 = null;
		MatOfKeyPoint keypoints2 = null;
		
		Log.i("TAG", "通道数："+img1.channels());
    	
		featureDetector.detect(img1, keypoints1);
		featureDetector.detect(img2, keypoints2);
		
		Log.i("TAG", "通道数："+img2.channels());
    	
		
		
		DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
		Mat descriptors1 = null;
		Mat descriptors2 = null;
		descriptorExtractor.compute(img1, keypoints1, descriptors1);
		descriptorExtractor.compute(img2, keypoints2, descriptors2);
		
		DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
		MatOfDMatch matches = null;
		descriptorMatcher.match(descriptors1, descriptors2, matches);
		
		System.out.print("!!!!!!!!!!!!!success!!!");
		return matches;
		
		}

 }
