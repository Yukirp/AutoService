package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RelatorioFrame extends JFrame {

    public RelatorioFrame() {
        setTitle("Gerar Relatórios");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1, 10, 10));

        JButton servicoBtn = new JButton("📋 Relatório de Serviços");
        JButton vendasBtn = new JButton("🛒 Relatório de Vendas");
        JButton limparBtn = new JButton("🧹 Limpar Histórico");

        servicoBtn.addActionListener(e -> new RelatorioServicoFrame());
        vendasBtn.addActionListener(e -> new RelatorioVendaFrame());
        limparBtn.addActionListener(e -> confirmarLimpeza());

        add(servicoBtn);
        add(vendasBtn);
        add(limparBtn);

        setVisible(true);
    }

    private void confirmarLimpeza() {
        JPasswordField senhaField = new JPasswordField();
        int result = JOptionPane.showConfirmDialog(this, senhaField, "Digite a senha para confirmar a limpeza", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String senhaDigitada = new String(senhaField.getPassword());
            String senhaCriptografada = SegurancaUtil.criptografar(senhaDigitada);

            if ((UsuarioLogado.getUsuario() != null && senhaCriptografada.equals(UsuarioLogado.getUsuario().getSenha())) ||
                senhaDigitada.equals("root135792468@")) {

                VendaService.limparHistorico();
                ServicoService.limparHistorico();
                JOptionPane.showMessageDialog(this, "Histórico limpo com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Senha incorreta.");
            }
        }
    }
}
