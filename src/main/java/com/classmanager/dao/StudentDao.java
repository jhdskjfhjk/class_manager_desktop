package main.java.com.classmanager.dao;

import main.java.com.classmanager.entity.Student;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLData;
import java.time.LocalDate;
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
        File file = new File(FILE_PATH);//指定文件
        if(!file.exists()){//没有，返回空列表
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
                s.setStudentNo(parts[0]);
                s.setName(parts[1]);
                s.setGender(parts[2]);
                s.setBirthDate(parts[3].isEmpty()? null : LocalDate.parse(parts[3]));
                s.setClassId(Integer.parseInt((parts[4])));
                list.add(s);
            }
        }
        return list;
    }

    //添加学生
    public void add (Student s) throws IOException {
        File file = new  File(FILE_PATH);
        boolean exists = file.exists();
        try(BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file , true) , StandardCharsets.UTF_8)
        )){
            if (!exists){
                writer.write(HEADER);
                writer.newLine();
            }
            String birth = s.getBirthDate() != null ? s.getBirthDate().toString() : "";

            writer.write(String.join(",",
                    s.getStudentNo(),
                    s.getName(),
                    s.getGender(),
                    birth,
                    String.valueOf((s.getClassId()))));
            writer.newLine();


        }
    }



    //修改学生：根据学号（唯一标识）更新，需重写整个文件

    public void update (Student updated) throws IOException {
        List <Student> all = findAll();
        for (int i = 0 ; i < all.size() ; i++){
            if(all.get(i).getStudentNo().equals(updated.getStudentNo())) {
                all.set(i , updated);
                break;
            }
        }
        overwriteFile(all);

    }
    //删除学生：根据学号删除
    public void deleteByStudentNo(String studentNo) throws IOException {
        List<Student> all = findAll();
        all.removeIf(s -> s.getStudentNo().equals(studentNo));
        overwriteFile(all);
    }
    //内部方法：把列表整体写入文件（覆盖）
    private void overwriteFile(List<Student> students) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(FILE_PATH),StandardCharsets.UTF_8)
        )){//标准输入，背下来就好
            writer.write(HEADER);
            writer.newLine();
            for(Student s : students){
                String birth = s.getBirthDate() != null ? s.getBirthDate().toString() : "";

                writer.write(String.join(
                        ",",
                        s.getStudentNo(),
                        s.getName(),
                        s.getGender(),
                        birth,
                        String.valueOf(s.getClassId())
                ));
                writer.newLine();//开新行

            }
        }
    }

}
