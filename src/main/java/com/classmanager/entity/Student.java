package main.java.com.classmanager.entity;

import java.time.LocalDate;

public class Student {
    private int id;
    private String studentNo;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private int classId;        // 外键，关联班级ID
    private String className;   // 用于界面显示，不存文件

    public Student(){}
    public Student(String studentNo , String name , String gender, LocalDate birthDate , int classId){
        this.studentNo = studentNo;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.classId = classId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString(){
        return className;
    }
}
