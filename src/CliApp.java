import main.java.com.classmanager.entity.ClassInfo;
import main.java.com.classmanager.entity.Student;
import main.java.com.classmanager.service.ClassService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class CliApp {
    private static ClassService classService = new ClassService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try{
            classService.initClasses();
        } catch (IOException e) {
            System.out.println("初始化班级失败：" + e.getMessage());
            return;
        }

        System.out.println("==========班级管理系统=============");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": listStudents(); break;
                case "2": addStudent(); break;
                case "3": editStudent(); break;
                case "4": deleteStudent(); break;
                case "5": listClasses(); break;
                case "0": running = false; break;
                default: System.out.println("无效选项，请重新输入");
            }
        }
    }
    private static void printMenu() {
        System.out.println("\n请选择操作：");
        System.out.println("1. 查看所有学生");
        System.out.println("2. 添加学生");
        System.out.println("3. 修改学生");
        System.out.println("4. 删除学生");
        System.out.println("5. 查看班级列表");
        System.out.println("0. 退出");
        System.out.print("> ");
    }

    private static void listStudents() {
        try {
            List<Student> students = classService.getAllStudents();
            if (students.isEmpty()) {
                System.out.println("暂无学生记录");
                return;
            }
            System.out.printf("%-12s %-10s %-6s %-12s %-10s%n",
                    "学号", "姓名", "性别", "出生日期", "班级");
            for (Student s : students) {
                System.out.printf("%-12s %-10s %-6s %-12s %-10s%n",
                        s.getStudentNo(),
                        s.getName(),
                        s.getGender(),
                        s.getBirthDate() != null ? s.getBirthDate() : "",
                        s.getClassName() != null ? s.getClassName() : "");
            }
        } catch (IOException e) {
            System.out.println("读取学生失败：" + e.getMessage());
        }
    }

    private static void addStudent() {
        try {
            System.out.print("请输入学号：");
            String studentNo = scanner.nextLine().trim();
            System.out.print("请输入姓名：");
            String name = scanner.nextLine().trim();
            System.out.print("请输入性别(男/女)：");
            String gender = scanner.nextLine().trim();
            System.out.print("请输入出生日期(yyyy-MM-dd，可留空)：");
            String birthText = scanner.nextLine().trim();
            LocalDate birthDate = null;
            if (!birthText.isEmpty()) {
                birthDate = LocalDate.parse(birthText);
            }
            System.out.print("请输入班级ID：");
            int classId = Integer.parseInt(scanner.nextLine().trim());
            Student s = new Student(studentNo, name, gender, birthDate, classId);
            classService.addStudent(s);
            System.out.println("添加成功");
        } catch (DateTimeParseException e) {
            System.out.println("出生日期格式错误，应为 yyyy-MM-dd");
        } catch (NumberFormatException e) {
            System.out.println("班级ID必须为整数");
        } catch (IllegalArgumentException e) {
            System.out.println("输入有误：" + e.getMessage());
        } catch (IOException e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    private static void editStudent() {
        try {
            System.out.print("请输入要修改的学号：");
            String studentNo = scanner.nextLine().trim();
            System.out.print("请输入新姓名：");
            String name = scanner.nextLine().trim();
            System.out.print("请输入新性别(男/女)：");
            String gender = scanner.nextLine().trim();
            System.out.print("请输入新出生日期(yyyy-MM-dd，可留空)：");
            String birthText = scanner.nextLine().trim();
            LocalDate birthDate = null;
            if (!birthText.isEmpty()) {
                birthDate = LocalDate.parse(birthText);
            }
            System.out.print("请输入新班级ID：");
            int classId = Integer.parseInt(scanner.nextLine().trim());
            Student s = new Student(studentNo, name, gender, birthDate, classId);
            classService.updateSudent(s);
            System.out.println("修改成功");
        } catch (DateTimeParseException e) {
            System.out.println("出生日期格式错误，应为 yyyy-MM-dd");
        } catch (NumberFormatException e) {
            System.out.println("班级ID必须为整数");
        } catch (IllegalArgumentException e) {
            System.out.println("输入有误：" + e.getMessage());
        } catch (IOException e) {
            System.out.println("修改失败：" + e.getMessage());
        }
    }

    private static void deleteStudent() {
        try {
            System.out.print("请输入要删除的学号：");
            String studentNo = scanner.nextLine().trim();
            classService.deleteStudent(studentNo);
            System.out.println("删除成功");
        } catch (IOException e) {
            System.out.println("删除失败：" + e.getMessage());
        }
    }

    private static void listClasses() {
        try {
            List<ClassInfo> classes = classService.getAllClass();
            if (classes.isEmpty()) {
                System.out.println("暂无班级");
                return;
            }
            System.out.printf("%-6s %-10s%n", "ID", "班级名称");
            for (ClassInfo c : classes) {
                System.out.printf("%-6d %-10s%n", c.getId(), c.getClassName());
            }
        } catch (IOException e) {
            System.out.println("读取班级失败：" + e.getMessage());
        }
    }

}
