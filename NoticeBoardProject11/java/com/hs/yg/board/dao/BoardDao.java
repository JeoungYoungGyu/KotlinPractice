package com.hs.yg.board.dao;

import org.springframework.stereotype.Repository;
import com.hs.yg.board.Board;
import com.hs.yg.board.Reply;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class BoardDao {	
	
	//DB�� ����
	final private String driver = "org.mariadb.jdbc.Driver"; 
	final private String url = "jdbc:mariadb://127.0.0.1:3306/board";
	final private String userid = "Jeoung"; 
	final private String userpw = "1111"; 
	
	//������ �ʿ��� ������
	private Connection conn; 
	private PreparedStatement pstmt = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	//insertBoard�� insertFile�� ���õ� ������
	final private String sqlI = "INSERT INTO dBoard(title, content, vname, passwd) values (?,?,?,?)";
	final private String sqlIF = "INSERT INTO dboard_file(bno, org_filename, new_filename, file_size) VALUES (?, ?, ?, ?)";
	
	//insertReply�� ���õ� ������
	final private String sqlRI = "INSERT INTO dboard_reply(bno, content, writer) VALUES (?, ?, ?)";
	final private String sqlRU = "UPDATE dboard SET replyCnt = (SELECT COUNT(*) FROM dboard_reply WHERE bno = ?) WHERE bno = ?";
	
	//deleteBoard�� ���õ� ������
	final private String sqlD = "DELETE FROM dboard WHERE bno=?";
	final private String sqlDR = "DELETE FROM dboard_reply WHERE bno=?";
	final private String sqlDF = "DELETE from dboard_file WHERE bno=?";
	
	//getContent�� ���õ� ������
	final private String sqlGCU = "UPDATE dboard SET viewCnt = (SELECT SUM(viewCnt) FROM dboard WHERE bno = ?) + 1 WHERE bno = ?"; 
	final private String sqlGC = "SELECT* FROM dboard where bno=?";
	
	//getBoardList�� ���õ� ������	
	final private String sqlGBL = "SELECT* FROM dboard";
	
	//getBoardCnt�� ���õ� Ŀ���� 
	final private String sqlCnt = "select count(*) as cnt from dboard";	
	//getSearchedBoardCnt�� ���õ� ������
	final private String sqlSCntT = "select count(*) as cnt from dboard where title like ?";	
	final private String sqlSCntC = "select count(*) as cnt from dboard content like ?";	
	final private String sqlSCntTC = "select count(*) as cnt from dboard where title like ? or content like ?";	
	
	//getListPage�� ���õ� ������
	final private String sqlP = "select bno, title, content, vname, passwd, replyCnt, viewCnt from dboard order by bno desc LIMIT ?, ?";
	final private String sqlB = "select sum(file_size) as SUM from dboard_file where bno = ?";
	final private String sqlGB = "select bno from dboard order by bno desc LIMIT 0, 1";

	//getSearchedBoardList�� ���õ� ������
	final private String sqlST = "SELECT* FROM dboard where title like ? order by bno desc LIMIT ?, ?"; 
	final private String sqlSC = "SELECT* FROM dboard where content like ? order by bno desc LIMIT ?, ?"; 
	final private String sqlSTC = "SELECT* FROM dboard where title like ? or content like ? order by bno desc LIMIT ?, ?"; 
	
	//getReplyList�� ���õ� ������
	final private String sqlRGRL = "SELECT * FROM dboard_Reply where bno=?";	
	
	//DB ����
	public void connect() {
		try { 
			Class.forName(driver); 
			conn = DriverManager.getConnection(url, userid, userpw); 
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	//DB ���� ����
	public void disconnect() {
		try {
			if(pstmt != null) pstmt.close();
			if(conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//sqlI�� ����, 8859_1 ������ �����͸� UTF-8�� ��ȯ ��, DB�� ���� -> sqlGB�� ������ ���� �ֱ��� �Խñ� 1���� bno�� ������ ��ȯ��
	public int insertBoard(Board vo) throws SQLException {
		int bno = 0;
		connect();
		try {
			pstmt = conn.prepareStatement(sqlI);
			pstmt.setString(1, new String(vo.getTitle().getBytes("8859_1"), "UTF-8"));
			pstmt.setString(2, new String(vo.getContent().getBytes("8859_1"), "UTF-8"));
			pstmt.setString(3, new String(vo.getName().getBytes("8859_1"), "UTF-8"));
			pstmt.setString(4, new String(vo.getPasswd().getBytes("8859_1"), "UTF-8"));
			pstmt.executeUpdate();
			pstmt = conn.prepareStatement(sqlGB);
			rs = pstmt.executeQuery();
			if(rs.next())	bno = rs.getInt("bno");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		disconnect();
		return bno;
	}

	//sqlB�� ������ �ش� bno�� �� ���� �뷮�� ���� sum�� ���� -> sqlIF�� ������ bno, ���� ���� �̸�, ���ο� ���� �̸�, ���� �뷮�� �����Ѵ�. �̶�, ���� �뷮�� 100MB �̳��� ���� ���ǰ� ����ȴ�.
	public void insertFile(Map<String, Object> map, int bno) throws Exception{
		int sum = 0; 
		long longToInt = 0;
		String fileName = new String(map.get("ORG_FILE_NAME").toString().getBytes("8859_1"), "UTF-8");
		connect();
		pstmt = conn.prepareStatement(sqlB);
		pstmt.setInt(1, bno);	
		rs = pstmt.executeQuery();
		if(rs.next())	sum = rs.getInt("SUM");
		pstmt = conn.prepareStatement(sqlIF);
		pstmt.setInt(1, bno);			
		pstmt.setString(2, fileName);
		pstmt.setString(3, map.get("STORED_FILE_NAME").toString());
		longToInt = (Long) map.get("FILE_SIZE");
		pstmt.setLong(4, (Long) map.get("FILE_SIZE"));
		
		if(sum + (int)longToInt > 104857600) { // ������ �뷮 ���� 100MB�� ���� ��, sql���� �������� ����.
			return;
		}
		pstmt.executeUpdate();	
		disconnect();
	}
	
	//sqlDR�� ���� �ش� bno�� ���� reply�� ���� �����Ѵ�. sqlDF�� ���� �ش� bno�� ���� File�� ���� �����Ѵ�. ���� sqlD�� ���� �ش� bno�� ���� �Խñ��� �����Ѵ�.
	public void deleteBoard(Board vo) throws SQLException {
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
		disconnect();
	}

	//sqlGCU�� ����, �ش� �Խñ��� viewCnt�� 1 ���� -> sqlGC�� ���� �ش� bno�� bno, title, content, vname, passwd, replyCnt�� Board t�� ������ �� ��ȯ�Ѵ�.
	public Board getContent(Board vo) throws SQLException {
		Board t = null;
		connect();
		pstmt = conn.prepareStatement(sqlGCU);
		pstmt.setInt(1, vo.getBno());
		pstmt.setInt(2, vo.getBno());
		rs = pstmt.executeQuery();
		
		pstmt = conn.prepareStatement(sqlGC);
		pstmt.setInt(1, vo.getBno());
		rs = pstmt.executeQuery();
		if(rs.next()){
			t = new Board();
			t.setBno(rs.getInt("bno"));
			t.setTitle(rs.getString("title"));
			t.setContent(rs.getString("content"));
			t.setName(rs.getString("vname"));
			t.setPasswd(rs.getString("passwd"));
			t.setCommentCount(rs.getInt("replyCnt"));
		}
		disconnect();
		return t; 
	}
	
	public Board getContent(int dno) throws SQLException {
		Board t = null;
		connect();
		pstmt = conn.prepareStatement(sqlGCU);
		pstmt.setInt(1, dno);
		pstmt.setInt(2, dno);
		rs = pstmt.executeQuery();
		
		pstmt = conn.prepareStatement(sqlGC);
		pstmt.setInt(1, dno);
		rs = pstmt.executeQuery();
		if(rs.next()){
			t = new Board();
			t.setBno(rs.getInt("bno"));
			t.setTitle(rs.getString("title"));
			t.setContent(rs.getString("content"));
			t.setName(rs.getString("vname"));
			t.setPasswd(rs.getString("passwd"));
			t.setCommentCount(rs.getInt("replyCnt"));
		}
		disconnect();
		return t; 
	}
	
	//sqlGBL�� ����, bno, title, content, vname, passwd, replyCnt, viewCnt�� Board t�� ������ ��, List �������� ��ȯ�Ѵ�.
	public List<Board> getBoardList(Board vo) throws SQLException {
		List<Board> temp = new ArrayList<Board>();
		connect();
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sqlGBL);
		while (rs.next()) { 
			Board t = new Board();
			t.setBno(rs.getInt("bno"));
			t.setTitle(rs.getString("title"));
			t.setContent(rs.getString("content"));
			t.setName(rs.getString("vname"));
			t.setPasswd(rs.getString("passwd"));
			t.setCommentCount(rs.getInt("replyCnt"));
			t.setViewCount(rs.getInt("viewCnt"));
			temp.add(t);
		}
		disconnect();
		return temp;
	}
	
	//sqlRI�� ����, �ش�Ǵ� bno�� Rcontent, Rwriter��  dboard_reply�� ���� -> sqlRu�� ���� �ش�Ǵ� dboard�� bno�� replyCnt�� ������Ų��. 
	public void insertReply(Reply vo) throws SQLException {
		connect();
		pstmt = conn.prepareStatement(sqlRI); 	// ����� �����ͺ��̽� �ȿ� ������ ��
		pstmt.setInt(1, vo.getRbno());
		pstmt.setString(2, vo.getRcontent());
		pstmt.setString(3, vo.getRwriter());
		pstmt.executeQuery();
			
		pstmt = conn.prepareStatement(sqlRU); 	// dboard�� replyCnt�� ������Ŵ
		pstmt.setInt(1, vo.getRbno());
		pstmt.setInt(2, vo.getRbno());
		pstmt.executeQuery();
		disconnect();
	}
	
	//sqlRGRL�� ����, �ش�Ǵ� bno�� rno, content, writer�� Reply t�� ���� �� List �������� ��ȯ�Ѵ�.
	public List<Reply> getReplyList(int bno) throws SQLException {
		List<Reply> temp = new ArrayList<Reply>();
		connect();	
		pstmt = conn.prepareStatement(sqlRGRL);
		pstmt.setInt(1, bno);
		rs = pstmt.executeQuery();
		while (rs.next()) { 
			Reply t = new Reply();
			t.setRbno(rs.getInt("bno"));
			t.setRrno(rs.getInt("rno"));
			t.setRcontent(rs.getString("content"));
			t.setRwriter(rs.getString("writer"));
			temp.add(t);
		}
		disconnect();
		return temp;
	}

	//searchOption ���ǿ� ���� sqlSTC, sqlST, sqlSC �� �� �� �ϳ��� ���� �����Ѵ�. title, content, vname, passwd, replyCnt, viewCnt�� Board t�� ������ ��, List �������� ��ȯ�Ѵ�.
	public List<Board> getSearchedBoardList(String searchOption, String keyword, int displayPost, int pageNum) throws SQLException {
		List<Board> temp = new ArrayList<Board>();		
		String akeyword = "%"+keyword+"%";
		connect();
		if(searchOption.contentEquals("titleCon")) {	//���ǿ� ����  sql���� �ٸ���.
			pstmt = conn.prepareStatement(sqlSTC);
			pstmt.setString(1, akeyword);
			pstmt.setString(2, akeyword);
			pstmt.setInt(3, displayPost);
			pstmt.setInt(4, pageNum);
		}else if(searchOption.contentEquals("title")) {
			pstmt = conn.prepareStatement(sqlST);
			pstmt.setString(1, akeyword);
			pstmt.setInt(2, displayPost);
			pstmt.setInt(3, pageNum);
		}else {
			pstmt = conn.prepareStatement(sqlSC);
			pstmt.setString(1, akeyword);
			pstmt.setInt(2, displayPost);
			pstmt.setInt(3, pageNum);
		}
		rs = pstmt.executeQuery();	
		while (rs.next()) { 
			Board t = new Board();
			t.setBno(rs.getInt("bno"));
			t.setTitle(rs.getString("title"));
			t.setContent(rs.getString("content"));
			t.setName(rs.getString("vname"));
			t.setPasswd(rs.getString("passwd"));
			t.setCommentCount(rs.getInt("replyCnt"));
			t.setViewCount(rs.getInt("viewCnt"));
			temp.add(t);
		}
		disconnect();
		return temp;
	}

	//sqlCnt�� ���� ����, dboard�� count�� int�� �޾� ��ȯ�Ѵ�.
	public int getBoardCnt() throws SQLException {
		int temp = 0;
		connect();
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sqlCnt);	
		if(rs.next())	temp = rs.getInt("cnt");	
		disconnect();
		return temp;
	}

	//searchOption ���ǿ� ���� sqlSCntTC, sqlSCntT, sqlSCntC �� �� �� �ϳ��� ���� �����Ѵ�. �ش� ������ �Խñ� ���� int�� �޾� ��ȯ�Ѵ�.
	public int getSearchedBoardCnt(String searchOption, String keyword) throws SQLException {
		int num = 0;
		String akeyword = "%"+keyword+"%";
		connect();
		if(searchOption.contentEquals("titleCon")) {
			pstmt = conn.prepareStatement(sqlSCntTC);
			pstmt.setString(1, akeyword);
			pstmt.setString(2, akeyword);
		}else if(searchOption.contentEquals("title")) {
			pstmt = conn.prepareStatement(sqlSCntT);
			pstmt.setString(1, akeyword);
		}else {
			pstmt = conn.prepareStatement(sqlSCntC);
			pstmt.setString(1, akeyword);
		}
		rs = pstmt.executeQuery();	
		if(rs.next())	num = rs.getInt("cnt");
		disconnect();
		return num;
	}
	
	public List<Board> getListPage(int displayPost, int postNum) throws SQLException {
		List<Board> temp = new ArrayList<Board>();
		connect();
		pstmt = conn.prepareStatement(sqlP);
		pstmt.setInt(1, displayPost);
		pstmt.setInt(2, postNum);
		rs = pstmt.executeQuery();	
		while (rs.next()) { 
			Board t = new Board();
			t.setBno(rs.getInt("bno"));
			t.setTitle(rs.getString("title"));
			t.setContent(rs.getString("content"));
			t.setName(rs.getString("vname"));
			t.setPasswd(rs.getString("passwd"));
			t.setCommentCount(rs.getInt("replyCnt"));
			t.setViewCount(rs.getInt("viewCnt"));
			temp.add(t);
		}
		disconnect();
		return temp;
		}
}
