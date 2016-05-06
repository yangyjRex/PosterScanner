#include <jni.h>
#include <opencv2/core/core.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/calib3d/calib3d.hpp>
#include <opencv2/stitching.hpp>

#include <vector>
#include <iostream>
#include <stdio.h>
#include <list>
#include<sstream>
#include<string>

using namespace std;
using namespace cv;

extern "C" {
//JNIEXPORT Mat JNICALL Java_org_opencv_samples_tutorial3_Sample3Native_FindFeatures(JNIEnv*, jobject, jlong addrGray, jlong addrRgba)

JNIEXPORT void JNICALL  Java_org_opencv_samples_tutorial3_Sample3Native_FindFeatures(
		JNIEnv*, jobject, jlong im1, jlong im2, jlong im3, jint no_images) {

	vector<Mat> imgs;
	bool try_use_gpu = false;
	// New testing
	Mat& temp1 = *((Mat*) im1);
	Mat& temp2 = *((Mat*) im2);
	Mat& pano = *((Mat*) im3);


	for (int k = 0; k < no_images; ++k) {
		string id;
		ostringstream convert;
		convert << k;
		id = convert.str();
		Mat img = imread("/storage/emulated/0/panoTmpImage/im" + id + ".jpeg");

		imgs.push_back(img);
	}

//	Mat result;
	Stitcher stitcher = Stitcher::createDefault(try_use_gpu);
	Stitcher::Status status = stitcher.stitch(imgs, pano);
//	imwrite(resultPath, result);

}

}

