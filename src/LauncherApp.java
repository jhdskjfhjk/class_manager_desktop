import main.java.com.classmanager.entity.ClassInfo;
import main.java.com.classmanager.entity.Student;
import main.java.com.classmanager.service.ClassService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * 班级管理系统 GUI 启动器（美化版）。
 * 基于 Swing + Nimbus 外观，零外部依赖，JDK 自带即可一键运行。
 * 运行方式：右键此类 -> Run 'LauncherApp.main()'，或双击 run.bat。
 */
public class LauncherApp {

    // 配色方案（现代靛紫风）
    private static final Color PRIMARY = new Color(0x6366F1);
    private static final Color PRIMARY_LIGHT = new Color(0xE0E7FF);
    private static final Color BG = new Color(0xF5F7FA);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT = new Color(0x1F2937);
    private static final Color SUBTEXT = new Color(0x6B7280);
    private static final Color BORDER = new Color(0xE5E7EB);
    private static final Color STRIPE = new Color(0xF9FAFB);
    private static final Color DANGER = new Color(0xEF4444);

    private static final Font FONT = new Font("Microsoft YaHei", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("Microsoft YaHei", Font.BOLD, 13);
    private static final Font FONT_TITLE = new Font("Microsoft YaHei", Font.BOLD, 22);

    private static final ClassService classService = new ClassService();

    private static JFrame frame;
    private static DefaultTableModel tableModel;
    private static JTable table;
    private static JLabel statusLabel;

    public static void main(String[] args) {
        setupLookAndFeel();
        try {
            classService.initClasses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "初始化班级失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
        SwingUtilities.invokeLater(LauncherApp::createAndShowGUI);
    }

    private static void setupLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }
        UIManager.put("control", BG);
        UIManager.put("nimbusBase", PRIMARY);
        UIManager.put("nimbusBlueGrey", PRIMARY);
        UIManager.put("nimbusLightBackground", CARD);
        UIManager.put("text", TEXT);
        UIManager.put("OptionPane.background", CARD);
        UIManager.put("Panel.background", CARD);
        UIManager.put("ComboBox.background", CARD);
    }

    private static void createAndShowGUI() {
        frame = new JFrame("班级管理系统");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(960, 620);
        frame.setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);
        root.setBorder(new EmptyBorder(16, 16, 16, 16));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createTablePane(), BorderLayout.CENTER);
        root.add(createStatusBar(), BorderLayout.SOUTH);

        frame.setContentPane(root);
        refreshTable();
        frame.setVisible(true);
    }

    private static JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("班级管理系统");
        title.setFont(FONT_TITLE);
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Class Manager · 学生信息管理");
        subtitle.setFont(FONT);
        subtitle.setForeground(SUBTEXT);
        subtitle.setBorder(new EmptyBorder(2, 0, 0, 0));

        JPanel titleBox = new JPanel();
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setOpaque(false);
        JButton refreshBtn = makeSecondaryButton("刷新");
        JButton addBtn = makePrimaryButton("＋ 添加学生");
        JButton editBtn = makeSecondaryButton("修改");
        JButton deleteBtn = makeDangerButton("删除");
        JButton classesBtn = makeSecondaryButton("班级列表");
        toolbar.add(refreshBtn);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        toolbar.add(classesBtn);

        header.add(titleBox, BorderLayout.WEST);
        header.add(toolbar, BorderLayout.EAST);
        header.setBorder(new EmptyBorder(0, 4, 14, 4));

        refreshBtn.addActionListener(e -> refreshTable());
        addBtn.addActionListener(e -> showStudentDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                toast("请先在表格中选择一行", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            showStudentDialog((String) tableModel.getValueAt(row, 0));
        });
        deleteBtn.addActionListener(e -> deleteSelected());
        classesBtn.addActionListener(e -> showClassesDialog());

        return header;
    }

    private static JScrollPane createTablePane() {
        String[] columns = {"学号", "姓名", "性别", "出生日期", "班级"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(CARD);
        table.setSelectionBackground(PRIMARY_LIGHT);
        table.setSelectionForeground(TEXT);
        table.setFont(FONT);
        table.setForeground(TEXT);

        // 斑马条纹 + 内边距
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    setBackground(PRIMARY_LIGHT);
                    setForeground(TEXT);
                } else {
                    setBackground(row % 2 == 0 ? CARD : STRIPE);
                    setForeground(TEXT);
                }
                setFont(FONT);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        });

        // 表头美化
        JTableHeader header = table.getTableHeader();
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setPreferredSize(new Dimension(100, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBackground(PRIMARY);
                setForeground(Color.WHITE);
                setFont(FONT_BOLD);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setHorizontalAlignment(SwingConstants.LEFT);
                return this;
            }
        });

        // 双击编辑
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        showStudentDialog((String) tableModel.getValueAt(row, 0));
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollPane.getViewport().setBackground(CARD);
        return scrollPane;
    }

    private static JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(10, 4, 0, 4));
        statusLabel = new JLabel("加载中…");
        statusLabel.setFont(FONT);
        statusLabel.setForeground(SUBTEXT);
        bar.add(statusLabel, BorderLayout.WEST);
        JLabel hint = new JLabel("提示：双击行可快速编辑");
        hint.setFont(FONT);
        hint.setForeground(SUBTEXT);
        bar.add(hint, BorderLayout.EAST);
        return bar;
    }

    private static void refreshTable() {
        tableModel.setRowCount(0);
        int count = 0;
        try {
            List<Student> students = classService.getAllStudents();
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                        s.getStudentNo(),
                        s.getName(),
                        s.getGender(),
                        s.getBirthDate() != null ? s.getBirthDate().toString() : "",
                        s.getClassName() != null ? s.getClassName() : ""
                });
                count++;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "读取学生列表失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
        statusLabel.setText("共 " + count + " 名学生");
    }

    private static void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            toast("请先在表格中选择一行", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String studentNo = (String) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(frame,
                "确定删除学生 " + studentNo + " " + name + " 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            classService.deleteStudent(studentNo);
            refreshTable();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "删除失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void showStudentDialog(String existingStudentNo) {
        boolean isEdit = existingStudentNo != null;
        String title = isEdit ? "修改学生" : "添加学生";

        JTextField studentNoField = styleField(new JTextField(22));
        JTextField nameField = styleField(new JTextField(22));
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"男", "女"});
        styleComboBox(genderBox);
        JTextField birthField = styleField(new JTextField(22));
        birthField.setToolTipText("格式：yyyy-MM-dd，可留空");
        JComboBox<ClassInfo> classBox = new JComboBox<>();
        styleComboBox(classBox);

        try {
            for (ClassInfo c : classService.getAllClass()) {
                classBox.addItem(c);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "加载班级失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (classBox.getItemCount() == 0) {
            toast("班级列表为空，请先初始化班级", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isEdit) {
            try {
                Student target = null;
                for (Student s : classService.getAllStudents()) {
                    if (s.getStudentNo().equals(existingStudentNo)) {
                        target = s;
                        break;
                    }
                }
                if (target == null) {
                    JOptionPane.showMessageDialog(frame, "未找到该学生",
                            "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                studentNoField.setText(target.getStudentNo());
                studentNoField.setEditable(false);
                studentNoField.setBackground(STRIPE);
                nameField.setText(target.getName());
                if (target.getGender() != null) {
                    genderBox.setSelectedItem(target.getGender());
                }
                birthField.setText(target.getBirthDate() != null ? target.getBirthDate().toString() : "");
                for (int i = 0; i < classBox.getItemCount(); i++) {
                    if (classBox.getItemAt(i).getId() == target.getClassId()) {
                        classBox.setSelectedIndex(i);
                        break;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "读取学生信息失败：" + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD);
        panel.setBorder(new EmptyBorder(14, 14, 6, 14));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, gbc, "学号", studentNoField, 0);
        addField(panel, gbc, "姓名", nameField, 1);
        addField(panel, gbc, "性别", genderBox, 2);
        addField(panel, gbc, "出生日期", birthField, 3);
        addField(panel, gbc, "班级", classBox, 4);

        while (true) {
            int result = JOptionPane.showConfirmDialog(frame, panel, title,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            String studentNo = studentNoField.getText().trim();
            String name = nameField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            String birthText = birthField.getText().trim();
            ClassInfo selected = (ClassInfo) classBox.getSelectedItem();

            if (studentNo.isEmpty()) {
                toast("学号不能为空", JOptionPane.WARNING_MESSAGE);
                continue;
            }
            if (name.isEmpty()) {
                toast("姓名不能为空", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            LocalDate birthDate = null;
            if (!birthText.isEmpty()) {
                try {
                    birthDate = LocalDate.parse(birthText);
                } catch (DateTimeParseException ex) {
                    toast("出生日期格式错误，应为 yyyy-MM-dd", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
            }

            Student s = new Student(studentNo, name, gender, birthDate, selected.getId());
            try {
                if (isEdit) {
                    classService.updateSudent(s);
                } else {
                    classService.addStudent(s);
                }
                refreshTable();
                return;
            } catch (IllegalArgumentException e) {
                toast(e.getMessage(), JOptionPane.WARNING_MESSAGE);
                continue;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "保存失败：" + e.getMessage(),
                        "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    private static void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT);
        lbl.setForeground(SUBTEXT);
        panel.add(lbl, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private static void showClassesDialog() {
        String[] columns = {"ID", "班级名称"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            for (ClassInfo c : classService.getAllClass()) {
                model.addRow(new Object[]{c.getId(), c.getClassName()});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "读取班级列表失败：" + e.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JTable classTable = new JTable(model);
        classTable.setRowHeight(32);
        classTable.setShowGrid(false);
        classTable.setIntercellSpacing(new Dimension(0, 0));
        classTable.setBackground(CARD);
        classTable.setFont(FONT);
        classTable.setForeground(TEXT);
        classTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBackground(row % 2 == 0 ? CARD : STRIPE);
                setForeground(TEXT);
                setFont(FONT);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        });
        JTableHeader header = classTable.getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(100, 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                setBackground(PRIMARY);
                setForeground(Color.WHITE);
                setFont(FONT_BOLD);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setPreferredSize(new Dimension(420, 240));
        scrollPane.getViewport().setBackground(CARD);
        JOptionPane.showMessageDialog(frame, scrollPane, "班级列表",
                JOptionPane.PLAIN_MESSAGE);
    }

    // ====== 样式工具 ======

    private static <T extends JTextField> T styleField(T field) {
        field.setFont(FONT);
        field.setForeground(TEXT);
        field.setBackground(CARD);
        field.setCaretColor(TEXT);
        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        field.setPreferredSize(new Dimension(240, 32));
        return field;
    }

    private static <T> JComboBox<T> styleComboBox(JComboBox<T> box) {
        box.setFont(FONT);
        box.setBackground(CARD);
        box.setForeground(TEXT);
        box.setPreferredSize(new Dimension(240, 32));
        return box;
    }

    private static JButton makePrimaryButton(String text) {
        return makeButton(text, PRIMARY, Color.WHITE);
    }

    private static JButton makeDangerButton(String text) {
        return makeButton(text, DANGER, Color.WHITE);
    }

    private static JButton makeSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT);
        btn.setBackground(CARD);
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(true);
        btn.setBorder(new LineBorder(BORDER, 1));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(96, 34));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(STRIPE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(CARD);
            }
        });
        return btn;
    }

    private static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 34));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private static void toast(String msg, int type) {
        JOptionPane.showMessageDialog(frame, msg, "提示", type);
    }
}
