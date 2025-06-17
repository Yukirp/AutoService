package br.com.autoservice;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CadastrarProdutoFrame extends JFrame {

    private JTextField nomeField;
    private JTextField precoField;
    private JTextField marcaField;
    private JTextField quantidadeField; // Novo campo para quantidade
    private JTextArea compatibilidadeArea;
    private JTextArea descricaoArea;

    public CadastrarProdutoFrame() {
        setTitle("Cadastrar Produto");
        setSize(400, 500);  // Ajustei o tamanho da janela para acomodar todos os campos
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Títulos dos campos
        JLabel nomeLabel = new JLabel("Nome do Produto:");
        nomeLabel.setBounds(20, 20, 120, 25);

        nomeField = new JTextField();
        nomeField.setBounds(140, 20, 200, 25);

        JLabel precoLabel = new JLabel("Preço:");
        precoLabel.setBounds(20, 60, 120, 25);

        precoField = new JTextField();
        precoField.setBounds(140, 60, 200, 25);

        JLabel marcaLabel = new JLabel("Marca:");
        marcaLabel.setBounds(20, 100, 120, 25);

        marcaField = new JTextField();
        marcaField.setBounds(140, 100, 200, 25);

        // Compatibilidade (em vez de Descrição)
        JLabel compatibilidadeLabel = new JLabel("Compatibilidade:");
        compatibilidadeLabel.setBounds(20, 140, 120, 25);

        compatibilidadeArea = new JTextArea();
        compatibilidadeArea.setBounds(140, 140, 200, 80);
        compatibilidadeArea.setLineWrap(true);
        compatibilidadeArea.setWrapStyleWord(true);

        // Quantidade
        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setBounds(20, 230, 120, 25);

        quantidadeField = new JTextField();
        quantidadeField.setBounds(140, 230, 200, 25); // Novo campo para quantidade

        // Descrição
        JLabel descricaoLabel = new JLabel("Descrição:");
        descricaoLabel.setBounds(20, 270, 120, 25);

        descricaoArea = new JTextArea();
        descricaoArea.setBounds(140, 270, 200, 80);
        descricaoArea.setLineWrap(true);
        descricaoArea.setWrapStyleWord(true);

        JButton salvarBtn = new JButton("Salvar");
        salvarBtn.setBounds(140, 370, 120, 30);

        // Adicionando os componentes na tela
        add(nomeLabel);
        add(nomeField);
        add(precoLabel);
        add(precoField);
        add(marcaLabel);
        add(marcaField);
        add(compatibilidadeLabel);
        add(compatibilidadeArea);
        add(quantidadeLabel);
        add(quantidadeField); // Novo campo para quantidade
        add(descricaoLabel);
        add(descricaoArea);
        add(salvarBtn);

        salvarBtn.addActionListener((ActionEvent e) -> salvarProduto());

        setVisible(true);
    }

    private void salvarProduto() {
        String nome = nomeField.getText();
        String precoStr = precoField.getText();
        String marca = marcaField.getText();
        String compatibilidade = compatibilidadeArea.getText();
        String descricao = descricaoArea.getText();
        String quantidadeStr = quantidadeField.getText(); // Lendo a quantidade do campo

        if (nome.isBlank() || precoStr.isBlank() || marca.isBlank() || compatibilidade.isBlank() || descricao.isBlank() || quantidadeStr.isBlank()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            Double preco = Double.parseDouble(precoStr);
            int quantidade = Integer.parseInt(quantidadeStr); // Convertendo a quantidade para int

            Produto existente = ProdutoService.buscarProdutoPorNomeMarca(nome, marca);
            if (existente != null) {
                // Produto já existe, atualiza a quantidade
                int novaQuantidade = existente.getQuantidade() + quantidade;
                existente.setQuantidade(novaQuantidade);
                ProdutoService.atualizarProduto(existente);
                JOptionPane.showMessageDialog(this, "Produto já existia. Quantidade atualizada para " + novaQuantidade + ".");
            } else {
                // Produto novo
                ProdutoService.salvarProduto(nome, preco, marca, compatibilidade, descricao, quantidade);
                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            }

            limparCampos();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço ou quantidade inválidos.");
        }
    }

    private void limparCampos() {
        nomeField.setText("");
        precoField.setText("");
        marcaField.setText("");
        compatibilidadeArea.setText("");
        descricaoArea.setText("");
        quantidadeField.setText(""); // Limpando o campo de quantidade
    }
}
