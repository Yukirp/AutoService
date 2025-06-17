package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VenderProdutoFrame extends JFrame {

    private JTextField nomeField;
    private JTextField quantidadeField;
    private JTextField marcaField;
    private JTextField compradorField;

    public VenderProdutoFrame() {
        setTitle("Vender Produto");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel nomeLabel = new JLabel("Nome do Produto:");
        nomeLabel.setBounds(20, 20, 120, 25);
        nomeField = new JTextField();
        nomeField.setBounds(140, 20, 200, 25);

        JLabel marcaLabel = new JLabel("Marca:");
        marcaLabel.setBounds(20, 60, 120, 25);
        marcaField = new JTextField();
        marcaField.setBounds(140, 60, 200, 25);

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setBounds(20, 100, 120, 25);
        quantidadeField = new JTextField();
        quantidadeField.setBounds(140, 100, 200, 25);

        JLabel compradorLabel = new JLabel("Nome do Cliente:");
        compradorLabel.setBounds(20, 140, 120, 25);
        compradorField = new JTextField();
        compradorField.setBounds(140, 140, 200, 25);

        JButton venderBtn = new JButton("Vender");
        venderBtn.setBounds(140, 180, 120, 30);

        add(nomeLabel);
        add(nomeField);
        add(marcaLabel);
        add(marcaField);
        add(quantidadeLabel);
        add(quantidadeField);
        add(compradorLabel);
        add(compradorField);
        add(venderBtn);

        venderBtn.addActionListener((ActionEvent e) -> venderProduto());

        setVisible(true);
    }

    private void venderProduto() {
        String nome = nomeField.getText().trim();
        String marca = marcaField.getText().trim();
        String quantidadeStr = quantidadeField.getText().trim();
        String comprador = compradorField.getText().trim();

        if (nome.isEmpty() || marca.isEmpty() || quantidadeStr.isEmpty() || comprador.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            int quantidadeVendida = Integer.parseInt(quantidadeStr);
            Produto produto = ProdutoService.buscarProdutoPorNomeMarca(nome, marca);

            if (produto == null) {
                JOptionPane.showMessageDialog(this, "Produto não encontrado.");
                return;
            }

            if (produto.getQuantidade() < quantidadeVendida) {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente.");
                return;
            }

            ProdutoService.diminuirQuantidade(produto, quantidadeVendida);
            double valorTotal = produto.getPreco() * quantidadeVendida;
            Venda venda = new Venda(produto, quantidadeVendida, comprador, valorTotal);
            VendaService.salvarVenda(venda);

            JOptionPane.showMessageDialog(this, "Venda realizada com sucesso!");
            limparCampos();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantidade inválida.");
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        marcaField.setText("");
        quantidadeField.setText("");
        compradorField.setText("");
    }
}
