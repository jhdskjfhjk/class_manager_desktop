import main.java.com.classmanager.service.ClassService;

import java.io.IOException;
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



}
