package com.hs.yg.board.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hs.yg.board.Board;
import com.hs.yg.board.Reply;
import com.hs.yg.board.dao.BoardDao;
import com.hs.yg.util.FileUtils;

/**
 * <pre>
 * BoardService
 * </pre>
 * 
 * @author wjddy
 */
@Service
public class BoardService {
	@Resource(name = "fileUtils")
	private FileUtils fileUtils;

	@Autowired
	BoardDao dao;

	/**
	 * <pre>
	 * �Խ��� �� ����� �������� �żҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @return ���Ǹ� ���� ������ �����͸� List<Board>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Board> getBoardList(Board vo) throws SQLException {
		return dao.getBoardList(vo);
	}

	/**
	 * <pre>
	 * ���� �����͸� �������� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @return ���Ǹ� ���� ������ �����͸� Board�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public Board getBoard(Board vo) throws SQLException {
		return dao.getBoard(vo);
	}

	/**
	 * <pre>
	 * getContent�� �����ε�
	 * </pre>
	 * 
	 * @param bno �Խ��� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� Board�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public Board getBoard(int bno) throws SQLException {
		return dao.getBoard(bno);
	}

	/**
	 * <pre>
	 * �۰� ������ ����ϴ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo        ��κ��� ������ Board Ÿ���� ������
	 * @param mpRequest ���� ������� ����ϱ� ���� MultipartHttpServletRequest Ÿ���� ��ü
	 * @return ���Ǹ� ���� ������ �����͵� �� �Խ��� ��ȣ�� ��ȯ��
	 * @throws Exception
	 */
	public int addBoard(Board vo, MultipartHttpServletRequest mpRequest) throws Exception {
		int bno = dao.addBoard(vo);
		List<Map<String, Object>> list = fileUtils.parseInsertFileInfo(bno, mpRequest);
		int size = list.size();
		for (int i = 0; i < size; i++) {
			dao.addFile(list.get(i));
		}
		return bno;
	}

	/**
	 * <pre>
	 * �۰� ���� ���� ����, ����� �����ϴ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @throws SQLException
	 */
	public void removeBoard(Board vo) throws SQLException {
		dao.removeBoard(vo);
	}

	/**
	 * <pre>
	 * ����� �Է��ϴ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Reply Ÿ���� ������
	 * @throws SQLException
	 */
	public void addReply(Reply vo) throws SQLException {
		dao.addReply(vo);
	}

	/**
	 * <pre>
	 * �ش� bno�� ��� ����� ��������  �޼ҵ�
	 * </pre>
	 * 
	 * @param bno �Խ��� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� List<Reply>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Reply> getReplyList(int bno) throws SQLException {
		return dao.getReplyList(bno);
	}

	/**
	 * <pre>
	 * �ش� ���ǿ� �ش�Ǵ� �� ����� �������� �޼ҵ�
	 * </pre>
	 * 
	 * @param searchOption �˻�����
	 * @param keyword      �˻���
	 * @param displayPost  ���� ����ϰ� �ִ� ������ �ѹ�
	 * @param pageNum      ǥ���� �������� ��
	 * @return ���Ǹ� ���� ������ �����͸� List<Board>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Board> getSearchBoardList(String searchOption, String keyword, int displayPost, int pageNum) throws SQLException {
		return dao.getSearchBoardList(searchOption, keyword, displayPost, pageNum);
	}

	/**
	 * <pre>
	 * �� ���� ������ �������� �޼ҵ�
	 * </pre>
	 * 
	 * @return ���� �� ������ ��ȯ��
	 * @throws SQLException
	 */
	public int getBoardCnt() throws SQLException {
		return dao.getBoardCnt();
	}

	/**
	 * <pre>
	 * �˻� ���ǰ� ����¡ ���ǿ� �ش�Ǵ� ���� ������ �������� �޼ҵ�
	 * </pre>
	 * 
	 * @param searchOption �˻� ����
	 * @param keyword      �˻���
	 * @return �˻� ���ǿ� �´� ���� �� ������ ��ȯ��
	 * @throws SQLException
	 */
	public int getSearchBoardListCount(String searchOption, String keyword) throws SQLException {
		return dao.getSearchBoardListCount(searchOption, keyword);
	}

	/**
	 * <pre>
	 * �ش� ����¡ ������ �� ����� �������� �޼ҵ�
	 * </pre>
	 * 
	 * @param displayPost ���� ����ϰ� �ִ� ������ �ѹ�
	 * @param pageNum     ǥ���� �������� ��
	 * @return ���Ǹ� ���� ������ �����͸� List<Board>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Board> getBoardList(int displayPost, int pageNum) throws SQLException {
		return dao.getBoardList(displayPost, pageNum);
	}

	/**
	 * <pre>
	 * �ش� bno�� ���� ����� �������� �޼ҵ�
	 * </pre>
	 * 
	 * @param bno �Խ��� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� List<Map<String, Object>>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getFileList(int bno) throws SQLException {
		return dao.getFileList(bno);
	}

	/**
	 * <pre>
	 * �ش� fno�� ������ �ٿ�ε��ϴ� �޼ҵ�
	 * </pre>
	 * 
	 * @param fno ���� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� Map<String, Object>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public Map<String, Object> getFile(int fno) throws SQLException {
		return dao.getFile(fno);
	}

	/**
	 * <pre>
	 * �ۼ���, ����, ������ ����� ���� ���� �� ���� ���ε带 ������Ʈ�ϴ� �ޙ�,
	 * </pre>
	 * 
	 * @param vo        ��κ��� ������ Board Ÿ���� ������
	 * @param files     ��κ��� ������ String �迭 Ÿ���� ���� ��ȣ
	 * @param fileNames ��κ��� ������ String �迭 Ÿ���� ���� ��ȣ
	 * @param mpRequest ���� ������� ����ϱ� ���� MultipartHttpServletRequest Ÿ���� ��ü
	 * @throws Exception
	 */
	public void modifyBoard(Board vo, String[] files, String[] fileNames, MultipartHttpServletRequest mpRequest) throws Exception {
		dao.modifyBoard(vo);
		List<Map<String, Object>> list = fileUtils.parseUpdateFileInfo(vo, files, fileNames, mpRequest);
		Map<String, Object> tempMap = null;
		int size = list.size();
		for (int i = 0; i < size; i++) {
			tempMap = list.get(i);
			if (tempMap.get("IS_NEW").equals("Y")) {
				dao.addFile(tempMap);
			} else {
				dao.modifyFile(tempMap);
			}
		}
		dao.removeFile();
	}
}
