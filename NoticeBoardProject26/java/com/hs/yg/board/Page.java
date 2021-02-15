package com.hs.yg.board;

/**
 * ����¡ ���� ��
 * 
 * @author �����
 */
public class Page {
	final private int POSTNUM = 20;
	final private int PAGENUMCNT = 5;
	private int num;
	private int count;
	private int startPageNum;
	private int endPageNum;
	private int end;

	private int displayPost;
	private String searchTypeKeyword;
	private boolean prev;
	private boolean next;
	private boolean fprev;
	private boolean enext;

	public void setNum(int num) {
		this.num = num;
	}

	/**
	 * <pre>
	 * �������� ����ϴ� Method
	 * </pre>
	 * 
	 * @param count �Խñ��� �� ������ ����
	 */
	public void setCount(int count) {
		this.count = count;
		if (count > 0) {
			if (count % 10 == 0) {
				end = count / POSTNUM;
			} else {
				end = (count / POSTNUM) + 1;
			}
		}
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

	/**
	 * <prev> ������ ��ȣ�� ���� ����¡ ��ư�� �Ի��ϴ� �޼ҵ� </prev>
	 */
	private void dataCalc() {
		endPageNum = (int) (Math.ceil((double) num / (double) PAGENUMCNT) * PAGENUMCNT);
		startPageNum = endPageNum - (PAGENUMCNT - 1);
		int endPageNum2 = (int) (Math.ceil((double) count / (double) POSTNUM));
		if (endPageNum > endPageNum2) {
			endPageNum = endPageNum2;
		}
		prev = startPageNum == 1 ? false : true;
		next = endPageNum * POSTNUM >= count ? false : true;
		enext = num < end ? true : false;
		fprev = num > 1 ? true : false;
		displayPost = (num - 1) * POSTNUM;
	}

	/**
	 * <prev> searchOption�� keyword�� ���ڿ��� �����ִ� �޼ҵ� </prev>
	 * 
	 * @param searchOption �˻� ������ ����
	 * @param keyword      �˻�� ����
	 */
	public void setSearchTypeKeyword(String searchOption, String keyword) {
		if (searchOption.equals("") || keyword.equals("")) {
			searchTypeKeyword = "";
		} else {
			searchTypeKeyword = "&searchOption=" + searchOption + "&keyword=" + keyword;
		}
	}

	public String getSearchTypeKeyword() {
		return searchTypeKeyword;
	}

	public int getEnd() {
		return end;
	}

	public boolean isFprev() {
		return fprev;
	}

	public boolean isEnext() {
		return enext;
	}
}
