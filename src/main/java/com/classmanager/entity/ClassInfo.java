package main.java.com.classmanager.entity;
//班级实体 :: 一个班级的信息，只有 id 和 className。
public class ClassInfo {
    private int id;
    private String className;

    public ClassInfo (int id, String className) {
        this.id = id;
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
