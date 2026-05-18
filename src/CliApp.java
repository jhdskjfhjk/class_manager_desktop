import main.java.com.classmanager.entity.ClassInfo;
import main.java.com.classmanager.entity.Student;
import main.java.com.classmanager.service.ClassService;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class CliApp {
    private static ClassService classService = new ClassService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
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
                case "4": deleteStudents(); break;
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

    //查看所有学生

    private static void listStudents () throws IOException {
        List<Student> students = classService.getAllStudents();
        try {
            students = classService.getAllStudents();
            if (students.isEmpty()) {
                System.out.println("暂无学生记录");
                return;
            }
            System.out.println("\n学号\t\t姓名\t性别\t出生日期\t\t班级");
            System.out.println("---------------------------------------------------");

            for (Student s : students) {
                System.out.printf(
                        "%s\t%s\t%s\t%s\t%s\n",
                        s.getStudentNo(),
                        s.getName(),
                        s.getGender(),
                        s.getBirthDate() != null ? s.getBirthDate() : "",
                        s.getClassName()
                );
            }
        }catch (IOException e) {
            System.out.println("读取学生数据失败:" + e.getMessage());
        }
    }

    //添加学生
    private static void addStudent() {
        try {
            System.out.print("学号：");
            String no = scanner.nextLine().trim();
            System.out.print("姓名：");
            String name = scanner.nextLine().trim();
            System.out.print("性别（男/女）：");
            String gender = scanner.nextLine().trim();
            System.out.print("出生日期（yyyy-MM-dd）：");
            String birthStr = scanner.nextLine().trim();
            LocalDate birth = null;
            if (!birthStr.isEmpty()) {
                birth = LocalDate.parse(birthStr);
            }
            // 显示班级列表供选择
            List<ClassInfo> classes = classService.getAllClass();
            if (classes.isEmpty()) {
                System.out.println("没有班级，请先添加班级。");
                return;
            }
            System.out.println("可选班级：");
            for (ClassInfo c : classes) {
                System.out.println(c.getId() + " - " + c.getClassName());
            }
            System.out.print("请选择班级ID：");
            int classId = Integer.parseInt(scanner.nextLine().trim());

            Student s = new Student(no, name, gender, birth, classId);
            classService.addStudent(s);
            System.out.println("添加成功！");
        } catch (DateTimeParseException e) {
            System.out.println("日期格式错误，请使用 yyyy-MM-dd 格式。");
        } catch (NumberFormatException e) {
            System.out.println("班级ID必须是数字。");
        } catch (IllegalArgumentException e) {
            System.out.println("输入错误：" + e.getMessage());
        } catch (IOException e) {
            System.out.println("添加失败：" + e.getMessage());
        }
    }

    //修改学生
    // 修改学生
    private static void editStudent() {
        try {
            System.out.print("请输入要修改的学生学号：");
            String no = scanner.nextLine().trim();
            // 查找该学生是否存在
            List<Student> all = classService.getAllStudents();
            Student target = all.stream()
                    .filter(s -> s.getStudentNo().equals(no))
                    .findFirst()
                    .orElse(null);
            if (target == null) {
                System.out.println("未找到该学号的学生。");
                return;
            }

            System.out.println("当前信息：");
            System.out.printf("学号：%s, 姓名：%s, 性别：%s, 出生日期：%s, 班级ID：%d\n",
                    target.getStudentNo(), target.getName(), target.getGender(),
                    target.getBirthDate(), target.getClassId());

            System.out.println("请输入新信息（直接回车保留原值）：");
            System.out.print("姓名[" + target.getName() + "]：");
            String name = scanner.nextLine().trim();
            if (!name.isEmpty()) target.setName(name);

            System.out.print("性别[" + target.getGender() + "]：");
            String gender = scanner.nextLine().trim();
            if (!gender.isEmpty()) target.setGender(gender);

            System.out.print("出生日期[" + (target.getBirthDate() != null ? target.getBirthDate() : "") + "]：");
            String birthStr = scanner.nextLine().trim();
            if (!birthStr.isEmpty()) {
                target.setBirthDate(LocalDate.parse(birthStr));
            }

            // 班级修改
            List<ClassInfo> classes = classService.getAllClass();
            System.out.println("可选班级：");
            for (ClassInfo c : classes) {
                System.out.println(c.getId() + " - " + c.getClassName());
            }
            System.out.print("班级ID[" + target.getClassId() + "]：");
            String classIdStr = scanner.nextLine().trim();
            if (!classIdStr.isEmpty()) {
                target.setClassId(Integer.parseInt(classIdStr));
            }

            classService.updateSudent(target);
            System.out.println("修改成功！");
        } catch (DateTimeParseException e) {
            System.out.println("日期格式错误。");
        } catch (NumberFormatException e) {
            System.out.println("班级ID必须为数字。");
        } catch (IOException e) {
            System.out.println("修改失败：" + e.getMessage());
        }
    }


    //删除学生

    private static void deleteStudents () {//删除学生
        try {
            System.out.println("请输入要删除的学生学号:");
            String no = scanner.next().trim();
            //确认

            System.out.print("确定要删除的学号为" + no + "的学生吗？(y/n):");
            String confirm = scanner. nextLine().trim();
            if("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {//判断是不是yes or y
                classService.deleteStudent(no);
                System.out.println("删除成功。");
            } else {
                System.out.println("已取消删除。");
            }
        }catch (IOException e) {
            System.out.println("删除失败:" + e.getMessage());
        }
    }


    // 查看班级列表
    private static void listClasses() {
        try {
            List<ClassInfo> classes = classService.getAllClass();
            if (classes.isEmpty()) {
                System.out.println("暂无班级。");
                return;
            }
            System.out.println("\n班级ID\t班级名称");
            for (ClassInfo c : classes) {
                System.out.println(c.getId() + "\t" + c.getClassName());
            }
        } catch (IOException e) {
            System.out.println("读取班级数据失败：" + e.getMessage());
        }
    }




}
