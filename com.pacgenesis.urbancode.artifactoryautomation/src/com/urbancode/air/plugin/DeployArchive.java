package com.urbancode.air.plugin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class DeployArchive {
	
	public boolean putArchives(String artifactoryURI, String repository, String userName, String password, List<File> files) {				
		
//		Encoder encoder = Base64.getEncoder(); //commenting because this was introduced in 1.8
		boolean success = true;
		String enc = userName.trim()+":"+password.trim();
		final String BASEURI = artifactoryURI + "/" + repository;
		for(File file : files) {
			try {
				String encodedFileName = file.getName().replaceAll(" ", "%20");
				URI artifactoryPutURI = new URI(BASEURI+"/"+encodedFileName);				
				System.out.println("Artifactory URI: " + artifactoryPutURI);
				HttpURLConnection artifactoryConnection = (HttpURLConnection)artifactoryPutURI.toURL().openConnection();
				artifactoryConnection.setConnectTimeout(0);
				artifactoryConnection.setReadTimeout(0);
				artifactoryConnection.setChunkedStreamingMode(0);
				artifactoryConnection.setRequestMethod("PUT");
				artifactoryConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeBase64String(enc.getBytes()));
				artifactoryConnection.setRequestProperty("Content-Length", Long.toString(file.length()));
				artifactoryConnection.setUseCaches(false);
				artifactoryConnection.setDoInput(true);
				artifactoryConnection.setDoOutput(true);
				artifactoryConnection.connect();
				
				//Write file to Artifactory stream
				BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(artifactoryConnection.getOutputStream());
				FileInputStream fileInputStream = new FileInputStream(file);
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream, (int)file.length());
				byte[] fileArray = new byte[65536];
				int numRead;
				while((numRead = bufferedInputStream.read(fileArray)) != -1) {
					bufferedOutputStream.write(fileArray, 0, numRead);
				}
				bufferedInputStream.close();
				bufferedOutputStream.close();
				
				//Read response from Artifactory
				InputStreamReader inReader = new InputStreamReader(artifactoryConnection.getInputStream());
				BufferedReader reader = new BufferedReader(inReader);
				while(reader.ready()) {
					System.out.print(reader.readLine());
				}
			} catch (MalformedURLException e) {
				success = false;
				e.printStackTrace();
			} catch (ProtocolException e) {
				success = false;
				e.printStackTrace();
			} catch (IOException e) {
				success = false;
				e.printStackTrace();
			} catch (URISyntaxException e) {
				success = false;
				e.printStackTrace();
			} 
		}		
		return success;
	}
}
