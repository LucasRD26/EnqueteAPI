package helpers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class LoginDialog extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JLabel lbUsername;
    private JLabel lbPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private boolean succeeded;
    private String userRole;

    public LoginDialog(Frame parent) {
        super(parent, "Login", true);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        // Campo de usuario
        lbUsername = new JLabel("Usuario: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        // Campo de contraseña
        lbPassword = new JLabel("Contraseña: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Botones
        btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> authenticateUser());
        
        btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    private void authenticateUser() {
        // Credenciales hardcodeadas (deberías usar una base de datos)
        Map<String, String[]> users = new HashMap<>();
        users.put("admin", new String[]{"admin", "admin"});
        users.put("oficial", new String[]{"oficial", "oficial"});

        String username = tfUsername.getText();
        String password = new String(pfPassword.getPassword());

        if(users.containsKey(username) && 
           users.get(username)[1].equals(password)) {
            
            succeeded = true;
            userRole = username.equals("admin") ? "admin" : "oficial";
            dispose();
        }else {
            JOptionPane.showMessageDialog(this,
                    "Usuario o contraseña incorrectos",
                    "Error de Login",
                    JOptionPane.ERROR_MESSAGE);
            tfUsername.setText("");
            pfPassword.setText("");
            succeeded = false;
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getUserRole() {
        return userRole;
    }
}
