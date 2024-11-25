// EmployeeManagementSystem.java
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

class EmployeeManagementSystem extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField idField, nameField, ageField, salaryField;
    private JButton addButton, updateButton, deleteButton, clearButton;

    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/mydb2";
    private static final String USER = "root";
    private static final String PASSWORD = "0000";


    public EmployeeManagementSystem() {
        setTitle("员工管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 创建表格
        String[] columns = {"ID", "姓名", "年龄", "薪资"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 创建输入面板
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 50, 5));

        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("姓名:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("年龄:"));
        ageField = new JTextField();
        inputPanel.add(ageField);

        inputPanel.add(new JLabel("薪资:"));
        salaryField = new JTextField();
        inputPanel.add(salaryField);

        // 创建按钮
        addButton = new JButton("添加");
        updateButton = new JButton("更新");
        deleteButton = new JButton("删除");
        clearButton = new JButton("清空");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        inputPanel.add(buttonPanel);
        add(inputPanel, BorderLayout.SOUTH);

        // 添加事件监听器
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearFields());

        // 表格选择监听器
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    idField.setText(table.getValueAt(selectedRow, 0).toString());
                    nameField.setText(table.getValueAt(selectedRow, 1).toString());
                    ageField.setText(table.getValueAt(selectedRow, 2).toString());
                    salaryField.setText(table.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        // 加载数据
        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Employee")) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getDouble("salary")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            showError("加载数据失败: " + e.getMessage());
        }
    }

    private void addEmployee() {
        try {
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            double salary = Double.parseDouble(salaryField.getText());

            String sql = "INSERT INTO Employee (name, age, salary) VALUES (?, ?, ?)";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, name);
                pstmt.setInt(2, age);
                pstmt.setDouble(3, salary);
                pstmt.executeUpdate();

                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "员工添加成功！");
            }
        } catch (Exception e) {
            showError("添加失败: " + e.getMessage());
        }
    }

    private void updateEmployee() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            int age = Integer.parseInt(ageField.getText());
            double salary = Double.parseDouble(salaryField.getText());

            String sql = "UPDATE Employee SET name=?, age=?, salary=? WHERE id=?";
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, name);
                pstmt.setInt(2, age);
                pstmt.setDouble(3, salary);
                pstmt.setInt(4, id);
                pstmt.executeUpdate();

                loadData();
                clearFields();
                JOptionPane.showMessageDialog(this, "员工信息更新成功！");
            }
        } catch (Exception e) {
            showError("更新失败: " + e.getMessage());
        }
    }

    private void deleteEmployee() {
        try {
            int id = Integer.parseInt(idField.getText());

            int confirm = JOptionPane.showConfirmDialog(this,
                    "确定要删除这条记录吗？", "确认删除",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM Employee WHERE id=?";
                try (Connection conn = getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();

                    loadData();
                    clearFields();
                    JOptionPane.showMessageDialog(this, "员工记录删除成功！");
                }
            }
        } catch (Exception e) {
            showError("删除失败: " + e.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        salaryField.setText("");
        table.clearSelection();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "错误",
                JOptionPane.ERROR_MESSAGE);
    }
}