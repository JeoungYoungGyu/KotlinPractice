package com.hs.yg.board.controller;

import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.hs.yg.board.Board;
import com.hs.yg.board.Page;
import com.hs.yg.board.Reply;
import com.hs.yg.board.service.BoardService;

@Controller
public class BoardController {

	@Autowired
	BoardService service;
	
	// * ó�� ������, index.jsp�� �̵�
	// index -> index.jsp -> listPage.jsp
	@RequestMapping("/")
	public String goIndex() {
		return "index"; 
	}	
			
	// * �Խñ� ����¡ ����, listPage.jsp���� num�� �Է¹����� Page page�� List<board> pageList�� �޾ƿ� listPage.jsp�� ����
	// page�� ���޹��� ������ ��ȣ�� service.getBoardCnt()�� ���� ���� �Խñ� ���� ���� page�� �������� �ʱ�ȭ�� -> 
	// service.getListPage()�� �ʱ�ȭ�� ������ displatPost�� postNum�� ���� �� ����� lPage�� ���� ->
	// model.addAttribute�� �̿��� lPage�� boardList, page�� page, num�� select�� listPage.jsp�� ������.
	// listPage -> listPage.jsp -> content or searchListPage or listPage(������ �̵�)
	@RequestMapping("/listPage") 
	public void listpage(Model model, @RequestParam(value = "num", defaultValue = "1") int num) throws Exception {
		 Page page = new Page();	 
		 page.setNum(num);
		 page.setCount(service.getBoardCnt());  
		 List<Board> lPage = service.getListPage(page.getDisplayPost(), page.getPostNum());
		 model.addAttribute("boardList", lPage);   
		 model.addAttribute("page", page);
		 model.addAttribute("select", num);		 
	}
	
	// * �˻��� �Խñ� ����¡ ����, searchListPage.jsp����  searchOption�� keyword �׸��� num�� �Է¹�����, Page page�� List<board> list�� �޾ƿ� searchListPage.jsp�� ����
	// page�� ���޹��� ������ ��ȣ�� service.getSearchedBoardCnt()�� ���� ���� �˻��� �Խñ� ���� ���� page�� �������� �ʱ�ȭ�� -> 
	// service.getListPage()��  searchOption, keyword, displatPost�� postNum�� ���� �� ����� sLPage�� ���� ->
	// model.addAttribute�� �̿��� sLPage�� boardList, page�� page, num�� select�� searchOption�� keyword�� ���� �̸����� searchListPage.jsp�� ������.
	// searchListPage -> searchListPage.jsp -> content or searchListPage(������ �̵�) or listPage
	@RequestMapping(value = "/searchListPage")
	public void getBoardSearchList(@RequestParam(value = "searchOption",required = false, defaultValue = "title") String searchOption,
		@RequestParam(value = "keyword",required = false, defaultValue = "") String keyword, 
		@RequestParam(value = "num", defaultValue = "1") int num, Model model) throws SQLException {
		Page page = new Page();	 
		page.setNum(num);
		page.setCount(service.getSearchedBoardCnt(searchOption, keyword));  
		List<Board> sLPage = service.getSearchedBoardList(searchOption, keyword, page.getDisplayPost(), page.getPostNum());
		page.setSearchTypeKeyword(searchOption, keyword);
		model.addAttribute("boardList", sLPage);
		model.addAttribute("page", page);
		model.addAttribute("select", num);
		model.addAttribute("searchOption", searchOption);
		model.addAttribute("keyword", keyword);	
	}

	// * �� �� ����, listaPage.jsp �Ǵ� searchListPage.jsp���� bno�� �Է� �޾ƿ�, Board board�� List<Reply> replyList�� �۰� ��� �����͸� ������ �� content.jsp�� ����
	// content -> content.jsp -> writeReply or secret or move or delete or listPage
	@RequestMapping("/content") 
	public String getBoard(Board vo, Model model) throws SQLException {
        Board board = service.getContent(vo);
        List<Reply> replyList = service.getReplyList(vo.getBno());
		model.addAttribute("board", board); 
		model.addAttribute("reply", replyList);
		return "content"; 
	}
	
	// * ��б� Ȯ��, content.jsp���� num�� tpasswd�� �Է� �޾�, bno�� passwd�� �����Ѵ�. Board board�� List<Reply> replyList�� �۰� ��� �����͸� ����.
	//board.passwd��  passwd�� ��ġ�ϴٸ� board�� replyList�� ������ model.addAttribute�� �̿��� board�� replyList�� sboard�� sreply��� �̸����� content.jsp�� ����
	//secret -> secret.jsp -> writeReply or move or delete or listPage
	@RequestMapping("/secret")
	public String getBoard(@RequestParam("num") int bno, @RequestParam("tpasswd") String passwd, Model model) throws SQLException {
        Board board = service.getContent(bno);
        List<Reply> replyList = service.getReplyList(bno);
        if(board.getPasswd().equals(passwd)) {
        	model.addAttribute("sboard", board); 
        	model.addAttribute("sreply", replyList);
        }
		return "secretPage"; 
	}
	
	// * �� ����, inseartBoard.jsp���� name, passwd, title, content, file�� �Է� �޾�, MultipartHttpServletRequest�� ���� �۰� ������ �� ���� �����ͺ��̽��� ����.
	// dao inseartBoard�� ���� dBoard�� dBoard_files�� �۰� ������ ������ ��, listPage�� �����̷�Ʈ�Ѵ�.
	@RequestMapping(value = "/insert", method = RequestMethod.POST) 
	public String insertBoard(Board vo, MultipartHttpServletRequest mpRequest) throws Exception { 
		service.insertBoard(vo, mpRequest); 
		return "redirect:listPage"; 
	}
	
	// * �� ���� ������ �̵�, content.jsp or secretPage.jsp���� �����۸�ũ�� ���� �̵�, inseatBoard.jsp�� �̵���
	// move -> insertBoard.jsp
	@RequestMapping("/move") 
	public String moveInsertBoard()throws Exception{
		return "insertBoard";
	}
	
	// * �� ����,  content.jsp or secretPage.jsp���� �����۸�ũ�� ���� �̵�, delete ��Ʈ�ѷ� �̵��� int bno�� ��������, deleteBoard�� ���� �ش��ϴ� bno�� ����, ���, �� ������ ������
	// dao deleteBoard�� ���� dBoard, dBoard_files, dBoard_reply�� �����͸� ������ ��,  listPage�� �����̷�Ʈ�Ѵ�.
	@RequestMapping("/delete") 
	public String deleteBoard(Board vo) throws SQLException {
		service.deleteBoard(vo);
		return "redirect:listPage";
	}
	
	// * ��� ���� ,  content.jsp or secretPage.jsp���� rwriter�� rcontent �׸��� bno�� �Է¹޴´�. 
	//HttpServletRequest request�� ���� Reply vo�� ������ ������ ��, insertReply�� ������ dBoard_reply�� �ش� ������ �Է��� ��, listPage�� �����̷�Ʈ�Ѵ�.
	@RequestMapping("/writeReply") 
	public String writeReply(Reply vo, HttpServletRequest request) throws SQLException {
		vo.setRbno(Integer.parseInt(request.getParameter("bno")));
		vo.setRcontent(request.getParameter("rcontent"));
		vo.setRwriter(request.getParameter("rwriter"));
		service.insertReply(vo);
		return "redirect:listPage";
	}
	
	// �� �̻� ������� �ʴ� ��Ʈ�ѷ� ------------------------------------------------------------------------------------------------------------------
	
	 // �˻��� �Խñ� ���
	@RequestMapping("/search") 
	public String getSearchedBoardList(@RequestParam("condition") String searchOption, 
			@RequestParam("keyword") String keyword, @RequestParam(value = "num", defaultValue = "1")
			int num, Model model) throws SQLException {
		Page page = new Page();	 
		page.setNum(num);
		page.setCount(service.getSearchedBoardCnt(searchOption, keyword)); 
		List<Board> boardList = service.getSearchedBoardList(searchOption, keyword, page.getDisplayPost(), page.getPostNum());
		model.addAttribute("boardList", boardList);
		model.addAttribute("page", page);
		model.addAttribute("select", num);
		model.addAttribute("searchOption", searchOption);
		model.addAttribute("keyword", keyword);		
		return "searchListPage"; 
	}
	
	// �Խñ� ���
	@RequestMapping("/list") 
	public String getBoardList(Board vo, Model model) throws SQLException {
		List<Board> boardList = service.getBoardList(vo);
		model.addAttribute("boardList", boardList);
		return "listPage"; 
	}
}
