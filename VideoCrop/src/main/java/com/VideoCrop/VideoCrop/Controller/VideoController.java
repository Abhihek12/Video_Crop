package com.VideoCrop.VideoCrop.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.VideoCrop.VideoCrop.Service.VideoService;

@Controller
@RequestMapping("/api/videos")
public class VideoController {

	  @Autowired
	  private VideoService videoService;
	  
	  
	  @PostMapping("/upload/crop")
	    public ResponseEntity<?> uploadAndCropVideos(
	            @RequestParam("videos") MultipartFile[] videos,
	            @RequestParam("length") int length,
	            @RequestParam("width") int width) {
	        try {
	            String croppedFiles = videoService.processVideos(videos, length, width);
	            return ResponseEntity.ok(croppedFiles);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.badRequest().body(e.getMessage());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing videos.");
	        }
	    }
}
