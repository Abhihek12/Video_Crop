package com.VideoCrop.VideoCrop.IService;

import java.awt.Dimension;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface IVideoService {
	
	 public String processVideos(MultipartFile[] videos, int length, int width) throws Exception;
	 public String getPath(MultipartFile video)  throws Exception ;
	 public Dimension getVideoDimension(String videoPath) throws Exception;
	 public void cropAndResizeVideo(String inputPath, String outputPath, int length, int width) throws IOException, InterruptedException;
}
