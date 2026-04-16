package com.bshashi.gallerybackup;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class BotUploader {
	
	private Context context;
	private Activity activity;
	private String botToken;
	private String chatId;
	private int index, totalIndex;
	private OnUploadListener uploadListener;
	
	public interface OnUploadListener {
		void onSuccess(String fileId, String filePath);
		void onError(String error);
		void onProgress(int percent);
	}
	
	public BotUploader(Context ctx, Activity act) {
		this.context = ctx;
		this.activity = act;
	}
	
	public void setBotToken(String token) {
		this.botToken = token;
	}
	
	public void setChatId(String id) {
		this.chatId = id;
	}
	
	public void setUploadListener(OnUploadListener listener) {
		this.uploadListener = listener;
	}
	
	public void uploadPhoto(String pathOrUri) {
		new PhotoUploadTask().execute(pathOrUri);
	}
	
	public void photoIndex(int indexx, int total) {
		this.index = indexx;
		this.totalIndex = total;
	}
	
	private class PhotoUploadTask extends AsyncTask<String, Integer, String> {
		
		private String errorMsg = "";
		private String originalPath = "";
		
		@Override
		protected String doInBackground(String... params) {
			try {
				originalPath = params[0];
				
				InputStream inputStream = getInputStream(originalPath);
				if (inputStream == null) {
					errorMsg = "Unable to open file";
					return null;
				}
				
				return uploadToTelegram(inputStream, getFileName(originalPath));
				
			} catch (Exception e) {
				errorMsg = e.getMessage();
				return null;
			}
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			if (uploadListener != null) {
				uploadListener.onProgress(values[0]);
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				uploadListener.onSuccess(result, originalPath);
			} else {
				uploadListener.onError(errorMsg);
			}
		}
		
		private InputStream getInputStream(String input) {
			try {
				if (input.startsWith("content://")) {
					Uri uri = Uri.parse(input);
					return context.getContentResolver().openInputStream(uri);
				} else if (input.startsWith("file://")) {
					return new FileInputStream(new File(Uri.parse(input).getPath()));
				} else {
					return new FileInputStream(new File(input));
				}
			} catch (Exception e) {
				return null;
			}
		}
		
		private String getFileName(String path) {
			try {
				String filename = "IMG_" + index + "_of_" + totalIndex + "__" + Build.BRAND + getExtension(path);
				return (filename != null && !filename.isEmpty()) ? filename : new File(path).getName();
			} catch (Exception e) {
				return "IMG_" + System.currentTimeMillis()+ ".jpg";
			}
		}
		
		private String uploadToTelegram(InputStream inputStream, String fileName) throws Exception {
			
			String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
			String LINE_FEED = "\r\n";
			//URL url = new URL("https://api.telegram.org/bot" + botToken + "/sendDocument");
			URL url = new URL("https://api.telegram.org/bot" + botToken + "/sendPhoto");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			
			conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			
			OutputStream outputStream = conn.getOutputStream();
			DataOutputStream writer = new DataOutputStream(outputStream);
			
			writer.writeBytes("--" + boundary + LINE_FEED);
			writer.writeBytes("Content-Disposition: form-data; name=\"chat_id\"" + LINE_FEED);
			writer.writeBytes(LINE_FEED);
			writer.writeBytes(chatId + LINE_FEED);
			
			String mimeType = getMimeType(fileName);
			writer.writeBytes("--" + boundary + LINE_FEED);
			//writer.writeBytes("Content-Disposition: form-data; name=\"document\"; filename=\"" + fileName + "\"" + LINE_FEED);
			writer.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"" + fileName + "\"" + LINE_FEED);
			writer.writeBytes("Content-Type: " + mimeType + LINE_FEED);
			writer.writeBytes(LINE_FEED);
			
			byte[] buffer = new byte[4096];
			int bytesRead;
			long uploaded = 0;
			
			ByteArrayOutputStream temp = new ByteArrayOutputStream();
			int len;
			while ((len = inputStream.read(buffer)) != -1) {
				temp.write(buffer, 0, len);
			}
			
			byte[] fileBytes = temp.toByteArray();
			long total = fileBytes.length;
			
			ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
			
			while ((bytesRead = bis.read(buffer)) != -1) {
				writer.write(buffer, 0, bytesRead);
				uploaded += bytesRead;
				publishProgress((int) ((uploaded * 100) / total));
			}
			
			writer.writeBytes(LINE_FEED);
			writer.writeBytes("--" + boundary + "--" + LINE_FEED);
			
			writer.flush();
			writer.close();
			inputStream.close();
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == HttpURLConnection.HTTP_OK) {
				String response = readStream(conn.getInputStream());
				return parseFileUrl(response);
			} else {
				errorMsg = "HTTP Error: " + responseCode;
				return null;
			}
		}
		
		private String readStream(InputStream in) throws Exception {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
			return result.toString();
		}
		/*
		private String parseFileUrl(String jsonResponse) {
			try {
				JsonObject json = new Gson().fromJson(jsonResponse, JsonObject.class);
				
				if (json.get("ok").getAsBoolean()) {
					
					String fileId = json.getAsJsonObject("result")
					.getAsJsonObject("document")
					.get("file_id").getAsString();
					
					return getFileUrl(botToken, fileId);
				}
				
			} catch (Exception e) {
				errorMsg = "Parse error: " + e.getMessage();
			}
			return null;
		}
		*/
		private String parseFileUrl(String jsonResponse) {
			try {
				JsonObject json = new Gson().fromJson(jsonResponse, JsonObject.class);
				
				if (json.get("ok").getAsBoolean()) {
					
					com.google.gson.JsonArray photos = json.getAsJsonObject("result").getAsJsonArray("photo");
					
					int lastIndex = photos.size() - 1;
					
					String fileId = photos.get(lastIndex)
					.getAsJsonObject()
					.get("file_id").getAsString();
					
					return getFileUrl(botToken, fileId);
				}
				
			} catch (Exception e) {
				errorMsg = "Parse error: " + e.getMessage();
			}
			return null;
		}
		
		private String getFileUrl(String botToken, String fileId) {
			try {
				String apiUrl = "https://api.telegram.org/bot" + botToken + "/getFile?file_id=" + fileId;
				
				java.net.URL url = new java.net.URL(apiUrl);
				java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				
				java.io.BufferedReader reader = new java.io.BufferedReader(
				new java.io.InputStreamReader(conn.getInputStream())
				);
				
				StringBuilder response = new StringBuilder();
				String line;
				
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				
				reader.close();
				
				com.google.gson.JsonObject json = new com.google.gson.Gson().fromJson(response.toString(), com.google.gson.JsonObject.class);
				
				if (json.get("ok").getAsBoolean()) {
					String filePath = json.getAsJsonObject("result")
					.get("file_path").getAsString();
					
					return "https://api.telegram.org/file/bot" + botToken + "/" + filePath;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		private String getMimeType(String fileName) {
			String lower = fileName.toLowerCase();
			
			if (lower.endsWith(".png")) return "image/png";
			if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
			
			return "application/octet-stream";
		}
		
		private boolean isImageFile(String path) {
			String lower = path.toLowerCase();
			return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png");
		}
		
		private String getExtension(String path) {
			try {
				int i = path.lastIndexOf(".");
				if (i > 0) {
					return path.substring(i);
				}
			} catch (Exception ignored) {}
			return ".jpg";
		}
	}
}
