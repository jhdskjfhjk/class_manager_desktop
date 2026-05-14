package main.java.com.classmanager.dao;

import main.java.com.classmanager.entity.ClassInfo;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/*
* 班级数据存储
* 职责：读写 classes.csv 文件，提供“获取所有班级”、“根据ID获取班级名称”、“添加班级”等方法。
* 文件格式：id,班级名称
*
*
*
* */
public class ClassDao {
    private static final String FILE_PATH = "classes.csv";
    private static final String HESDER = "id,calssName"

    //获取所有班级
    public List<ClassInfo> findAll() throws IOException{
        List<ClassInfo> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if(!file.exists()){
            return list;
        }

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
        )){
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if(firstLine){
                    firstLine = false'
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                list.add(new ClassInfo(id,name));
            }
        }
        return list;
    }

    //根据id查找班级姓名
    public String getClassNameById (int classId) throws IOException {
        return findAll().stream()
                .filter(c -> c.getId() == classId)//根据classId匹配
                .map(ClassInfo::getClassName)
                .findFirst()
                .orElse("未知班级");

    }
    //添加班级（如果文件不存在则创建并写入表头）
    public void addClass(ClassInfo c) throws IOException {
        File file = new File (FILE_PATH);
        boolean exists = file.exists();
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file,true),StandardCharsets.UTF_8)
        )) {

            if (!exists){
                writer.write(HESDER);
                writer.newLine();
            }
            writer.write(c.getId() + "," + c.getClassName());
            writer.newLine();
        }
    }

    //初始化默认班级
    public void initDefaultClasses() throws IOException {
        if(findAll().isEmpty()){
            addClass(new ClassInfo(1,"一年级一班"));
            addClass(new ClassInfo(2,"一年级二班"));
            addClass(new ClassInfo(3."二年级二班"));
        }
    }
}










