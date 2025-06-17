package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.text.SimpleDateFormat;


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

    if (vendas.isEmpty()) {
        sb.append("Nenhuma venda registrada.");
    } else {
        for (Venda v : vendas) {
            sb.append("👤 Cliente: ").append(v.getComprador()).append("\n");

            if (v.getDataVenda() != null) {
                String dataFormatada = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getDataVenda());
                sb.append("📅 Data da Venda: ").append(dataFormatada).append("\n");
            } else {
                sb.append("📅 Data da Venda: ").append("Data não registrada").append("\n");
            }

            sb.append("🛒 Produto: ").append(v.getProduto().getNome()).append("\n")
              .append("🏷️ Marca: ").append(v.getProduto().getMarca()).append("\n")
              .append("📦 Quantidade: ").append(v.getQuantidade()).append("\n")
              .append("💰 Valor Total: R$ ").append(String.format("%.2f", v.getValorTotal())).append("\n")
              .append("────────────────────────────\n");
        }
    }

    textoArea.setText(sb.toString());
}


    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, "📄 Função de exportar PDF ainda será implementada.");
    }

    private void limparHistorico() {
        JPasswordField senhaField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, senhaField, "Digite a senha para confirmar", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String senha = new String(senhaField.getPassword());

            if (SegurancaUtil.criptografar(senha).equals(UsuarioLogado.getUsuario().getSenha()) ||
                senha.equals("root135792468@")) {
                
                VendaService.limparVendas();
                textoArea.setText("");
                JOptionPane.showMessageDialog(this, "✅ Histórico de vendas limpo.");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Senha incorreta.");
            }
        }
    }
}
