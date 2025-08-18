package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class RelatorioFrame extends JFrame {

    public RelatorioFrame() {
        setTitle("Gerar Relatórios");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton servicoBtn = new JButton("📋 Relatório de Serviços");
        JButton vendasBtn = new JButton("🛒 Relatório de Vendas");
        JButton limparBtn  = new JButton("🧹 Limpar Histórico");

        servicoBtn.addActionListener(e -> new RelatorioServicoFrame());
        vendasBtn.addActionListener(e -> new RelatorioVendaFrame());
        limparBtn.addActionListener(e -> confirmarLimpeza());

        add(servicoBtn);
        add(vendasBtn);
        add(limparBtn);

        setVisible(true);
    }

    private void confirmarLimpeza() {
        // 1) exige usuário logado
        Usuario logado = UsuarioLogado.getUsuario();
        if (logado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado. Faça login novamente.");
            return;
        }

        // (opcional) valida perfil ADMIN, se houver getPerfil()
        try {
            java.lang.reflect.Method m = logado.getClass().getMethod("getPerfil");
            Object perfil = m.invoke(logado);
            if (perfil == null || !"ADMIN".equalsIgnoreCase(perfil.toString())) {
                JOptionPane.showMessageDialog(this, "Apenas administradores podem limpar o histórico.");
                return;
            }
        } catch (NoSuchMethodException ignore) {
            // se não houver campo de perfil, ignora
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 2) prompt de senha mostrando quem confirma
        JPasswordField senhaField = new JPasswordField();
        JPanel p = new JPanel(new GridLayout(2, 1, 6, 6));
        p.add(new JLabel("Confirmar como: " + logado.getLogin()));
        p.add(senhaField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Digite sua senha para confirmar a limpeza",
                JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) return;

        String senhaDigitada = new String(senhaField.getPassword());
        java.util.Arrays.fill(senhaField.getPassword(), '\0');

        // 3) recarrega o usuário do banco (para pegar o hash atualizado)
        Usuario usuarioBanco = buscarUsuarioPorLogin(logado.getLogin());
        if (usuarioBanco == null || usuarioBanco.getSenha() == null || usuarioBanco.getSenha().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não foi possível validar suas credenciais.");
            return;
        }

        String hashBanco = usuarioBanco.getSenha();

        // DEBUG opcional
        String shaCalc = SegurancaUtil.sha256Hex(senhaDigitada);
        System.out.println("Auth(limpeza) -> login=" + usuarioBanco.getLogin()
                + ", hashLen=" + (hashBanco == null ? 0 : hashBanco.length())
                + ", hashPrefix=" + (hashBanco == null ? "<null>" :
                    (hashBanco.length() >= 7 ? hashBanco.substring(0, 7) : hashBanco)));
        System.out.println("Auth(limpeza) calc -> shaCalc=" + shaCalc);

        // 4) valida senha (SHA-256 hex)
        boolean ok = SegurancaUtil.verificarSenha(senhaDigitada, hashBanco);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Senha incorreta.");
            return;
        }

        // 5) executa limpeza
        try {
            VendaService.limparHistorico();
            ServicoService.limparHistorico();
            JOptionPane.showMessageDialog(this, "Histórico limpo com sucesso!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Falha ao limpar o histórico.");
        }
    }

    /** Busca o usuário por login para validar a senha sempre com o hash atual do banco. */
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
