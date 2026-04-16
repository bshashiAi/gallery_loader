package com.bshashi.gallerybackup;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class GalleryLoader {
	
	private static final String TAG = "GalleryLoader";
	
	public static final int SORT_DATE_NEWEST = 0;
	public static final int SORT_DATE_OLDEST = 1;
	public static final int SORT_NAME_ASC = 2;
	public static final int SORT_NAME_DESC = 3;
	public static final int SORT_SIZE_LARGE = 4;
	public static final int SORT_SIZE_SMALL = 5;
	
	public static final String FOLDER_CAMERA = "Camera";
	public static final String FOLDER_SCREENSHOTS = "Screenshots";
	public static final String FOLDER_WHATSAPP = "WhatsApp Images";
	public static final String FOLDER_DOWNLOAD = "Download";
	public static final String FOLDER_DCIM = "DCIM";
	public static final String FOLDER_PICTURES = "Pictures";
	
	public static List<String> getGalleryPaths(Context context, int limit, String folderName, int sortOrder, boolean ignored) {
		
		List<String> result = new ArrayList<>();
		
		String[] projection = {
			MediaStore.Images.Media._ID
		};
		
		String selection = null;
		String[] selectionArgs = null;
		
		if (folderName != null && !folderName.isEmpty()) {
			selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " LIKE ?";
			selectionArgs = new String[]{"%" + folderName + "%"};
		}
		
		String orderBy = buildSortOrder(sortOrder);
		
		Cursor cursor = null;
		
		try {
			cursor = context.getContentResolver().query(
			MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			projection,
			selection,
			selectionArgs,
			orderBy
			);
			
			if (cursor != null) {
				
				int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
				
				int count = 0;
				
				while (cursor.moveToNext()) {
					
					long id = cursor.getLong(idColumn);
					
					Uri contentUri = ContentUris.withAppendedId(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
					
					result.add(contentUri.toString());
					
					count++;
					
					if (limit > 0 && count >= limit) break;
				}
			}
			
		} catch (Exception e) {
			Log.e(TAG, "Error loading gallery", e);
		} finally {
			if (cursor != null) cursor.close();
		}
		
		Log.d(TAG, "Loaded " + result.size() + " images");
		return result;
	}
	
	private static String buildSortOrder(int sortType) {
		
		switch (sortType) {
			
			case SORT_DATE_OLDEST:
			return MediaStore.Images.Media.DATE_ADDED + " ASC";
			
			case SORT_NAME_ASC:
			return MediaStore.Images.Media.DISPLAY_NAME + " ASC";
			
			case SORT_NAME_DESC:
			return MediaStore.Images.Media.DISPLAY_NAME + " DESC";
			
			case SORT_SIZE_LARGE:
			return MediaStore.Images.Media.SIZE + " DESC";
			
			case SORT_SIZE_SMALL:
			return MediaStore.Images.Media.SIZE + " ASC";
			
			case SORT_DATE_NEWEST:
			default:
			return MediaStore.Images.Media.DATE_ADDED + " DESC";
		}
	}
}
