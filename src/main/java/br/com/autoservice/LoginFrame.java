package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class LoginFrame extends JFrame {

    private JTextField loginField;
    private JPasswordField senhaField;

    public LoginFrame() {
        setTitle("AutoService - Login");
        setSize(350, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel loginLabel = new JLabel("Login:");
        loginField = new JTextField();

        JLabel senhaLabel = new JLabel("Senha:");
        senhaField = new JPasswordField();

        JButton entrarButton = new JButton("Entrar");

        panel.add(loginLabel);
        panel.add(loginField);
        panel.add(senhaLabel);
        panel.add(senhaField);
        panel.add(new JLabel()); // espaço
        panel.add(entrarButton);

        add(panel);

        entrarButton.addActionListener(e -> autenticar());

        setVisible(true);
    }

    private void autenticar() {
        String login = loginField.getText().trim();
        String senha = new String(senhaField.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha login e senha.");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // NÃO faça WHERE com senha — busque só por login
            Query<Usuario> q = session.createQuery(
                "FROM Usuario u WHERE u.login = :login", Usuario.class);
            q.setParameter("login", login);
            Usuario usuario = q.uniqueResult();

            if (usuario == null || usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos.");
                return;
            }

            String hashBanco = usuario.getSenha(); // mapeado para coluna senhaHash
            boolean ok = SegurancaUtil.verificarSenha(senha, hashBanco);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos.");
                return;
            }

            UsuarioLogado.setUsuario(usuario);
            new PrincipalFrame();
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao tentar autenticar.");
        }
    }
}
