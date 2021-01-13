package com.hs.yg.board;

import org.springframework.web.multipart.MultipartFile;

public class Board {
	private int bno;	//���� ��ȣ	
	private String title;	//���� ����
	private String name;	//���� �̸�
	private String passwd;	//���� ��й�ȣ (null ���)
	private String content; //���� ����
	private int commentCount; //�ۿ� �޸� ����� ��
	private int viewCount; //�ۿ� �޸� ����� ��
	private String fileName; //���� ����½�_ ������ �̸�
	private MultipartFile uploadFile; //���� ����½�_���� ��ü
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public int getBno() {
		return bno;
	}
	public void setBno(int bno) {
		this.bno = bno;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public MultipartFile getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(MultipartFile uploadFile) {
		this.uploadFile = uploadFile;
	}
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
}
