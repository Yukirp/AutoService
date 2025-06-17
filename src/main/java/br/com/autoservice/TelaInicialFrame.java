package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TelaInicialFrame extends JFrame {

    public TelaInicialFrame() {
        setTitle("AutoService - Bem-vindo");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton btnLogin = new JButton("Entrar");
        JButton btnCadastrar = new JButton("Cadastrar Usuário");

        btnLogin.addActionListener((ActionEvent e) -> {
            new LoginFrame();
            dispose();
        });

        btnCadastrar.addActionListener((ActionEvent e) -> {
            new CadastroUsuarioFrame();
            dispose();
        });

        panel.add(new JLabel("Bem-vindo ao AutoService", SwingConstants.CENTER));
        panel.add(btnLogin);
        panel.add(btnCadastrar);

        add(panel);
        setVisible(true);
    }
}
