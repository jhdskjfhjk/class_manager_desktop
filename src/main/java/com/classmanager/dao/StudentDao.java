package main.java.com.classmanager.dao;

import main.java.com.classmanager.entity.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
* 职责：读写 students.csv，实现增、删、改、查。
*文件格式：学号,姓名,性别,出生日期,班级ID
*注意：修改和删除需要重写整个文件（因为CSV不易原位修改）。
* */
public class StudentDao {
    private static final String FILE_PATH = "student.csv";
    private static final String HEADER = "studentNo,name,gender,birthDate,classId";


    //读取所有学生信息
    public List<Student> findAll() throws IOException {
        List<Student> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if(!file.exists()){
            return list;
        }

        try (BufferedReader reader = new BufferedReader(
              new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
              /*

              * FileInputStream：：打开一个字节流链接到指定文件（file） ， 使用UFTF_8
              * new BufferedReader：缓冲加载器
              InputStreamReader：：它是一个“转换器”：把字节流 → 字符流。

                你指定了 UTF-8 编码，告诉它：“这些字节是用 UTF-8 规则写成的，请按这个规则把字节组合成正确的字符（例如中文）”。

                现在可以按字符读了（read() 返回 char 值），但效率还不够高。
              *
              * */
        )){
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");//根据“，”分割
                if (parts.length < 5) continue;
                Student s = new Student();






            }
        }
    }

}
