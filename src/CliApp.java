import main.java.com.classmanager.service.ClassService;

import java.util.Scanner;

public class CliApp {
    private static ClassService classService = new ClassService();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try{
            classService.initClasses();
        }
    }


}
