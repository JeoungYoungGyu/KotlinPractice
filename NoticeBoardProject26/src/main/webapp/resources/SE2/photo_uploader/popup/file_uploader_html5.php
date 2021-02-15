<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@page import="java.io.*"%>
<%@page import="java.util.UUID"%>
<%@page import="java.text.SimpleDateFormat"%>
 
<%
    //��������
    String sFileInfo = "";
    //���ϸ��� �޴´� - �Ϲ� �������ϸ�
    String filename = request.getHeader("file-name");
    //���� Ȯ����
    String filename_ext = filename.substring(filename.lastIndexOf(".") + 1);
    //Ȯ���ڸ��ҹ��ڷ� ����
    filename_ext = filename_ext.toLowerCase();
 
    //�̹��� ���� �迭����
    String[] allow_file = { "jpg", "png", "bmp", "gif" };
 
    //�����鼭 Ȯ���ڰ� �̹������� 
    int cnt = 0;
    for (int i = 0; i < allow_file.length; i++) {
        if (filename_ext.equals(allow_file[i])) {
            cnt++;
        }
    }
 
    //�̹����� �ƴ�
    if (cnt == 0) {
        out.println("NOTALLOW_" + filename);
    } else {
        //�̹����̹Ƿ� �ű� ���Ϸ� ���丮 ���� �� ���ε�   
        //���� �⺻���
        String dftFilePath = request.getSession().getServletContext().getRealPath("/");
        //���� �⺻��� _ �󼼰��
        String filePath = dftFilePath + "SE2" + File.separator + "multiupload" + File.separator;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String realFileNm = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String today = formatter.format(new java.util.Date());
        realFileNm = today + UUID.randomUUID().toString() + filename.substring(filename.lastIndexOf("."));
        String rlFileNm = filePath + realFileNm;
        ///////////////// ������ ���Ͼ��� ///////////////// 
        InputStream is = request.getInputStream();
        OutputStream os = new FileOutputStream(rlFileNm);
        int numRead;
        byte b[] = new byte[Integer.parseInt(request.getHeader("file-size"))];
        while ((numRead = is.read(b, 0, b.length)) != -1) {
            os.write(b, 0, numRead);
        }
        if (is != null) {
            is.close();
        }
        os.flush();
        os.close();
        ///////////////// ������ ���Ͼ��� /////////////////
 
        // ���� ���
        sFileInfo += "&bNewLine=true";    
        sFileInfo += "&sFileName=" + filename;    
        sFileInfo += "&sFileURL=/smarteditorSample/SE2/multiupload/"+realFileNm;
        out.println(sFileInfo);
    }
%>