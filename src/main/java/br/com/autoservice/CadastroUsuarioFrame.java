package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class CadastroUsuarioFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField senhaField;

    public CadastroUsuarioFrame() {
        setTitle("Cadastro de Usuário");
        setSize(360, 210);
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

        cadastrarButton.addActionListener((ActionEvent e) -> cadastrarUsuario());

        setVisible(true);
    }

    private void cadastrarUsuario() {
        String login = loginField.getText() == null ? "" : loginField.getText().trim();
        char[] senhaArr = senhaField.getPassword();
        String senha = new String(senhaArr);
        java.util.Arrays.fill(senhaArr, '\0'); // limpa da memória

        // validações simples
        if (login.isEmpty() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha login e senha.");
            return;
        }
        if (login.length() < 3) {
            JOptionPane.showMessageDialog(this, "O login deve ter pelo menos 3 caracteres.");
            return;
        }
        if (senha.length() < 6) {
            JOptionPane.showMessageDialog(this, "A senha deve ter pelo menos 6 caracteres.");
            return;
        }

        // gera hash SHA-256 em hex minúsculo (64 chars)
        String senhaHash = SegurancaUtil.sha256Hex(senha);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // já existe login?
            Query<Long> q = session.createQuery(
                "select count(u.id) from Usuario u where u.login = :login", Long.class);
            q.setParameter("login", login);
            Long jaExiste = q.uniqueResult();
            if (jaExiste != null && jaExiste > 0) {
                JOptionPane.showMessageDialog(this, "Login já existe. Escolha outro.");
                return;
            }

            // salva
            Transaction tx = session.beginTransaction();
            Usuario novoUsuario = new Usuario(login, senhaHash); // Usuario(login, senhaHash)
            session.save(novoUsuario);
            tx.commit();

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            dispose();
            new LoginFrame(); // abre tela de login

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário.");
        }
    }
}
