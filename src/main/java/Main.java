import javax.swing.*;

public static void main(String[] args) {
    // 注册MySQL驱动
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
        System.err.println("找不到MySQL JDBC驱动程序");
        e.printStackTrace();
        return;
    }
    new EmployeeManagementSystem().setVisible(true);
}