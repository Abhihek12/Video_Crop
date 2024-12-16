package com.VideoCrop.VideoCrop.Service;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.VideoCrop.VideoCrop.IService.IVideoService;

@Service
public class VideoService implements IVideoService {

	private static final String INPUT_FOLDER = "D:/Image";
	private static final String OUTPUT_FOLDER = "D:/Image/CropVideo";

	@Override
	public String processVideos(MultipartFile[] videos, int length, int width) throws Exception {

		for (MultipartFile video : videos) {
			String inputPath = getPath(video);

			Dimension videoDimension = getVideoDimension(inputPath);

			if (length > videoDimension.getHeight() || width > videoDimension.getWidth()) {
				throw new IllegalArgumentException("Crop dimensions cannot exceed original video dimensions: " + video.getOriginalFilename());
			}

			String outputPath = OUTPUT_FOLDER + "/" + length + "X" + width + "_" + video.getOriginalFilename();
			cropAndResizeVideo(inputPath, outputPath, length, width);

		}
		return "Video Crop Succesfully!!";
	}

	@Override
	public String getPath(MultipartFile video) throws Exception {
		String path = INPUT_FOLDER + "/" + video.getOriginalFilename();
		return path;
	}

	@Override
	public Dimension getVideoDimension(String videoPath) throws Exception {
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath);
		grabber.start();
		int width = grabber.getImageWidth();
		int height = grabber.getImageHeight();
		grabber.stop();
		return new Dimension(width, height);
	}

	public void cropAndResizeVideo(String inputPath, String outputPath, int targetWidth, int targetHeight)
			throws IOException, InterruptedException {
		FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputPath);
		grabber.start();

		int originalWidth = grabber.getImageWidth();
		int originalHeight = grabber.getImageHeight();

		FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputPath, targetWidth, targetHeight);
		recorder.setVideoCodec(grabber.getVideoCodec());
		recorder.setFormat("mp4");
		recorder.setFrameRate(grabber.getFrameRate());
		recorder.start();

		OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();

		Frame frame;
		while ((frame = grabber.grabFrame()) != null) {
			if (frame.image != null) {
				Mat mat = converter.convert(frame);

				int xOffset = (originalWidth - targetWidth) / 2;
				int yOffset = (originalHeight - targetHeight) / 2;
				Rect cropRegion = new Rect(xOffset, yOffset, targetWidth, targetHeight);

				Mat croppedMat = new Mat(mat, cropRegion);

				Frame croppedFrame = converter.convert(croppedMat);

				recorder.record(croppedFrame);

				croppedMat.release();
			}
		}

		grabber.stop();
		recorder.stop();
		recorder.release();
	}

}
