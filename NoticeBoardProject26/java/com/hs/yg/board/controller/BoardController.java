package com.hs.yg.board.controller;

import java.io.File;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hs.yg.board.Board;
import com.hs.yg.board.Page;
import com.hs.yg.board.Reply;
import com.hs.yg.board.service.BoardService;

/**
 * <pre>
 * BoardController
 * </pre>
 * 
 * @author �����
 */
@Controller
public class BoardController {
	@Autowired
	BoardService service;

	@RequestMapping("/")
	public String moveIndex() {
		return "board-index";
	}

	/**
	 * <pre>
	 * �Խ��� �� ����� �������� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param model addAttribute�� ����ϱ� ���� Model Ÿ���� ��ü
	 * @param num   ������ ��ȣ ��
	 * @throws Exception
	 */
	@RequestMapping("getBoardList")
	public String getBoardListController(Model model, @RequestParam(value = "num", defaultValue = "1") int num) throws Exception {
		Page page = new Page();
		page.setNum(num);
		page.setCount(service.getBoardCnt());
		List<Board> lPage = service.getBoardList(page.getDisplayPost(), page.getPostNum());
		model.addAttribute("boardList", lPage);
		model.addAttribute("page", page);
		model.addAttribute("select", num);
		return "board-list";
	}

	/**
	 * <pre>
	 * �˻� ���ǿ� �´� �Խñ� ����� �������� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param searchOption �˻� Ű����
	 * @param keyword      �˻���
	 * @param num          ������ ��ȣ ��
	 * @param model        addAttribute�� ����ϱ� ���� Model Ÿ���� ��ü
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value = "getSearchBoardList")
	public String getSearchBoardListController(@RequestParam(value = "searchOption", required = false, defaultValue = "title") String searchOption,
			@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, @RequestParam(value = "num", defaultValue = "1") int num, Model model)
			throws SQLException {
		Page page = new Page();
		page.setNum(num);
		page.setCount(service.getSearchBoardListCount(searchOption, keyword));
		List<Board> sLPage = service.getSearchBoardList(searchOption, keyword, page.getDisplayPost(), page.getPostNum());
		page.setSearchTypeKeyword(searchOption, keyword);
		model.addAttribute("boardList", sLPage);
		model.addAttribute("page", page);
		model.addAttribute("select", num);
		model.addAttribute("searchOption", searchOption);
		model.addAttribute("keyword", keyword);
		return "board-search-list";
	}

	/**
	 * <pre>
	 * �۰� ���� ��ȣ�� �ش�Ǵ� ��� �׸��� ������ ������ �������� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo    ��κ��� ������ BoardŸ���� ������
	 * @param model addAttribute�� ����ϱ� ���� Model Ÿ���� ��ü
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping("getBoard")
	public String getBoardController(Board vo, Model model) throws SQLException {
		Board board = service.getBoard(vo);
		List<Reply> replyList = service.getReplyList(vo.getBno());
		List<Map<String, Object>> fileList = service.getFileList(vo.getBno());
		model.addAttribute("board", board);
		model.addAttribute("reply", replyList);
		model.addAttribute("file", fileList);
		return "board-detail";
	}

	/**
	 * <pre>
	 * ��б��� ��й�ȣ Ȯ�� �� ��б��� ������ �������� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param bno    ��κ��� ������ �Խ��� ��ȣ
	 * @param passwd ��κ��� ������ �Խ��� ��й�ȣ
	 * @param model  addAttribute�� ����ϱ� ���� Model Ÿ���� ��ü
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping("getSecretBoard")
	public String getSecretBoardController(@RequestParam("bno") int bno, @RequestParam("tpasswd") String passwd, Model model) throws SQLException {
		Board board = service.getBoard(bno);
		String temp = "";
		List<Reply> replyList = service.getReplyList(bno);
		List<Map<String, Object>> fileList = service.getFileList(bno);
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(passwd.getBytes("utf8"));
			temp = String.format("%064x", new BigInteger(1, digest.digest()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (board.getPasswd().equals(temp)) {
			model.addAttribute("sboard", board);
			model.addAttribute("sreply", replyList);
			model.addAttribute("file", fileList);
			model.addAttribute("passwd", passwd);
			return "board-secret-detail";
		} else {
			return "redirect:getBoardList";
		}
	}

	/**
	 * <pre>
	 * ���� �Է��ϴ� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo        ��κ��� ������ Board Ÿ���� ������
	 * @param rttr      �����̷�Ʈ�� �� �信 �����͸� �Ѱ��ֱ� ���� RedirectAttributes Ÿ���� ��ü
	 * @param mpRequest ���� ������� ����ϱ� ���� MultipartHttpServletRequest Ÿ���� ��ü
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/addBoard", method = RequestMethod.POST)
	public String addBoardController(Board vo, RedirectAttributes rttr, MultipartHttpServletRequest mpRequest) throws Exception {
		int bno = service.addBoard(vo, mpRequest);
		rttr.addAttribute("bno", bno);
		return "redirect:getBoard";
	}

	/**
	 * <pre>
	 * insertBoard.jsp�� �̵��ϱ� ���� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("moveAddBoard")
	public String moveAddBoardController() throws Exception {
		return "board-insert";
	}

	/**
	 * <pre>
	 * �۰� �ۿ� ���õ� ��� �� ���� �����͸� �����ϴ� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo ��κ��� ������ Board Ÿ���� ������
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping("/removeBoard")
	public String removeBoardController(Board vo) throws SQLException {
		service.removeBoard(vo);
		return "redirect:getBoardList";
	}

	/**
	 * <pre>
	 * updateBoard.jsp�� �̵��ϱ� ���� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param bno   ��κ��� ������ �Խ��� ��ȣ
	 * @param model addAttribute�� ����ϱ� ���� Model Ÿ���� ��ü
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping("moveModifyBoard")
	public String moveModifyBoardController(int bno, Model model) throws SQLException {
		Board board = service.getBoard(bno);
		List<Reply> replyList = service.getReplyList(bno);
		List<Map<String, Object>> fileList = service.getFileList(bno);
		model.addAttribute("board", board);
		model.addAttribute("reply", replyList);
		model.addAttribute("file", fileList);
		return "board-update";
	}

	/**
	 * <pre>
	 * ���� ������ �����ϴ� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo        ��κ��� ������ Board Ÿ���� ������
	 * @param rttr      �����̷�Ʈ�� �� �信 �����͸� �Ѱ��ֱ� ���� RedirectAttributes Ÿ���� ��ü
	 * @param files     ������ ���� ��ȣ���� String �迭 ���·� ������
	 * @param fileNames ������ ���� �̸����� String �迭 ���·� ������
	 * @param mpRequest ���� ������� ����ϱ� ���� MultipartHttpServletRequest Ÿ���� ��ü
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/modifyBoard", method = RequestMethod.POST)
	public String modifyBoardController(Board vo, RedirectAttributes rttr, @RequestParam(required = false, value = "fileNoDel[]") String[] files,
			@RequestParam(required = false, value = "fileNameDel[]") String[] fileNames, MultipartHttpServletRequest mpRequest) throws Exception {
		service.modifyBoard(vo, files, fileNames, mpRequest);
		rttr.addAttribute("bno", vo.getBno());
		return "redirect:getBoard";
	}

	/**
	 * <pre>
	 * ����� ����ϴ� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param vo   ��κ��� ������ Reply Ÿ���� ������
	 * @param rttr �����̷�Ʈ�� �� �信 �����͸� �Ѱ��ֱ� ���� RedirectAttributes Ÿ���� ��ü
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping("/addReply")
	public String addReplyController(Reply vo, RedirectAttributes rttr) throws SQLException {
		service.addReply(vo);
		rttr.addAttribute("bno", vo.getRbno());

		return "redirect:getBoard";
	}

	/**
	 * <pre>
	 * ������ �ٿ�ε��ϱ� ���� ��Ʈ�ѷ� �޼ҵ�
	 * </pre>
	 * 
	 * @param fno      ��κ��� ������ ���� ��ȣ
	 * @param response ������ �ٿ�ε� �ޱ� ���� HttpServletResponse Ÿ���� ��ü
	 * @throws Exception
	 */
	@RequestMapping(value = "/getFile")
	public void getFileController(@RequestParam("num") int fno, HttpServletResponse response) throws Exception {
		Map<String, Object> resultMap = service.getFile(fno);
		String storedFileName = (String) resultMap.get("new_filename");
		String originalFileName = (String) resultMap.get("org_filename");

		byte fileByte[] = org.apache.commons.io.FileUtils.readFileToByteArray(new File("C:\\Users\\wjddy\\Downloads\\file\\" + storedFileName));
		response.setContentType("application/octet-stream");
		response.setContentLength(fileByte.length);
		response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(originalFileName, "UTF-8") + "\";");
		response.getOutputStream().write(fileByte);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}
