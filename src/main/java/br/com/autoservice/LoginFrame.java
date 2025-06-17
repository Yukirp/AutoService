package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.hibernate.Session;
import org.hibernate.query.Query;
import br.com.autoservice.Usuario;


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
        panel.add(new JLabel()); // espaço vazio
        panel.add(entrarButton);

        add(panel);

        entrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });

        setVisible(true);
    }

    private void autenticar() {
        String login = loginField.getText();
        String senha = new String(senhaField.getPassword());

        // Verifica se é o login universal
       if (login.equals("admin") && senha.equals("root135792468@")) {
            Usuario usuarioAdmin = new Usuario("admin", SegurancaUtil.criptografar("root135792468@"));
            UsuarioLogado.setUsuario(usuarioAdmin); // agora sim, sem erro
            new PrincipalFrame();
            dispose();
            return;
    }


        // Caso contrário, verifica no banco com a senha criptografada
        String senhaCriptografada = SegurancaUtil.criptografar(senha);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE login = :login AND senha = :senha", Usuario.class);
            query.setParameter("login", login);
            query.setParameter("senha", senhaCriptografada);
            Usuario usuario = query.uniqueResult();

            if (usuario != null) {
                UsuarioLogado.setUsuario(usuario);
                new PrincipalFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login ou senha inválidos.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao tentar autenticar.");
        }
    }
}
