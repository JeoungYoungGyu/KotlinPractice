	package com.hs.yg.board;

public class Page {
	final private int POSTNUM = 10; // �� ������ ��ȣ�� ǥ���� �������� ����
	final private int PAGENUMCNT = 5; // ǥ���� ������ ��ȣ ���� 
	private int num; // JSP���� ������ �������� ��ȣ
	private int count; // �� �������� ����
	private int startPageNum; // ǥ���ϴ� ������ ��ȣ�� ó��
	private int endPageNum; // ǥ���ϴ� ������ ��ȣ�� ��
	
	private int displayPost; // sql�� ���, ���� ���� �ִ� ���������� -10
	private String searchTypeKeyword; // &searchOption=searchOption&keyword=keyword Ÿ������ ���ڿ� ��ȯ

	private boolean prev;
	private boolean next;
	
	public void setNum(int num) {
		 this.num = num;
	}

	public void setCount(int count) {
		 this.count = count;
		 dataCalc();
	}

	public int getCount() {
		 return count;
	}

	public int getPostNum() {
		 return POSTNUM;
	}

	public int getDisplayPost() {
		 return displayPost;
	}

	public int getPageNumCnt() {
		 return POSTNUM;
	}

	public int getEndPageNum() {
		 return endPageNum;
	}

	public int getStartPageNum() {
		 return startPageNum;
	}

	public boolean getPrev() {
		 return prev;
	} 

	public boolean getNext() {
		 return next;
	}
		
	private void dataCalc() {
		 endPageNum = (int)(Math.ceil((double)num / (double)PAGENUMCNT) * PAGENUMCNT); // ceil(���� ������ �� / ǥ���� ������ ��ȣ ����)  * ǥ���� ������ ��ȣ ���� 
		 startPageNum = endPageNum - (PAGENUMCNT - 1); // ǥ���ϴ� ������ ��ȣ�� ��  - (ǥ���� ������ ��ȣ ���� - 1), endPageNum2�� ����鼭 ���� �ϴ� ������ statPageNum�� ��Ȯ�ϰ� �ֱ� ���� ���
		 
		 int endPageNum2 = (int)(Math.ceil((double)count / (double)POSTNUM)); // �ݿø�(�� ������ ����) / �� ������ ��ȣ�� ǥ���� �������� ����
		 if(endPageNum > endPageNum2) { // 59��° ���� ��������, �ʿ� �̻��� page ������ ���� �� �ִٴ� ��, �׷��Ƿ� 61��° ���� 
			 endPageNum = endPageNum2;
		 }
		 prev = startPageNum == 1 ? false : true;	// num�� 1�� ��� prev�� false ó���Ǿ�, ��Ÿ���� ����
		 next = endPageNum * POSTNUM >= count ? false : true; // endPageNum * 10�� �� �Խñ� ������ ũ�ų� ���� ��� next�� ������ ����
		 displayPost = (num - 1) * POSTNUM;
	}
	
	public void setSearchTypeKeyword(String searchOption, String keyword) {
		if(searchOption.equals("") || keyword.equals("")) {
			searchTypeKeyword = ""; 
		} else {
			searchTypeKeyword = "&searchOption=" + searchOption + "&keyword=" + keyword; 
		}  
	}

	public String getSearchTypeKeyword() {
		return searchTypeKeyword;
	}
}
