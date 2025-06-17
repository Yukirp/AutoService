package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.hibernate.Session;

public class CadastroUsuarioFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField senhaField;

    public CadastroUsuarioFrame() {
        setTitle("Cadastro de Usuário");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel loginLabel = new JLabel("Novo Login:");
        loginField = new JTextField();

        JLabel senhaLabel = new JLabel("Nova Senha:");
        senhaField = new JPasswordField();

        JButton cadastrarButton = new JButton("Cadastrar");

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(senhaLabel);
        panel.add(senhaField);
        panel.add(new JLabel());
        panel.add(cadastrarButton);

        add(panel);

        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });

        setVisible(true);
    }

    private void cadastrarUsuario() {
        String login = loginField.getText();
        String senha = new String(senhaField.getPassword());

        if (login.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        String senhaCriptografada = SegurancaUtil.criptografar(senha);

        Usuario novoUsuario = new Usuario(login, senhaCriptografada);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(novoUsuario);
            session.getTransaction().commit();
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            new LoginFrame(); // redireciona para login
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário.");
        }
    }
}
