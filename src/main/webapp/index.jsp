<html>
<body>
<h2>FMALL</h2>
    SpringMVC上传文件1
    <form name = "form1" action="/manage/product/upload_file.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file" />
        <input type="submit" value="上传文件"/>
    </form>

    SpringMVC上传文件2-富文本
    <form name = "form1" action="/manage/product/upload_rich_file.do" method="post" enctype="multipart/form-data">
        <input type="file" value="rich_file" />
        <input type="submit" value="上传富文本" />
    </form>
</body>
</html>
