	package com.hs.yg.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.hs.yg.board.*;

@Component("fileUtils")
public class FileUtils {
	private static final String filePath = "C:\\mp\\file\\"; // ������ ����� ��ġ
	
	public List<Map<String, Object>> parseInsertFileInfo(Board vo, 
			MultipartHttpServletRequest mpRequest) throws Exception{
		
		/*
			Iterator�� �����͵��� ����ü? ���� �÷������κ��� ������ ���� �� �ִ� �������̽��Դϴ�.
			List�� �迭�� ���������� �������� ������ ����������, Map���� Ŭ�������� ���������� ������ ���� �����ϴ�.
			Iterator�� �̿��Ͽ� Map�� �ִ� �����͵��� while���� �̿��Ͽ� ���������� �����մϴ�.
		*/
		
		Iterator<String> iterator = mpRequest.getFileNames();
		
		MultipartFile multipartFile = null;
		String originalFileName = null;
		String originalFileExtension = null;
		String storedFileName = null;
		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> listMap = null;
		
		File file = new File(filePath);
		if(file.exists() == false) {
			file.mkdirs();	//��ο� ������ ���ٸ� ���� ����
		}
		
		while(iterator.hasNext()) {
			multipartFile = mpRequest.getFile(iterator.next()); // iterator�� ������ ���� ���, mpRequest���� ������ ���� �̸����� mpRequest.getFile�� �����´�.
			if(multipartFile.isEmpty() == false) { 
				originalFileName = multipartFile.getOriginalFilename(); // multipartfile�� �����Դٸ�, �������� �̸��� �����Ѵ�.
				originalFileExtension = originalFileName.substring(originalFileName.lastIndexOf(".")); //���� Ȯ���� �и�
				storedFileName = getRandomString() + originalFileExtension; //���� uuid�� Ȯ���ڸ� ����
				
				file = new File(filePath + storedFileName); // ��ο� �ش�Ǵ� ���� ���ο� ������ �����Ѵ�.
				multipartFile.transferTo(file); //������ ���� ��ο� ����
				listMap = new HashMap<String, Object>();
				listMap.put("ORG_FILE_NAME", originalFileName);	//������ ���� �̸��� ����
				listMap.put("STORED_FILE_NAME", storedFileName); //������ ���ο� �̸��� ����
				listMap.put("FILE_SIZE", multipartFile.getSize()); //������ ũ�⸦ ����(long)
				list.add(listMap);
			}
		}
		return list;
	}
	
	public static String getRandomString() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}