package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.text.SimpleDateFormat;

public class RelatorioServicoFrame extends JFrame {
    private JTextArea textoArea;

    public RelatorioServicoFrame() {
        setTitle("🛠️ Relatório de Serviços");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Título
        JLabel titulo = new JLabel("📋 Relatório de Serviços", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        // Área de texto com rolagem
        textoArea = new JTextArea();
        textoArea.setEditable(false);
        textoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textoArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textoArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Histórico de Serviços"));
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton exportarBtn = new JButton("📤 Exportar PDF");
        JButton limparBtn = new JButton("🧹 Limpar Histórico");
        painelBotoes.add(exportarBtn);
        painelBotoes.add(limparBtn);
        add(painelBotoes, BorderLayout.SOUTH);

        exportarBtn.addActionListener((ActionEvent e) -> exportarPDF());
        limparBtn.addActionListener((ActionEvent e) -> limparHistorico());

        carregarServicos();
        setVisible(true);
    }

    private void carregarServicos() {
        List<Servico> servicos = ServicoService.listarServicos();
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        if (servicos.isEmpty()) {
            sb.append("Nenhum serviço registrado.");
        } else {
            for (Servico s : servicos) {
                sb.append("👤 Cliente: ").append(s.getCarro().getCliente()).append("\n")
                  .append("🚗 Carro: ").append(s.getCarro().getMarca()).append(" ").append(s.getCarro().getModelo()).append(" (").append(s.getCarro().getPlaca()).append(")").append("\n")
                  .append("📅 Data: ").append(s.getData() != null ? sdf.format(s.getData()) : "Data não registrada").append("\n")
                  .append("📏 Quilometragem: ").append(s.getQuilometragem()).append(" km\n")
                  .append("🔧 Descrição: ").append(s.getDescricao()).append("\n")
                  .append("💰 Valor: R$ ").append(String.format("%.2f", s.getValor())).append("\n")
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
            String senhaDigitada = new String(senhaField.getPassword());
            String senhaDigitadaCriptografada = SegurancaUtil.criptografar(senhaDigitada);
            String senhaAdmCriptografada = "2f2ff0ec875a9bfa234009ac5f3f2109"; // hash MD5 da senha admin

            if (senhaDigitadaCriptografada.equals(senhaAdmCriptografada)) {
                ServicoService.limparServicos();
                textoArea.setText("");
                JOptionPane.showMessageDialog(this, "✅ Histórico de serviços limpo.");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Senha incorreta.");
            }
        }
    }
}
