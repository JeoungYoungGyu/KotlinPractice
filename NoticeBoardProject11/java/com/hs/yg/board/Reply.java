package com.hs.yg.board;

public class Reply {
	private int rbno; // Board�� bno, �ܷ�Ű���� �⺻Ű
	private int rrno; // ��� ��ȣ
	private String rcontent; // ����� ����
	private String rwriter; // ��� �ۼ���
	public int getRbno() {
		return rbno;
	}
	public void setRbno(int rbno) {
		this.rbno = rbno;
	}
	public int getRrno() {
		return rrno;
	}
	public void setRrno(int rrno) {
		this.rrno = rrno;
	}
	public String getRcontent() {
		return rcontent;
	}
	public void setRcontent(String rcontent) {
		this.rcontent = rcontent;
	}
	public String getRwriter() {
		return rwriter;
	}
	public void setRwriter(String rwriter) {
		this.rwriter = rwriter;
	}

}
