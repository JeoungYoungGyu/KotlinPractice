<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<c:set var="path" value="${pageContext.request.contextPath}" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${path}/resources/css/bootstrap.css" rel="stylesheet">
<link href="${path}/resources/css/insert.css" rel="stylesheet">
<title>글 수정</title>

<%-- jquery와 css인 bootstrap, content.css를 불러옴 --%>
<script type="text/javascript" src="${path}/resources/SE2/js/HuskyEZCreator.js" charset="utf-8"></script>
<script type="text/javascript" src="http://code.jquery.com/jquery-1.9.0.min.js"></script>
<script type="text/javascript">
var oEditors = [];

<%-- smarteditor를 불러옴 --%>
$(function(){
      nhn.husky.EZCreator.createInIFrame({
          oAppRef: oEditors,
          elPlaceHolder: "ir1",
          sSkinURI: "/smarteditorSample/SE2/SmartEditor2Skin.html",  
  		  <%-- 네이버 에디터의 툴바만 사용 --%>      
          htParams	 : {
              bUseToolbar : true,             
              bUseVerticalResizer : false,     
              bUseModeChanger : false,         
              fOnBeforeUnload : function(){
              }
          }, 
          fOnAppLoad : function(){
              oEditors.getById["ir1"].exec("PASTE_HTML", [""]);
          },
          fCreator: "createSEditor2"
      });

	  <%-- content 등록 및  유호성 검사에 관한 함수 --%>      
      $("#save").click(function(){ 
      		oEditors.getById["ir1"].exec("UPDATE_CONTENTS_FIELD", []);
    	
    		<%-- id가 save인 버튼을 눌렀을 때, content의 내용이 "", null, '&nbsp;', '<p>&nbsp;</p>' 중 하나라면 경고 표시 후 등록을 중단함--%>
        	var ir1 = $("#ir1").val();
        	if( ir1 == ""  || ir1 == null || ir1 == '&nbsp;' || ir1 == '<p>&nbsp;</p>')  {
            	alert("내용을 입력하세요.");
 			 	event.preventDefault();
             	return;
        	}
        	$("#update").submit();
      });    
});

<%-- 클래스가 error인 태그를 숨김 처리, submit을 눌렀을 때, 해당 클래스를 가진 태그가 값이 없다면 해당 클래스의 error 태그를 표시 --%>
$(document).ready(function() {
	<%-- 페이지가 로딩된다면 class가 error로 지정된 메세지를 숨김처리함. --%>
	$('.error').hide();
	<%-- submit을 눌렀을 때, 해당 클래스를 가진 태그가 값이 없다면 해당 클래스의 error 태그를 표시, writer, content 순으로 검사 --%>
	$('.submit').click(function(event) {
		if($('.insert_writer').val().length<1) {
			$('.insert_writer').next().show()
			event.preventDefault();
		}
		else {
			$('.insert_writer').next().hide();
		}
		if($('.insert_title').val().length<1) {
			$('.insert_title').next().show()
			event.preventDefault();
		}
		else {
			$('.insert_title').next().hide();
		}
	});
});

<%-- 파일 입력 창 등록 및 삭제에 관한 jqurey --%>
$(function(){
	<%-- 클래스가 fileAdd_btn을 누를 경우, addFileForm이 실행되며, id fileIndex가 있는 부분에 입력 폼이 추가됨 --%>
	$('.fileAdd_btn').click(addFileForm);
	<%-- 클래스가 fileDelBtn인 버튼을 누를 경우, 해당 입력 폼을 삭제함 --%>
	$(document).on('click', '.fileDelBtn', function(event) {
		$(this).parent().remove();
	});
});
var count = 0;

<%--
	0부터 시작하며, fileAdd_btn을 누른 횟수마다 count가 증가하며, file의 name과 id의 이름이 달라짐
	@author 정용규
	@since 2021-02-09
--%>
function addFileForm() {
	var html = "<div class='input-group mb-3'><div class='custom-file'><input type='file' class='custom-file-input' id='inputGroupFile"+(count)+"' name='file_"+(count)+"'></div><button class='fileDelBtn btn btn-secondary btn-sm'>삭제</button></div>";
	count++;
	$("#fileIndex").append(html);
}

var fileNoArry = new Array();
var fileNameArry = new Array();
<%-- --%>
function fn_del(value, name){		
	fileNoArry.push(value);
	fileNameArry.push(name);
	$("#fileNoDel").attr("value", fileNoArry);
	$("#fileNameDel").attr("value", fileNameArry);
}
</script>
</head>

<body>
	<div class="container">
		<div class="row">
			<%-- multipart/form-data 타입의 입력 폼, 제목, 작성자, 비밀번호, 내용, 파일을 입력 받음. --%>
			<form name="update" action="modifyBoard" method="POST" enctype="multipart/form-data">
				<input type="hidden" id="fileNoDel" name="fileNoDel[]" value="">
				<input type="hidden" id="fileNameDel" name="fileNameDel[]" value="">
				<input type="hidden" id="bno" name="bno" value="${board.bno}" />
				<table class="table mTable">
					<thead>
						<tr>
							<th class = "tHeader" id = "tColor">글 수정
						</tr>
					</thead>
					<tbody>
						<tr>
							<td>
								<div>
									<input type="text" class="insert_title form-control" value="${board.title}" id="title" name="title" maxlength='99' />
									<span class="error">제목을 입력해주십시오.</span>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div>
									<input type="text" class="insert_writer form-control" value="${board.name }" id="name" name="name" size="10" maxlength='14' /> 
									<span class="error">아이디를 입력해주십시오.</span>
								</div>
							</td>
						</tr>
						<tr>
							<td>
								<div>
									<textarea class="insert_context form-control" rows="10" cols="30" id="ir1" class="insert_content" name="content">${board.content }</textarea>
									<span class="error">내용을 입력해주십시오.</span>
								</div>
							</td>
						</tr>
						<%-- 기존에 있던 파일 목록을 불러옴 --%>
						<c:forEach var="file" items="${file}" varStatus="var">
							<tr>
								<td class="bl">
									<%-- 하이퍼링크를 실행하지 않으며, 파일 이름과 파일 아이디를 출력--%>					
									<a href="#" id="fileName" onclick="return false;">${file.org_filename}</a>(${file.file_id})
									<button id="fileDel" class="btn btn-secondary btn-sm fileDelBtn" onclick="fn_del('${file.file_id}','FILE_NO_${var.index}');" type="button">삭제</button>
								</td>
								<td>
									<input type="hidden" id="FILE_NO" name="FILE_NO_${var.index}" value="${file.file_id}">
								</td>
								<td>
									<input type="hidden" id="FILE_NAME" name="FILE_NAME" value="FILE_NO_${var.index}">
								</td>
							</tr>
						</c:forEach>
						<tr>
							<td id="fileIndex"></td>
						</tr>
						<tr>
							<%-- fileAdd_btn을 누르면 fn_addFile()을 실행함 --%>
							<td>
								<button type="button" class="fileAdd_btn btn btn-outline-primary">파일추가</button>
							</td>
						</tr>
						<tr>
							<td>
								<div class="bs">
									<%-- 입력 버튼을 누를 경우, 관련된 함수를 실행 후 컨트롤러 listPage로 이동 --%>				
									<button type="submit" class="save submit btn btn-outline-primary" id="save" value="저장" onclick="location.href= 'getBoardList">입력</button>
									<%-- 입력 버튼을 누를 경우, 컨트롤러 listPage로 이동 --%>									
									<button type="button" class="btn btn-outline-primary" onclick="location.href='getBoard?bno=${board.bno }'">취소</button>
								</div>
							</td>
						</tr>
					</tbody>
				</table>
			</form>
		</div>
	</div>
</body>
</html>
