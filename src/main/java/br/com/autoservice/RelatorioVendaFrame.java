package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.text.SimpleDateFormat;

import org.hibernate.Session;
import org.hibernate.query.Query;

public class RelatorioVendaFrame extends JFrame {
    private JTextArea textoArea;

    public RelatorioVendaFrame() {
        setTitle("🧾 Relatório de Vendas");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Título estilizado
        JLabel titulo = new JLabel("📦 Relatório de Vendas", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        // Área de texto com borda e rolagem
        textoArea = new JTextArea();
        textoArea.setEditable(false);
        textoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textoArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Histórico de Vendas"));
        add(scrollPane, BorderLayout.CENTER);

        // Botões na parte inferior
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton exportarBtn = new JButton("📤 Exportar PDF");
        JButton limparBtn = new JButton("🧹 Limpar Histórico");
        painelBotoes.add(exportarBtn);
        painelBotoes.add(limparBtn);
        add(painelBotoes, BorderLayout.SOUTH);

        exportarBtn.addActionListener((ActionEvent e) -> exportarPDF());
        limparBtn.addActionListener((ActionEvent e) -> limparHistorico());

        carregarVendas();
        setVisible(true);
    }

    private void carregarVendas() {
        List<Venda> vendas = VendaService.listarVendas();
        StringBuilder sb = new StringBuilder();

        if (vendas == null || vendas.isEmpty()) {
            sb.append("Nenhuma venda registrada.");
        } else {
            for (Venda v : vendas) {
                sb.append("👤 Cliente: ").append(nullSafe(v.getComprador())).append("\n");

                if (v.getDataVenda() != null) {
                    String dataFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getDataVenda());
                    sb.append("📅 Data da Venda: ").append(dataFormatada).append("\n");
                } else {
                    sb.append("📅 Data da Venda: ").append("Data não registrada").append("\n");
                }

                Produto p = v.getProduto();
                sb.append("🛒 Produto: ").append(p != null ? nullSafe(p.getNome()) : "")
                  .append("\n")
                  .append("🏷️ Marca: ").append(p != null ? nullSafe(p.getMarca()) : "")
                  .append("\n")
                  .append("📦 Quantidade: ").append(v.getQuantidade())
                  .append("\n")
                  .append("💰 Valor Total: R$ ").append(String.format("%.2f", v.getValorTotal()))
                  .append("\n")
                  .append("────────────────────────────\n");
            }
        }

        textoArea.setText(sb.toString());
    }

    private String nullSafe(String s) { return s == null ? "" : s; }

    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, "📄 Função de exportar PDF ainda será implementada.");
    }

    private void limparHistorico() {
        // 1) exige usuário logado
        Usuario logado = UsuarioLogado.getUsuario();
        if (logado == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado. Faça login novamente.");
            return;
        }

        // (opcional) valida perfil ADMIN, se existir getPerfil()
        try {
            java.lang.reflect.Method m = logado.getClass().getMethod("getPerfil");
            Object perfil = m.invoke(logado);
            if (perfil == null || !"ADMIN".equalsIgnoreCase(perfil.toString())) {
                JOptionPane.showMessageDialog(this, "Apenas administradores podem limpar o histórico.");
                return;
            }
        } catch (NoSuchMethodException ignore) {
            // sem campo de perfil, ignora
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // 2) prompt de senha mostrando quem confirma
        JPasswordField senhaField = new JPasswordField();
        JPanel p = new JPanel(new GridLayout(2, 1, 6, 6));
        p.add(new JLabel("Confirmar como: " + logado.getLogin()));
        p.add(senhaField);

        int result = JOptionPane.showConfirmDialog(
                this, p, "Digite sua senha para confirmar", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String senhaDigitada = new String(senhaField.getPassword());
        java.util.Arrays.fill(senhaField.getPassword(), '\0');

        // 3) recarrega o usuário do banco (hash mais recente)
        Usuario usuarioBanco = buscarUsuarioPorLogin(logado.getLogin());
        if (usuarioBanco == null || usuarioBanco.getSenha() == null || usuarioBanco.getSenha().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não foi possível validar suas credenciais.");
            return;
        }
        String hashBanco = usuarioBanco.getSenha();

        // DEBUG opcional
        String shaCalc = SegurancaUtil.sha256Hex(senhaDigitada);
        System.out.println("Auth(vendas) -> login=" + usuarioBanco.getLogin()
                + ", hashLen=" + (hashBanco == null ? 0 : hashBanco.length())
                + ", hashPrefix=" + (hashBanco == null ? "<null>" :
                    (hashBanco.length() >= 7 ? hashBanco.substring(0, 7) : hashBanco)));
        System.out.println("Auth(vendas) calc -> shaCalc=" + shaCalc);

        // 4) valida senha (SHA-256 hex)
        boolean ok = SegurancaUtil.verificarSenha(senhaDigitada, hashBanco);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "❌ Senha incorreta.");
            return;
        }

        // 5) executa limpeza
        try {
            VendaService.limparVendas();
            textoArea.setText("");
            JOptionPane.showMessageDialog(this, "✅ Histórico de vendas limpo.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Falha ao limpar o histórico.");
        }
    }

    /** Busca o usuário por login para validar a senha com o hash atual do banco. */
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
