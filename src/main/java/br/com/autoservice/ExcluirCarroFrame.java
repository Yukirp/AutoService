package br.com.autoservice;

import javax.swing.*;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.io.File;
import java.util.List;

public class ExcluirCarroFrame extends JFrame {

    private JTextField placaField;

    public ExcluirCarroFrame() {
        setTitle("Excluir Carro");
        setSize(350, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel placaLabel = new JLabel("Placa do carro:");
        placaLabel.setBounds(20, 20, 120, 25);
        placaField = new JTextField();
        placaField.setBounds(140, 20, 150, 25);

        JButton excluirBtn = new JButton("Excluir");
        excluirBtn.setBounds(110, 60, 120, 30);

        add(placaLabel);
        add(placaField);
        add(excluirBtn);

        excluirBtn.addActionListener(e -> excluirCarro());

        setVisible(true);
    }

    private void excluirCarro() {
        String placa = placaField.getText().trim();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe a placa do carro.");
            return;
        }

        // exige usuário logado
        Usuario usuarioLogado = UsuarioLogado.getUsuario();
        if (usuarioLogado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado. Faça login novamente.");
            return;
        }

        // (opcional) valida perfil ADMIN se houver getPerfil()
        try {
            java.lang.reflect.Method m = usuarioLogado.getClass().getMethod("getPerfil");
            Object perfil = m.invoke(usuarioLogado);
            if (perfil == null || !"ADMIN".equalsIgnoreCase(perfil.toString())) {
                JOptionPane.showMessageDialog(this, "Apenas administradores podem excluir carros.");
                return;
            }
        } catch (NoSuchMethodException ignore) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Carro carro = CarroService.buscarCarroPorPlaca(placa);
        if (carro == null) {
            JOptionPane.showMessageDialog(this, "Carro não encontrado.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir o carro:\n" +
            "Placa: " + carro.getPlaca() + "\nModelo: " + carro.getModelo() + "\nCliente: " + carro.getCliente(),
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        // pede a senha do PRÓPRIO usuário logado e mostra quem está confirmando
        JPasswordField passwordField = new JPasswordField();
        JPanel p = new JPanel(new java.awt.GridLayout(2, 1, 6, 6));
        p.add(new JLabel("Confirmar como: " + usuarioLogado.getLogin()));
        p.add(passwordField);
        int result = JOptionPane.showConfirmDialog(
            this, p, "Digite sua senha (administrador):", JOptionPane.OK_CANCEL_OPTION
        );
        if (result != JOptionPane.OK_OPTION) return;

        char[] senhaChars = passwordField.getPassword();
        String senhaDigitada = new String(senhaChars);
        java.util.Arrays.fill(senhaChars, '\0');

        // recarrega o usuário do banco e verifica com SHA-256
        Usuario usuarioParaValidar = buscarUsuarioPorLogin(usuarioLogado.getLogin());
        if (usuarioParaValidar == null || usuarioParaValidar.getSenha() == null || usuarioParaValidar.getSenha().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não foi possível carregar seus dados de autenticação.");
            return;
        }

        String hashBanco = usuarioParaValidar.getSenha(); // coluna senhaHash
        // debug útil
        String shaCalc = SegurancaUtil.sha256Hex(senhaDigitada);
        System.out.println("Auth debug -> login=" + usuarioParaValidar.getLogin()
                + ", hashLen=" + (hashBanco == null ? 0 : hashBanco.length())
                + ", hashPrefix=" + (hashBanco == null ? "<null>" :
                    (hashBanco.length() >= 7 ? hashBanco.substring(0,7) : hashBanco))
        );
        System.out.println("Auth calc  -> shaCalc=" + shaCalc + " (len=" + shaCalc.length() + ")");

        boolean ok = SegurancaUtil.verificarSenha(senhaDigitada, hashBanco);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Senha incorreta.");
            return;
        }

        // senha válida → executa exclusão + relatório
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Carro carroManaged = session.get(Carro.class, carro.getId());
            if (carroManaged == null) {
                JOptionPane.showMessageDialog(this, "Carro não encontrado na base de dados.");
                session.getTransaction().rollback();
                return;
            }

            // ajuste a entidade se for ServicoRealizado
            Query<Servico> query = session.createQuery(
                "FROM Servico s WHERE s.carro.id = :carroId", Servico.class
            );
            query.setParameter("carroId", carroManaged.getId());
            List<Servico> servicos = query.list();

            File pdf = RelatorioPDF.gerarRelatorioServicos(carroManaged, servicos);
            System.out.println("Relatório salvo em: " + pdf.getAbsolutePath());

            for (Servico s : servicos) {
                session.delete(s);
            }

            session.delete(carroManaged);
            session.getTransaction().commit();

            JOptionPane.showMessageDialog(this, "Carro e serviços excluídos com sucesso.");
            dispose();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao excluir o carro e serviços.");
        }
    }

    private Usuario buscarUsuarioPorLogin(String login) {
        if (login == null || login.isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Usuario> q = session.createQuery(
                "FROM Usuario u WHERE u.login = :login", Usuario.class);
            q.setParameter("login", login);
            return q.uniqueResult();
        }
    }
}
