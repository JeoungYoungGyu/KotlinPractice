package com.hs.yg.board.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.hs.yg.board.Board;
import com.hs.yg.board.DataBase;
import com.hs.yg.board.Reply;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * BoardDao ����
 * </pre>
 * 
 * @author �����
 */
@Repository
public class BoardDao {

	// DB�� �����ϱ� ���� ����
	@Autowired
	private DataBase dataBase;

	// ������ �ʿ��� ������
	private Connection conn;
	private PreparedStatement pstmt = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	// insertBoard�� insertFile�� ���õ� ������
	final private String sqlI = "INSERT INTO tb_board(title, content, name, register_date, modify_date) values (?,?,?, NOW(), NOW())";
	final private String sqlIP = "INSERT INTO tb_board(title, content, name, password, register_date, modify_date) values (?,?,?,SHA2(?, 256), NOW(), NOW())";
	final private String sqlIF = "INSERT INTO tb_board_file(board_id, org_filename, new_filename, register_date, modify_date, file_size) VALUES (?, ?, ?, now(), now(), ?)";

	// insertReply�� ���õ� ������
	final private String sqlRI = "INSERT INTO tb_board_reply(board_id, content, writer, register_date) VALUES (?, ?, ?, now())";
	final private String sqlRU = "UPDATE tb_board SET reply_count = (SELECT COUNT(*) FROM tb_board_reply WHERE board_id = ?) WHERE id = ?";

	// deleteBoard�� ���õ� ������
	final private String sqlD = "DELETE FROM tb_board WHERE id=?";
	final private String sqlDR = "DELETE FROM tb_board_reply WHERE board_id=?";
	final private String sqlDF = "DELETE from tb_board_file WHERE board_id=?";

	// getContent�� ���õ� ������
	final private String sqlGCU = "UPDATE tb_board SET view_count = (SELECT SUM(view_count) FROM tb_board WHERE id = ?) + 1 WHERE id = ?";
	final private String sqlGC = "SELECT* FROM tb_board where id=?";

	// getBoardCnt�� ���õ� Ŀ����
	final private String sqlCnt = "select count(*) as cnt from tb_board";

	// getSearchedBoardCnt�� ���õ� ������
	final private String sqlSCntT = "select count(*) as cnt from tb_board where title like ?";
	final private String sqlSCntC = "select count(*) as cnt from tb_board where content like ?";
	final private String sqlSCntTC = "select count(*) as cnt from tb_board where title like ? or content like ?";

	// getBoardList�� ���õ� ������
	final private String sqlGBL = "SELECT* FROM tb_board";

	// getListPage�� ���õ� ������
	// final private String sqlP = "select id, title, content, name, password,
	// reply_count, view_count, register_date from tb_board order by id desc LIMIT
	// ?, ?";
	final private String sqlP = "SELECT t.* FROM(SELECT t.*, @rn := @rn + 1 AS rn FROM tb_board t, (SELECT @rn := 0) r) t ORDER BY rn DESC LIMIT ?, ?";
	final private String sqlGB = "select id from tb_board order by id desc LIMIT 0, 1";

	// getSearchedBoardList�� ���õ� ������
	final private String sqlST = "SELECT t.* FROM(SELECT t.*, @rn := @rn + 1 AS rn FROM tb_board t, (SELECT @rn := 0) r) t where title LIKE ? ORDER BY rn DESC LIMIT ?, ?";
	final private String sqlSC = "SELECT t.* FROM(SELECT t.*, @rn := @rn + 1 AS rn FROM tb_board t, (SELECT @rn := 0) r) t where content LIKE ? ORDER BY rn DESC LIMIT ?, ?";
	final private String sqlSTC = "SELECT t.* FROM(SELECT t.*, @rn := @rn + 1 AS rn FROM tb_board t, (SELECT @rn := 0) r) t where title like ? or content like ? ORDER BY rn DESC LIMIT ?, ?";

	// getReplyList�� ���õ� ������
	final private String sqlRGRL = "SELECT * FROM tb_board_reply where board_id=?";

	// selectFileList�� ���õ� ������
	final private String sqlgfl = "SELECT file_id, org_filename, round(file_size/1024, 3) as file_size FROM tb_board_file where board_id=? and delete_yn = 'N' ORDER BY file_id ASC";
	final private String sqlgf = "select org_filename, new_filename FROM tb_board_file where file_id=?";

	// updateBoard�� ���õ� ������
	final private String sqlU = "UPDATE tb_board SET title = ?, content = ?, name = ?, modify_date = NOW() WHERE id = ?";
	final private String sqlUF = "UPDATE tb_board_file set delete_yn = 'Y', modify_date = now() WHERE file_id = ?";
	final private String sqlUDF = "DELETE FROM tb_board_file WHERE delete_yn='Y'";

	/**
	 * <pre>
	 * JDBC�� �����ϴ� �޼ҵ�
	 * </pre>
	 */
	public void connect() {
		try {
			Class.forName(dataBase.getDriver());
			conn = DriverManager.getConnection(dataBase.getUrl(), dataBase.getUserid(), dataBase.getUserpw());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * JDBC ������ �����ϴ� �޼ҵ�
	 * </pre>
	 */
	public void disconnect() {
		try {
			if (pstmt != null) {
				pstmt.close();
			}
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * �Խñ��� �����ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @return ���Ǹ� ���� ������ �����͵� �� �Խ��� ��ȣ�� ��ȯ��
	 * @throws SQLException
	 */
	public int addBoard(Board vo) throws SQLException {
		int bno = 0;
		try {
			connect();
			if (vo.getPasswd().contentEquals("")) {
				pstmt = conn.prepareStatement(sqlI);
				pstmt.setString(1, new String(vo.getTitle().getBytes("8859_1"), "UTF-8"));
				pstmt.setString(2, new String(vo.getContent().getBytes("8859_1"), "UTF-8"));
				pstmt.setString(3, new String(vo.getName().getBytes("8859_1"), "UTF-8"));
				pstmt.executeUpdate();
			} else {
				pstmt = conn.prepareStatement(sqlIP);
				pstmt.setString(1, new String(vo.getTitle().getBytes("8859_1"), "UTF-8"));
				pstmt.setString(2, new String(vo.getContent().getBytes("8859_1"), "UTF-8"));
				pstmt.setString(3, new String(vo.getName().getBytes("8859_1"), "UTF-8"));
				pstmt.setString(4, new String(vo.getPasswd().getBytes("8859_1"), "UTF-8"));
				pstmt.executeUpdate();
			}
			pstmt = conn.prepareStatement(sqlGB);
			rs = pstmt.executeQuery();
			if (rs.next())
				bno = rs.getInt("id");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return bno;
	}

	/**
	 * <pre>
	 * �Խñ��� ������ �����ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param map ���� ������ ���� Map<String, Object>�� ������ ������
	 * @param bno �Խ��� ��ȣ
	 * @throws Exception
	 */
	public void addFile(Map<String, Object> map) throws Exception {
		try {
			connect();
			pstmt = conn.prepareStatement(sqlIF);
			pstmt.setInt(1, (Integer) map.get("BNO"));
			pstmt.setString(2, new String(map.get("ORG_FILE_NAME").toString().getBytes("8859_1"), "UTF-8"));
			pstmt.setString(2, map.get("ORG_FILE_NAME").toString());
			pstmt.setString(3, map.get("STORED_FILE_NAME").toString());
			pstmt.setDouble(4, (Long) map.get("FILE_SIZE"));
			pstmt.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	/**
	 * <pre>
	 * �Խñ��� �����ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @throws SQLException
	 */
	public void removeBoard(Board vo) throws SQLException {
		try {
			connect();
			pstmt = conn.prepareStatement(sqlDR);
			pstmt.setInt(1, vo.getBno());
			pstmt.executeUpdate();
			pstmt = conn.prepareStatement(sqlDF);
			pstmt.setInt(1, vo.getBno());
			pstmt.executeUpdate();
			pstmt = conn.prepareStatement(sqlD);
			pstmt.setInt(1, vo.getBno());
			pstmt.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	/**
	 * <pre>
	 *  �Խñ��� �������� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @return ���Ǹ� ���� ������ �����͸� Board�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public Board getBoard(Board vo) throws SQLException {
		Board board = null;
		try {
			connect();
			pstmt = conn.prepareStatement(sqlGCU);
			pstmt.setInt(1, vo.getBno());
			pstmt.setInt(2, vo.getBno());
			rs = pstmt.executeQuery();
			pstmt = conn.prepareStatement(sqlGC);
			pstmt.setInt(1, vo.getBno());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				board = new Board();
				board.setBno(rs.getInt("id"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setName(rs.getString("name"));
				board.setPasswd(rs.getString("password"));
				board.setCommentCount(rs.getInt("reply_count"));
				board.setModifyDate(rs.getString("modify_date").substring(0, 19));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return board;
	}

	/**
	 * <pre>
	 *  getContent�� �����ε�
	 * </pre>
	 * 
	 * @param bno �Խ��� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� Board�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public Board getBoard(int bno) throws SQLException {
		Board board = null;
		try {
			connect();
			pstmt = conn.prepareStatement(sqlGCU);
			pstmt.setInt(1, bno);
			pstmt.setInt(2, bno);
			rs = pstmt.executeQuery();

			pstmt = conn.prepareStatement(sqlGC);
			pstmt.setInt(1, bno);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				board = new Board();
				board.setBno(rs.getInt("id"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setName(rs.getString("name"));
				board.setPasswd(rs.getString("password"));
				board.setCommentCount(rs.getInt("reply_count"));
				board.setModifyDate(rs.getString("modify_date").substring(0, 19));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return board;
	}

	/**
	 * <pre>
	 * ����� �Է��ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Reply Ÿ���� ������
	 * @throws SQLException
	 */
	public void addReply(Reply vo) throws SQLException {
		try {
			connect();
			pstmt = conn.prepareStatement(sqlRI);
			pstmt.setInt(1, vo.getRbno());
			pstmt.setString(2, vo.getRcontent());
			pstmt.setString(3, vo.getRwriter());
			pstmt.executeQuery();

			pstmt = conn.prepareStatement(sqlRU);
			pstmt.setInt(1, vo.getRbno());
			pstmt.setInt(2, vo.getRbno());
			pstmt.executeQuery();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	/**
	 * <pre>
	 * ��� ����� �ҷ����� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param bno �Խ��� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� List<Reply>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Reply> getReplyList(int bno) throws SQLException {
		List<Reply> replyList = new ArrayList<Reply>();
		Reply reply = null;
		try {
			connect();
			pstmt = conn.prepareStatement(sqlRGRL);
			pstmt.setInt(1, bno);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				reply = new Reply();
				reply.setRbno(rs.getInt("board_id"));
				reply.setRrno(rs.getInt("reply_id"));
				reply.setRcontent(rs.getString("content"));
				reply.setRwriter(rs.getString("writer"));
				reply.setrRegDate(rs.getString("register_date").substring(0, 19));
				replyList.add(reply);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return replyList;
	}

	/**
	 * <pre>
	 * ó�� �Խñ� ����� �ҷ����� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @return ���Ǹ� ���� ������ �����͸� List<Board>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Board> getBoardList(Board vo) throws SQLException {
		List<Board> boardList = new ArrayList<Board>();
		Board board = null;
		try {
			connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlGBL);
			while (rs.next()) {
				board = new Board();
				board.setBno(rs.getInt("id"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setName(rs.getString("name"));
				board.setPasswd(rs.getString("password"));
				board.setCommentCount(rs.getInt("reply_count"));
				board.setViewCount(rs.getInt("view_count"));
				board.setRegisterDate(rs.getString("register_date").substring(0, 19));
				boardList.add(board);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return boardList;
	}

	/**
	 * <pre>
	 * �˻��� �� ����� �ҷ����� ���� �޼ҵ�
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
		List<Board> boardList = new ArrayList<Board>();
		String akeyword = "%" + keyword + "%";
		Board board = null;
		try {
			connect();
			if (searchOption.contentEquals("titleCon")) { // ���ǿ� ���� sql���� �ٸ���.
				pstmt = conn.prepareStatement(sqlSTC);
				pstmt.setString(1, akeyword);
				pstmt.setString(2, akeyword);
				pstmt.setInt(3, displayPost);
				pstmt.setInt(4, pageNum);
			} else if (searchOption.contentEquals("title")) {
				pstmt = conn.prepareStatement(sqlST);
				pstmt.setString(1, akeyword);
				pstmt.setInt(2, displayPost);
				pstmt.setInt(3, pageNum);
			} else {
				pstmt = conn.prepareStatement(sqlSC);
				pstmt.setString(1, akeyword);
				pstmt.setInt(2, displayPost);
				pstmt.setInt(3, pageNum);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				board = new Board();
				board.setBoardNum(rs.getInt("rn"));
				board.setBno(rs.getInt("id"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setName(rs.getString("name"));
				board.setPasswd(rs.getString("password"));
				board.setCommentCount(rs.getInt("reply_count"));
				board.setViewCount(rs.getInt("view_count"));
				board.setRegisterDate(rs.getString("register_date").substring(0, 16));
				boardList.add(board);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return boardList;
	}

	/**
	 * <pre>
	 * �Խñ��� �� ������ �������� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @return ���� �� ������ ��ȯ��
	 * @throws SQLException
	 */
	public int getBoardCnt() throws SQLException {
		int count = 0;
		try {
			connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlCnt);
			if (rs.next())
				count = rs.getInt("cnt");

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return count;
	}

	/**
	 * <pre>
	 * �˻��� �Խñ��� �� ������ �������� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param searchOption �˻� ����
	 * @param keyword      �˻���
	 * @return �˻� ���ǿ� �´� ���� �� ������ ��ȯ��
	 * @throws SQLException
	 */
	public int getSearchBoardListCount(String searchOption, String keyword) throws SQLException {
		int count = 0;
		String akeyword = "%" + keyword + "%";
		try {
			connect();
			if (searchOption.contentEquals("titleCon")) {
				pstmt = conn.prepareStatement(sqlSCntTC);
				pstmt.setString(1, akeyword);
				pstmt.setString(2, akeyword);
			} else if (searchOption.contentEquals("title")) {
				pstmt = conn.prepareStatement(sqlSCntT);
				pstmt.setString(1, akeyword);
			} else {
				pstmt = conn.prepareStatement(sqlSCntC);
				pstmt.setString(1, akeyword);
			}
			rs = pstmt.executeQuery();
			if (rs.next())
				count = rs.getInt("cnt");
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return count;
	}

	/**
	 * <pre>
	 * �Խñ� ����� �������� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param displayPost ���� ����ϰ� �ִ� ������ �ѹ�
	 * @param pageNum     ǥ���� �������� ��
	 * @return ���Ǹ� ���� ������ �����͸� List<Board>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Board> getBoardList(int displayPost, int postNum) throws SQLException {
		List<Board> boardList = new ArrayList<Board>();
		Board board = null;
		try {
			connect();
			pstmt = conn.prepareStatement(sqlP);
			pstmt.setInt(1, displayPost);
			pstmt.setInt(2, postNum);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				board = new Board();
				board.setBoardNum(rs.getInt("rn"));
				board.setBno(rs.getInt("id"));
				board.setTitle(rs.getString("title"));
				board.setContent(rs.getString("content"));
				board.setName(rs.getString("name"));
				board.setPasswd(rs.getString("password"));
				board.setCommentCount(rs.getInt("reply_count"));
				board.setViewCount(rs.getInt("view_count"));
				board.setRegisterDate(rs.getString("register_date").substring(0, 16));
				boardList.add(board);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return boardList;
	}

	/**
	 * <pre>
	 * ���� ����� �������� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param bno �Խ��� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� List<Map<String, Object>>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getFileList(int bno) throws SQLException {
		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();
		Map<String, Object> file;
		try {
			connect();
			pstmt = conn.prepareStatement(sqlgfl);
			pstmt.setInt(1, bno);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				file = new HashMap<String, Object>();
				file.put("file_id", rs.getInt("file_id"));
				file.put("org_filename", rs.getString("org_filename"));
				file.put("file_size", rs.getDouble("file_size"));
				fileList.add(file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return fileList;
	}

	/**
	 * <pre>
	 * ������ �ٿ�ε��ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param fno ���� ��ȣ
	 * @return ���Ǹ� ���� ������ �����͸� Map<String, Object>�� ���·� ��ȯ��
	 * @throws SQLException
	 */
	public Map<String, Object> getFile(int fno) throws SQLException {
		Map<String, Object> file = new HashMap<String, Object>();
		try {
			connect();
			pstmt = conn.prepareStatement(sqlgf);
			pstmt.setInt(1, fno);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				file.put("org_filename", rs.getString("org_filename"));
				file.put("new_filename", rs.getString("new_filename"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
		return file;
	}

	/**
	 * <pre>
	 * �Խñ��� �����ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @throws SQLException
	 */
	public void modifyBoard(Board vo) throws SQLException {
		try {
			connect();
			pstmt = conn.prepareStatement(sqlU);
			pstmt.setString(1, new String(vo.getTitle().getBytes("8859_1"), "UTF-8"));
			pstmt.setString(2, new String(vo.getContent().getBytes("8859_1"), "UTF-8"));
			pstmt.setString(3, new String(vo.getName().getBytes("8859_1"), "UTF-8"));
			pstmt.setInt(4, vo.getBno());
			pstmt.executeUpdate();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	/**
	 * <pre>
	 * �Խñ��� ������ ������Ʈ�ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @param tempMap ���� ������ ���� Map<String, Object>�� ������ ������
	 * @throws SQLException
	 */
	public void modifyFile(Map<String, Object> tempMap) throws SQLException {
		int fno = 0;
		try {
			connect();
			Integer.parseInt(tempMap.get("FILE_NO").toString());
			pstmt = conn.prepareStatement(sqlUF);
			pstmt.setInt(1, fno);
			pstmt.executeUpdate();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}

	/**
	 * <pre>
	 * �Խñ��� ������ �����ϴ� ���� �޼ҵ�
	 * </pre>
	 * 
	 * @throws SQLException
	 */
	public void removeFile() throws SQLException {
		try {
			connect();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlUDF);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			disconnect();
		}
	}
}
