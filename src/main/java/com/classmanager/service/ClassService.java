package main.java.com.classmanager.service;

import main.java.com.classmanager.dao.ClassDao;
import main.java.com.classmanager.dao.StudentDao;
import main.java.com.classmanager.entity.ClassInfo;
import main.java.com.classmanager.entity.Student;

import java.io.IOException;
import java.util.List;

//业务逻辑层
/*
*职责：整合班级和学生的操作，提供上层需要的功能。包括：

初始化班级

添加学生时校验

获取所有学生（并填充班级名称）

获取班级列表（供下拉框使用）
*
*
*
*
*
*
*
* */

public class ClassService {
    //创建初始对象
    private StudentDao studentDao = new StudentDao();
    private ClassDao classDao = new ClassDao();


    //初始化班级（程序启动时调用）

    public void initClasses () throws IOException {
        classDao.initDefaultClasses();
    }

    //获取所有班级
    public List<ClassInfo> getAllClass () throws IOException {
        return classDao.findAll();
    }

    //添加学生，带基本校验

    public void addStudent (Student s) throws IOException {
        if(s.getName() == null || s.getName().trim().isEmpty()) {//trim()去除字符串开头和结尾的空格
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (s.getStudentNo() == null || s.getStudentNo().trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        //学号唯一性检查
        studentDao.add(s);
    }

    //获取所有学生，并填充班级姓名
    public List<Student> getAllStudents() throws IOException {
        List <Student> students = studentDao.findAll();
        for (Student s : students) {
            String className = classDao.getClassNameById(s.getClassId());
            s.setClassName(className);
        }
        return students;
    }

    //删除学生
    public void deleteStudent (String studentNo) throws IOException {
        studentDao.deleteByStudentNo(studentNo);
    }

    //修改学生

    public void updateSudent (Student s) throws IOException {
        studentDao.update(s);
    }


}
